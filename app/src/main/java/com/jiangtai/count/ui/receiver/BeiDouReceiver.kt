package com.jiangtai.count.ui.receiver

import android.content.BDManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.application.App
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.CommandUtils
import com.jiangtai.count.util.Preference
import com.jiangtai.count.util.ToJsonUtil
import com.lhzw.libcc.LibccInterface
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.lang.Exception
import java.nio.charset.Charset

/**
* @author xiezekai
* @des  北斗信息接收广播
* @create time 2021/8/26 2:06 下午
*/
class BeiDouReceiver : BroadcastReceiver() {
    private var minConfirmId: Int by Preference(Constant.MIN_CONFIRM_ID, -1)
    private var maxConfirmId: Int by Preference(Constant.MAX_CONFIRM_ID, -1)
    private var startID: Int by Preference(Constant.START_ID, 1)
    private var sortStartID:Int by Preference(Constant.SORT_START_ID,1)
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var eleMode: Boolean by Preference(Constant.ELE_MODE, false)
    private var eleDistance: Int by Preference(Constant.ELE_DISTANCE, 0)
    private var eleStartTime: Long by Preference(Constant.ELE_START_TIME, 0L)
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")
    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let { intent ->
            val byteArrayExtra = intent.getByteArrayExtra(Constant.RevBDInfo)
            //解压内容
            var str = ""
            byteArrayExtra?.let {
                str = String(it, Charset.forName("gb2312"))
            //    ToastUtils.showShort("收到北斗消息 :$str")
            }

            //0 ：指令  1： 北斗信号强度 2： 短消息
            val type = intent.getStringExtra(Constant.RevBDType)
            if (type != null) {
                var message = "BeiDou Message type is $type"
                when (type) {
                    "0" -> {
                        val uncompressMessageHead =
                            LibccInterface.uncompressMessageHead(str)
                        val order = uncompressMessageHead.order

                        if (str.isNotEmpty()) {
                            message += "order is $order " +
                                    "dataId is ${uncompressMessageHead.dataId} " +
                                    "currentDataID is${startID}"

                            try {
                                if (order == Constant.RECEIVED_ID_DOWNLOAD) {
                                    message += addConfirmId(str)
                                    message += "已收ID下发"
                                    val beiDouReceiveBean = BeiDouReceiveBean()
                                    beiDouReceiveBean.content = message
                                    beiDouReceiveBean.time = System.currentTimeMillis().toString()
                                    beiDouReceiveBean.save()
                                    EventBus.getDefault().postSticky(beiDouReceiveBean)
                                } else {
                                    var dipper = ""
                                    val dipperNum = intent.getStringExtra(Constant.RevBDAddress)
                                    dipperNum?.let {
                                        dipper = it
                                    }
                                    if (dipper.isNotEmpty()) {
                                        val beiDouReceiveBean = BeiDouReceiveBean()
                                        beiDouReceiveBean.content =
                                            "${uncompressMessageHead.dataId.toString()} " +
                                                    "address is $dipper " +
                                                    "content is $str"
                                        beiDouReceiveBean.time =
                                            System.currentTimeMillis().toString()
                                        beiDouReceiveBean.save()
                                        EventBus.getDefault().postSticky(beiDouReceiveBean)

                                        try {
                                            //保存除确认消息之外的消息
                                            val receiverConfirmIdMessage =
                                                ReceiverConfirmIdMessage()
                                            val bdManager =
                                                p0!!.getSystemService("beidou") as BDManager
                                            receiverConfirmIdMessage.address =
                                                bdManager.bdCardNumber
                                            receiverConfirmIdMessage.sendAddress = dipper
                                            receiverConfirmIdMessage.dataId =
                                                uncompressMessageHead.dataId.toString()
                                            receiverConfirmIdMessage.save()

                                            //最小已发送ID赋值
                                            val id = uncompressMessageHead.dataId.toString()
                                            minConfirmId = id.toInt()
                                        } catch (e: Exception) {
                                            LogUtils.e("parse beidou message error is $e")
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                             //   LogUtils.e("error is $e")
                            }
                        }

                        //平台下发命令
                        when (order) {
                            1 -> {
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        App.getMineContext()?.let {
                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,
                                                "开饭"
                                            )
                                        }
                                    } else {
                                        ToastUtils.showShort("请选择训练任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                            2 -> {
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        App.getMineContext()?.let {
                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,
                                                "集合"
                                            )
                                        }
                                    } else {
                                        ToastUtils.showShort("请选择训练任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                            3 -> {
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        App.getMineContext()?.let {
                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,
                                                "休息"
                                            )
                                        }
                                    } else {
                                        ToastUtils.showShort("请选择训练任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                        }

                    }
                    "1" -> {
                        val sig = intent.getStringExtra(Constant.SIG_RET)
                        sig?.let {
                            try {
                                val position = it.toInt()
                                val s = Constant.SIG_INFO[position]
                                val value = intent.getStringExtra(s)
                                val beiDouReceiveBean = BeiDouReceiveBean()
                                beiDouReceiveBean.content = "信号强度为: $value"
                                beiDouReceiveBean.time = System.currentTimeMillis().toString()
                                beiDouReceiveBean.save()
                            } catch (e: Exception) {
                                LogUtils.e("BeiDouReceiver $e")
                            }
                        }
                    }
                    "2" -> {
                        var dipper = ""
                        val dipperNum = intent.getStringExtra(Constant.RevBDAddress)
                        dipperNum?.let {
                            dipper = it
                            val beiDouReceiveBean = BeiDouReceiveBean()
                            beiDouReceiveBean.content = dealBDMessage(str, dipper, p0!!)
                            beiDouReceiveBean.time = System.currentTimeMillis().toString()
                            beiDouReceiveBean.save()
                            EventBus.getDefault().postSticky(beiDouReceiveBean)
                        }
                    }
                    else -> {
                        val beiDouReceiveBean = BeiDouReceiveBean()
                        beiDouReceiveBean.content = message + "unKnow message"
                        beiDouReceiveBean.time = System.currentTimeMillis().toString()
                        beiDouReceiveBean.save()
                        EventBus.getDefault().postSticky(beiDouReceiveBean)
                    }
                }
            }
        }
    }


    /**
     * @des 处理北斗短消息
     * @time 2021/8/18 10:43 上午
     */
    private fun dealBDMessage(body: String, sendAddress: String, p0: Context): String {
        var message = ""
        try {
            val uncompressMessage = LibccInterface.uncompressMessage(body)
            val receiverConfirmIdMessage = ReceiverConfirmIdMessage()
            val bdManager = p0.getSystemService("beidou") as BDManager
            receiverConfirmIdMessage.address = bdManager.bdCardNumber
            receiverConfirmIdMessage.sendAddress = sendAddress
            receiverConfirmIdMessage.dataId = uncompressMessage.dataId.toString()
            receiverConfirmIdMessage.save()

            //消息内容
            message = uncompressMessage.dataId.toString() + uncompressMessage.message

            //最小已发送ID赋值
            val id = ""
            minConfirmId = id.toInt()


            //短消息下发处理
            val message1 = uncompressMessage.message
            //json转换成实体类
            val mqttBean = ToJsonUtil.fromJson(message1, MqttBean::class.java)


            for (value in mqttBean.inputs) {
                try {
                    if (value.name == "text") {
                        val json = value.value
                        val bean = ToJsonUtil.fromJson(json, MqttBean.bean::class.java)

                        if (bean.type == "2") {
                            if (phoneId.isNotEmpty()) {
                                if (mCurrentMajorProject.isNotEmpty()) {
                                    var custom = ""
                                    if (bean.content.isNotEmpty()) {
                                        custom = bean.content
                                        ToastUtils.showShort(custom)
                                        App.getMineContext()?.let {
                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,
                                                custom
                                            )
                                        }
                                    }
                                } else {
                                    ToastUtils.showShort("请选择训练任务")
                                }
                            } else {
                                ToastUtils.showShort("请设置手持机ID")
                            }
                        } else {
                            eleMode = true
                            try {
                                eleDistance = bean.values.toInt()
                                eleStartTime = System.currentTimeMillis()
                                EventBus.getDefault().postSticky(EleBean())
                            } catch (e: Exception) {

                            }
                        }

                    }
                } catch (e: Exception) {

                }
            }

        } catch (e: Exception) {

        }


        return message
    }


    /**
     * @des 已收ID下发处理
     * @time 2021/8/24 4:35 下午
     */
    private fun addConfirmId(str: String): String {
        val uncompressReceivedIdDownload =
            LibccInterface.uncompressReceivedIdDownload(str)
        val receivedList = uncompressReceivedIdDownload.isReceivedList
        val startId = uncompressReceivedIdDownload.startId
        val minSentId = uncompressReceivedIdDownload.minSentId
        val maxSentId = uncompressReceivedIdDownload.maxSentId



        LogUtils.e("minSentId $minSentId")
        LogUtils.e("maxSentId $maxSentId")
        LogUtils.e("startId $startId")
        LogUtils.e("receivedList size is ${receivedList.size}")


        //北斗平台的已发送最小待确认ID
        minConfirmId = minSentId.toInt()
        //北斗平台的已发送最大待确认ID
        maxConfirmId = maxSentId.toInt()
        sortStartID = startId
        //获取数据库中大于等于起始ID的集合（startId == 0 ~ endId == 10）
        //11条数据
        //0，1，2，3，4，5，6，7，8，9，10，11
        val find = LitePal.where(
            "dataId >= ? and dataId <= ?",
            startId.toString(),
            maxSentId.toString()
        ).find(SendConfirmIdMessage::class.java)


        for ((index, value) in receivedList.withIndex()) {
            //假设每一个ID都存在的情况
            val i = index + startId
            for (message in find) {
                //id符合条件的进行状态改变
                if (i.toString() == message.dataId) {
                    if (value) {
                        message.sendState = "3"
                    } else {
                        message.sendState = "4"
                    }
                    message.save()
                }
            }
        }

        return "minSentId $minSentId \nmaxSentId $maxSentId \nstartId $startId\n\"receivedList size is ${receivedList.size}\""
    }


    private fun getCurrentTaskId(): String {
        var taskId = ""
        val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
        if (find.size > 0) {
            taskId = find.first().taskId
        }
        return taskId
    }


}