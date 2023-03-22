package com.jiangtai.count.ui.receiver

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.cld.mapapi.model.LatLng
import com.cld.mapapi.util.DistanceUtil
import com.jiangtai.count.bean.LocationReceiver
import com.jiangtai.count.bean.Person
import com.jiangtai.count.bean.Project
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.CommandUtils
import com.jiangtai.count.util.Preference
import org.litepal.LitePal
import java.lang.Exception
import kotlin.math.abs

class CheckDistanceService : Service() {
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var lastRequestTime: Long by Preference(Constant.LAST_REQUEST_TIME, 0L)
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startCheckDistanceThread()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startCheckDistanceThread() {

        Handler(Looper.getMainLooper()).postDelayed({
              CommandUtils.shootEnd(this,0)
            CommandUtils.shootEnd(this,phoneId.toInt())
        },100)
        Thread {
            var isRun = false
            while (mIsStart) {
                if (mCurrentMajorProject.isNotEmpty() && !isRun) {
                    isRun = true
                    //获取当前任务的所有人员
                    Thread.sleep(200)
                    if (System.currentTimeMillis() - lastRequestTime > 15 * 1000L) {
                        lastRequestTime = System.currentTimeMillis()
                        val personList = getPersonList()
                        if (personList.isNotEmpty()) {
                            requestWatchLocation(personList.first())
                            Handler(Looper.getMainLooper()).postDelayed({
                                CommandUtils.shootStart(applicationContext,0,true)
                                requestDeviceLocation(personList.first())
                            },300)
                        } else {
                            LogUtils.e("CheckDistanceService -> 当前任务暂无人员")
                        }
                    }

                    Thread.sleep(5 * 1000L)
                    checkDistanceAndNotifyWatch()
                    isRun = false
                } else {
                    LogUtils.e("CheckDistanceService -> 请选择当前任务")
                }
            }

            try {
                lastRequestTime = 0
                Thread.interrupted()
                Thread.sleep(300000000)
            } catch (e: Exception) {
                LogUtils.e("CheckDistanceService -> stopThread error is $e")
            }


        }.start()
    }

