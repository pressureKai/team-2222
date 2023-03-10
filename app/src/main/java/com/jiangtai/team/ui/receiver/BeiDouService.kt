package com.jiangtai.team.ui.receiver

import android.app.Service
import android.content.BDManager
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.cld.mapapi.model.LatLng
import com.jiangtai.team.application.App
import com.jiangtai.team.bean.BeiDouServiceBean
import com.jiangtai.team.bean.LocationReceiver
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.util.BeiDouUtils
import com.jiangtai.team.util.Preference
import org.greenrobot.eventbus.Subscribe

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArrayList


class BeiDouService : Service() {
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var latCenter: Double by Preference(Constant.LAT_CENTER, 0.toDouble())
    private var lngCenter: Double by Preference(Constant.LNG_CENTER, 0.toDouble())

    //最后一次发送北斗消息的时间
    private var lastSendTime: Long by Preference(Constant.LAST_SEND_TIME, 0L)
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")

    //是否空闲
    private var isLeisure = false

    private var serviceRunning = false

    //SOS消息发送队列
    private var mSendSOSMessageList: CopyOnWriteArrayList<LocationReceiver> = CopyOnWriteArrayList()

    //任务位置消息发送队列
    private var mSendTaskMessageList: CopyOnWriteArrayList<LocationReceiver> =
        CopyOnWriteArrayList()

    //接收位置列表
    private var receiverList: CopyOnWriteArrayList<LocationReceiver> = CopyOnWriteArrayList()

    override fun onCreate() {
        serviceRunning = true
        EventBus.getDefault().register(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        receiverList.clear()
        mSendSOSMessageList.clear()
        mSendTaskMessageList.clear()

        startSendBeiDouThread()


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        val registered = EventBus.getDefault().isRegistered(this)
        if (!registered) {
            EventBus.getDefault().register(this)
        }
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        EventBus.getDefault().unregister(this)
        return false
    }


    //发送普通位置上报方法
    //发送离散点位置上报方法
    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun mineReceiver(e: BeiDouServiceBean) {
        EventBus.getDefault().removeStickyEvent(e)
        e.locationReceiver?.let {
            if (it.isSOS) {
                //立即发送SOS
                //取消发送普通任务队列
                mSendSOSMessageList.add(it)
            } else {
                mSendTaskMessageList.add(it)
            }
        }
    }


    private fun sendBeiDouMessage(body: String) {
        Handler(Looper.getMainLooper()).post {
            App.getMineContext()?.let {
                lastSendTime = System.currentTimeMillis()
                val bdManager = it.getSystemService("beidou") as BDManager
                BeiDouUtils.sendMMS(bdManager, 0, 0, "0337005", body)
            }
        }
    }


    private fun startSendBeiDouThread() {
        Thread {
            while (true) {
                if (serviceRunning) {
                    if (System.currentTimeMillis() - lastSendTime > 62 * 1000) {
                        //发送北斗消息的时间间隔大于62秒
                        if (mSendSOSMessageList.size > 0) {
                            //SOS
                            try {
                                val first = mSendSOSMessageList.first()
                                val latLng = LatLng(latCenter, lngCenter)
                                val singlePoint = BeiDouUtils.singlePoint(
                                    latLng,
                                    locationList = arrayListOf(first)
                                )

                                for (value in singlePoint) {
                                    sendBeiDouMessage(value)
                                    mSendSOSMessageList.removeAt(0)
                                    Thread.sleep(62 * 1000)
                                }
                            } catch (e: Exception) {
                                LogUtils.e("send sos error is $e")
                            }
                        } else {
                            if (mSendTaskMessageList.size > 0) {
                                //Task
                                try {
                                    val shouldBeSendBeiDouLocation =
                                        getShouldBeSendBeiDouLocation(false, mSendTaskMessageList)
                                    //1.将所得应上传数组分为每10个位置点一组
                                    //2.如果将100个数组分成10组 （发送时间为10分钟）
                                    val splitList = splitList(shouldBeSendBeiDouLocation, 10)
                                    for ((index, value) in splitList.withIndex()) {
                                        val latLng = LatLng(latCenter, lngCenter)
                                        if (getCurrentTaskId().isNotEmpty()) {
                                            val taskPositionUpload = BeiDouUtils.taskPositionUpload(
                                                latLng,
                                                getCurrentTaskId().toInt(),
                                                value
                                            )
                                            for (body in taskPositionUpload) {
                                                sendBeiDouMessage(body)
                                                Thread.sleep(62 * 1000)
                                            }


                                            for (receiver in value) {
                                                if (receiver.time.isNotEmpty()) {
                                                    try {
                                                        removeTargetIdOnList(
                                                            receiver.userId,
                                                            receiver.time.toLong(),
                                                            mSendTaskMessageList
                                                        )
                                                        LogUtils.e("limit list size is ${mSendTaskMessageList.size}")
                                                    } catch (e: Exception) {
                                                        LogUtils.e("remove error is $e")
                                                    }
                                                }
                                            }
                                        }
                                    }


                                } catch (e: Exception) {
                                    LogUtils.e("sendTaskMessage error is $e")
                                }
                            } else {
                                //已收ID上报
                                lastSendTime = System.currentTimeMillis()
                                BeiDouUtils.receivedIdUpload()


                            }
                        }
                    }
                }
            }
        }.start()
    }


    private fun removeTargetIdOnList(
        userId: String,
        time: Long,
        list: CopyOnWriteArrayList<LocationReceiver>
    ) {
        try {
            val beRemoveIndex = ArrayList<Int>()
            for ((index, value) in list.withIndex()) {
                if (value.time.isNotEmpty()) {
                    try {
                        if (value.time.toLong() <= time.toLong() && userId == value.userId) {
                            beRemoveIndex.add(index)
                        }
                    } catch (e: Exception) {
                        beRemoveIndex.add(index)
                    }
                } else {
                    beRemoveIndex.add(index)
                }
            }


            if (beRemoveIndex.size > 0) {
                for ((indexInside, value) in beRemoveIndex.withIndex()) {
                    list.removeAt(value - indexInside)
                }
            }
        } catch (e: Exception) {
            LogUtils.e("remove error $e")
        }
    }


    private fun splitList(list: ArrayList<LocationReceiver>, size: Int)
            : ArrayList<ArrayList<LocationReceiver>> {
        val arrayList = ArrayList<ArrayList<LocationReceiver>>()
        var queueSize = size
        var inSideList: ArrayList<LocationReceiver> = ArrayList()
        for ((index, value) in list.withIndex()) {

            if (index == 0) {
                inSideList.clear()
            }

            if (index != list.size - 1) {
                if (index < queueSize) {
                    inSideList.add(value)
                } else {
                    val tempList = ArrayList<LocationReceiver>()
                    tempList.addAll(inSideList)
                    arrayList.add(tempList)
                    inSideList.clear()
                    queueSize += size
                    inSideList.clear()
                    inSideList.add(value)
                }
            } else {
                val tempList = ArrayList<LocationReceiver>()
                tempList.addAll(inSideList)
                arrayList.add(tempList)
            }

        }
        return arrayList
    }

    private fun getCurrentTaskId(): String {
        var taskId = ""
        val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
        if (find.size > 0) {
            taskId = find.first().taskId
        }
        return if (taskId.contains(".0")) taskId.replace(".0", "").trim() else taskId
    }


    private fun getPersonList(): ArrayList<Person> {
        val find = LitePal.where("projectId = ?", mCurrentMajorProject)
            .find(Project::class.java)
        val arrayList = ArrayList<Person>()
        if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
            val peopleId = find.first().peopleId
            val split = peopleId.split(",")
            for (value in split) {
                LogUtils.e("value is ${value.toUpperCase()}")
                val find1 =
                    LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                arrayList.addAll(find1)
            }
        }
        return arrayList
    }


    private fun getShouldBeSendBeiDouLocation(
        isSOS: Boolean,
        receiverList: CopyOnWriteArrayList<LocationReceiver>
    ): ArrayList<LocationReceiver> {
        val locationList = ArrayList<LocationReceiver>()
        val orderByTime = orderByTime(receiverList)
        for (value in orderByTime) {
            if (isSOS && value.isSOS) {
                if (isSameId(value.userId, locationList)) {
                    removeTargetUser(value.userId, locationList)
                }
                locationList.add(value)
            } else if (!isSOS && !value.isSOS) {
                if (isSameId(value.userId, locationList)) {
                    removeTargetUser(value.userId, locationList)
                }
                locationList.add(value)
            }
        }
        return locationList
    }


    /**
     * @des 移除列表中的目标用户
     * @time 2021/8/25 5:03 下午
     */
    private fun removeTargetUser(targetUserId: String, locationList: ArrayList<LocationReceiver>) {
        try {
            val beRemoveIndexList = ArrayList<Int>()
            for ((index, value) in locationList.withIndex()) {
                if (value.userId == targetUserId) {
                    beRemoveIndexList.add(index)
                }
            }
            if (beRemoveIndexList.size > 0) {
                for ((indexInside, value) in beRemoveIndexList.withIndex()) {
                    locationList.removeAt(value - indexInside)
                }
            }
        } catch (e: Exception) {
            LogUtils.e("BeiDouService removeTargetUser error is $e")
        }

    }

    //判断数组中是否存在相同的UserId
    private fun isSameId(
        targetUserId: String,
        locationList: ArrayList<LocationReceiver>
    ): Boolean {
        var sameId = false
        for (value in locationList) {
            if (value.userId == targetUserId) {
                sameId = true
                break
            }
        }
        return sameId
    }


    /**
     * @des 按时间排序
     * @time 2021/8/25 4:49 下午
     */
    private fun orderByTime(list: CopyOnWriteArrayList<LocationReceiver>): CopyOnWriteArrayList<LocationReceiver> {
        //冒泡排序所用的临时变量
        var tempLocationReceiver: LocationReceiver? = null
        //外层遍历（控制循环的次数）
        for (index in 0.until(list.size)) {
            //内层遍历
            for (inSideIndex in 0.until(list.size - index - 1)) {
                try {
                    if (list[inSideIndex].time.toLong() > list[inSideIndex + 1].time.toLong()) {
                        tempLocationReceiver = list[inSideIndex + 1]
                        list[inSideIndex + 1] = list[inSideIndex]
                        list[inSideIndex] = tempLocationReceiver
                    }
                } catch (e: Exception) {

                }
            }
        }

        return list
    }


    override fun onDestroy() {
        serviceRunning = false
        super.onDestroy()
    }

}