    /**
     * @des 检查距离 -> 通知腕表的设备离位
     * @time 2021/10/22 10:10 上午
     */
    private fun checkDistanceAndNotifyWatch() {
        LogUtils.e("CheckDistanceService -> checkDistanceAndNotifyWatch")
        val personList = getPersonList()
        //获取当前任务所有人员列表
        for (value in personList) {
            //从数据库获取最后一次腕表的位置信息上报
            val find = LitePal.where(
                "userId = ?",
                value.personId.toUpperCase()
            ).find(LocationReceiver::class.java)


            if (find.isNotEmpty()) {
                if (value.takeDevice == "1" || value.takeDevice == "1.0") {
                    if (value.deviceListLocal.isNotEmpty()) {
                        val split = value.deviceListLocal.split(",")
                        for (device in split) {
                            //获取设备位置列表
                            val device = LitePal.where(
                                "userId = ?",
                                device.toUpperCase()
                            ).find(LocationReceiver::class.java)

                            if (device.isNotEmpty()) {
                                //最后一次腕表上传的位置
                                val last = find.last()
                                //设备最后一次上传的位置
                                val lastDevice = device.last()

                                val watchLatLng = LatLng(last.lat.toDouble(), last.lng.toDouble())
                                val deviceLatLng =
                                    LatLng(lastDevice.lat.toDouble(), lastDevice.lng.toDouble())


                                var watchAcceptTime = 0L
                                var deviceAcceptTime = 0L

                                if (last.time.isNotEmpty()) {
                                    watchAcceptTime = last.time.toLong()
                                }

                                if (lastDevice.time.isNotEmpty()) {
                                    deviceAcceptTime = lastDevice.time.toLong()
                                }

                                //腕表与装备100分钟内的有效值
                                //腕表与装备均在最后200分钟内上报过位置
                                //腕表与装备均为有效值
                                if (((abs(watchAcceptTime - deviceAcceptTime) < 6000 * 1000 &&
                                            abs(System.currentTimeMillis() - watchAcceptTime) < 12000 * 1000 &&
                                            abs(System.currentTimeMillis() - deviceAcceptTime) < 12000 * 1000) ||
                                            watchAcceptTime == 0L || deviceAcceptTime == 0L)
                                ) {
                                    //限制比较的两个目标ID，是否在60s内获取的时间
                                    if (!(watchLatLng.latitude == 0.toDouble()
                                                || watchLatLng.longitude == 0.toDouble()
                                                || deviceLatLng.latitude == 0.toDouble()
                                                || deviceLatLng.longitude == 0.toDouble())
                                    ) {
                                        //过滤掉不正确的经纬度，计算距离
                                        LogUtils.e("CheckDistanceService -> 手表 lat${watchLatLng.latitude} id is ${last.userId}")
                                        LogUtils.e("CheckDistanceService -> 手表 lng${watchLatLng.longitude} id is ${last.userId}")

                                        LogUtils.e("CheckDistanceService -> device lat${deviceLatLng.latitude} id is ${lastDevice.userId}")
                                        LogUtils.e("CheckDistanceService -> device lng${deviceLatLng.longitude} id is ${lastDevice.userId}")
                                        val distance =
                                            DistanceUtil.getDistance(watchLatLng, deviceLatLng)
                                        if (distance > 10) {
                                            //发送离位命令
                                            application.applicationContext?.let {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    CommandUtils.actionCommand2(
                                                        this,
                                                        phoneId.toInt(),
                                                        getCurrentTaskId(),
                                                        mCurrentMajorProject,
                                                        value.personId,
                                                    "装备离位"
                                                    )
                                                }, 2000)
                                            }
                                            Thread.sleep(10 * 1000)
                                            LogUtils.e("CheckDistanceService -> 离位 $distance")
                                        } else {
                                            LogUtils.e("CheckDistanceService -> distance $distance")
                                        }
                                    } else {
                                        LogUtils.e("CheckDistanceService -> 经纬度坐标不正确")
                                    }
                                } else {
                                    LogUtils.e("CheckDistanceService -> 超时了")
                                }
                            } else {
                                LogUtils.e("CheckDistanceService -> 无装备列表")
                            }
                        }
                    }
                } else {
                    LogUtils.e("CheckDistanceService -> 未携带装备")
                }

            }
        }
    }


    private fun getCurrentTaskId(): String {
        var taskId = ""
        val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
        if (find.size > 0) {
            taskId = find.first().taskId
        }
        return taskId
    }

    /**
     * @des 请求腕表位置 --> 不可同时发送多个请求指令 --> 会造成阻塞
     * @time 2021/10/22 9:23 上午
     */
    private fun requestWatchLocation(person: Person) {
        LogUtils.e("CheckDistanceService -> requestWatchLocation")
        val personId = person.personId
        if (personId.isNotEmpty()) {
            application.applicationContext?.let {
                try {
                    var taskID = 0
                    taskID = if (mCurrentMajorProject.contains(".")) {
                        mCurrentMajorProject.toFloat().toInt()
                    } else {
                        mCurrentMajorProject.toInt()
                    }
                    CommandUtils.inquiryDeviceCommand2(it, phoneId.toInt(), taskID)
                    Thread.sleep(300)
                } catch (e: Exception) {
                    LogUtils.e("CheckDistanceService -> requestWatchLocation -> error is $e")
                }
            }
        }
    }

    /**
     * @des 请求设备位置
     * @time 2021/10/22 9:24 上午
     */
    private fun requestDeviceLocation(person: Person) {
        LogUtils.e("CheckDistanceService -> requestDeviceLocation")
        val deviceListLocal = person.deviceListLocal
        val split = deviceListLocal.split(",")

        if (split.isNotEmpty()) {
            try {
                if (phoneId.isNotEmpty()) {
                    application.applicationContext?.let {
                        CommandUtils.inquiryDeviceCommand2(it, 0)
                        Thread.sleep(300)
                    }
                } else {
                    LogUtils.e("CheckDistanceService -> requestDeviceLocation -> 设置信道ID")
                }
            } catch (e: Exception) {
                LogUtils.e("CheckDistanceService -> requestDeviceLocation -> error is $e")
            }
        } else {
            LogUtils.e("CheckDistanceService -> requestDeviceLocation -> 暂无装备列表")
        }

    }

    /**
     * @des 获取当前任务的所有人员
     * @time 2021/10/22 9:06 上午
     */
    private fun getPersonList(): ArrayList<Person> {
        val find = LitePal.where("projectId = ?", mCurrentMajorProject)
            .find(Project::class.java)
        val arrayList = ArrayList<Person>()
        if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
            val peopleId = find.first().peopleId
            val split = peopleId.split(",")
            for (value in split) {
                //   LogUtils.e("value is ${value.toUpperCase()}")
                val find1 =
                    LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                arrayList.addAll(find1)
            }
        }
        return arrayList
    }

    companion object {
        private var mIsStart: Boolean by Preference(Constant.START_CHECK_DISTANCE, false)
        fun startCurrentThread(context: Context) {
            try {
                mIsStart = true
                val intent = Intent()
                intent.setClass(context, CheckDistanceService::class.java)
                context.startService(intent)
            } catch (e: Exception) {
                LogUtils.e("CheckDistanceService startCurrentThreadError is $e")
            }
        }

        fun stopCurrentThread() {
            mIsStart = false
        }
    }
}