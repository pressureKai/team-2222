package com.cetc.serivcebeidou

import android.content.BDManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cetc.serivcebeidou.entity.BeiDouReceiverEntity
import com.cetc.serivcebeidou.entity.BeiDouSendMessageEntity
import com.cetc.serivcebeidou.entity.LastMessageIDDownloadEntity
import com.cetc.serivcebeidou.entity.MessageIDDownloadReceiverListEntity
import com.cetc.serivcebeidou.listener.OnServiceBeiDouReceiverListener
import com.cetc.serivcebeidou.receiver.ServiceBeiDouReceiver
import com.cetc.serivcebeidou.service.BeiDouSendMessageService
import com.lhzw.libcc.*
import org.litepal.LitePal
import java.lang.Exception
import java.nio.charset.Charset
import java.util.ArrayList
import org.litepal.LitePal.use

import org.litepal.LitePalDB




/**
 * @author xiezekai
 * @des 北斗管理类
 * @create time 2021/9/26 5:28 下午
 */
class ManagerBeiDou {
    companion object {

        const val RevBDAddress = "_RevBDAddress"
        const val RevBDInfo = "_RevBDInfo"
        const val RevBDType = "_RevBDType"
        const val RECEIVED_ID_DOWNLOAD = 11
        const val RECEIVED_ID_UPLOAD = 10
        const val SIG_RET = "strSignalInfoChanged"
        const val SIG_RET_INFO_0 = "SignalInfo0"
        const val SIG_RET_INFO_1 = "SignalInfo1"
        const val SIG_RET_INFO_2 = "SignalInfo2"
        const val SIG_RET_INFO_3 = "SignalInfo3"
        const val SIG_RET_INFO_4 = "SignalInfo4"
        const val SIG_RET_INFO_5 = "SignalInfo5"
        const val SIG_RET_INFO_6 = "SignalInfo6"
        const val SIG_RET_INFO_7 = "SignalInfo7"
        const val SIG_RET_INFO_8 = "SignalInfo8"
        const val SIG_RET_INFO_9 = "SignalInfo9"
        val SIG_INFO = arrayOf<String>(
            SIG_RET_INFO_0,
            SIG_RET_INFO_1,
            SIG_RET_INFO_2,
            SIG_RET_INFO_3,
            SIG_RET_INFO_4,
            SIG_RET_INFO_5,
            SIG_RET_INFO_6,
            SIG_RET_INFO_7,
            SIG_RET_INFO_8,
            SIG_RET_INFO_9
        )
        private var serviceBeiDouReceiver: ServiceBeiDouReceiver? = null

        //北斗平台ID
        private var beiDouTargetId = ""

        /**
         * @des 初始化北斗管理类，注册一个动态广播
         * @time 2021/9/26 5:34 下午
         */
        fun initService(
            context: Context,
            onServiceBeiDouReceiverListener: OnServiceBeiDouReceiverListener
        ) {
            //初始化数据库
//            LitePal.initialize(context)
//            LitePal.getDatabase()
            Handler(Looper.getMainLooper()).post {
//                val litePalDB = LitePalDB("beidou", 1)
//                litePalDB.addClassName(BeiDouReceiverEntity::class.java.name)
//                litePalDB.addClassName(BeiDouReceiverEntity::class.java.name)
//                litePalDB.addClassName(LastMessageIDDownloadEntity::class.java.name)
//                litePalDB.addClassName(MessageIDDownloadReceiverListEntity::class.java.name)
                val litePalDB = LitePalDB.fromDefault("beidou");
                use(litePalDB)
            }



            //启动接收广播
            serviceBeiDouReceiver = ServiceBeiDouReceiver(onServiceBeiDouReceiverListener)
            val intentFilter = IntentFilter();
            intentFilter.addAction("nci.hq.bd.service.RevSend")
            context.registerReceiver(serviceBeiDouReceiver, intentFilter)

            //启动Service
            val intent = Intent()
            intent.setClass(context, BeiDouSendMessageService::class.java)
            context.startService(intent)
        }


        /**
         * @des 注销广播，将监听器设置为空
         * @time 2021/9/27 9:14 上午
         */
        fun unRegisterReceiver(context: Context) {
            serviceBeiDouReceiver?.setListenerNull()
            context.unregisterReceiver(serviceBeiDouReceiver)
        }


        /**
         * @des 发送北斗短消息
         * @param content 消息内容
         * @param targetId 目标北斗平台ID
         * @param bdVersion 北斗通讯协议版本号（默认为0）
         * @param context 上下文 用于获取本地的bdCardNum
         * @param projectIndex 项目索引（默认为0）
         * @param groupingIndex 项目内分组索引（默认为0）
         * @time 2021/9/27 9:30 上午
         */
        fun sendBeiDouMessage(
            context: Context,
            content: String,
            targetId: String,
            bdVersion: Int? = 0,
            projectIndex: Int? = 0,
            groupingIndex: Int? = 0
        ) {
            val currentMessageId = getCurrentMessageId(targetId, context)
            val dataMessage = DataMessage(
                bdVersion!!, projectIndex!!,
                groupingIndex!!, currentMessageId + 1,
                System.currentTimeMillis(), content
            )
            val compressResult: CompressResult = LibccInterface.compressMessage(dataMessage)

            try {
                val bdManager =
                    context.getSystemService("beidou") as BDManager
                val messageList = compressResult.messageList
                for (value in messageList) {
                    send(bdManager, 0, 0, targetId.toString(), value)
                }
            } catch (e: Exception) {
                Log.e("ManagerBeiDou", "sendBeiDouMessage error is $e")
            }
        }


        /**
         * @des 已收ID上报
         * @time 2021/9/27 11:59 上午
         * @return
         */
        fun sendMessageIDUpload(context: Context) {
            val bdCardNumber = getBDCardNumber(context)
            if (bdCardNumber.isNotEmpty() && beiDouTargetId.isNotEmpty()) {
                val find = LitePal.where(
                    "address = ? and receiverAddress = ?",
                    beiDouTargetId, bdCardNumber
                ).find(LastMessageIDDownloadEntity::class.java)
                if (find.isNotEmpty()) {
                    //北斗平台最小待确认ID
                    val minSentID = find.last().minSentID
                    //北斗平台最大待确认ID
                    val maxSentID = find.last().maxSentID
                    //确认消息状态列表
                    val isReceivedList = ArrayList<Boolean>()
                    for (id in minSentID..maxSentID) {
                        //查询数据库中指定的消息接收实体（按接收地址，发送的北斗平台地址，消息ID）
                        val receiverEntity = LitePal.where(
                            "address = ? and  receiverAddress = ? and receiverMessageId = ?",
                            beiDouTargetId, bdCardNumber, id.toString()
                        ).find(BeiDouReceiverEntity::class.java)

                        if (isReceivedList.size < 480) {
                            if (receiverEntity.isNotEmpty()) {
                                isReceivedList.add(true)
                            } else {
                                isReceivedList.add(false)
                            }
                        }
                    }

                    val currentMessageId = getCurrentMessageId(beiDouTargetId, context)
                    //已收ID上报 消息头
                    val messageHead = MessageHead(
                        0,
                        1,
                        1,
                        3,
                        5,
                        true, currentMessageId + 1
                    )
                    messageHead.order = 10


                    val dataReceivedId = DataReceivedId()
                    dataReceivedId.startId = minSentID
                    dataReceivedId.isReceivedList = isReceivedList
                    dataReceivedId.minSentId =
                        getMinSentId(bdCardNumber, beiDouTargetId, currentMessageId)
                    dataReceivedId.maxSentId =
                        getMaxSentId(bdCardNumber, beiDouTargetId, currentMessageId)

                    val compressReceivedIdUpload =
                        LibccInterface.compressReceivedIdUpload(messageHead, dataReceivedId)

                    val bdManager =
                        context.getSystemService("beidou") as BDManager
                    val messageList = compressReceivedIdUpload.messageList
                    for (message in messageList) {
                        send(bdManager, 0, 0, beiDouTargetId, message)
                    }
                }
            }
        }


        /**
         * @des 获取手持机上已发送的最小待确认ID
         * @param bdCardNumber 本地手持北斗号
         * @param beiDouTargetId 目标北斗平台ID
         * @param currentMessageId 当前已发送最大ID
         * @time 2021/9/27 2:09 下午
         */
        private fun getMinSentId(
            bdCardNumber: String,
            beiDouTargetId: String,
            currentMessageId: Int
        ): Int {
            var minSentId = currentMessageId + 1
            //获取手持发送对应到对应平台已发送待确认最小ID
            val find = LitePal.where(
                "address = ? and receiverAddress = ? and sendSuccess = ?",
                bdCardNumber, beiDouTargetId, "1"
            ).find(BeiDouSendMessageEntity::class.java)
            if (find.isNotEmpty()) {
                minSentId = find.first().messageId
            }

            return minSentId
        }

        /**
         * @des 获取手持机上已发送的最大待确认ID
         * @param bdCardNumber 本地手持北斗号
         * @param beiDouTargetId 目标北斗平台ID
         * @param currentMessageId 当前已发送最大ID
         * @time 2021/9/27 2:11 下午
         */
        private fun getMaxSentId(
            bdCardNumber: String,
            beiDouTargetId: String,
            currentMessageId: Int
        ): Int {
            var maxSentId = currentMessageId + 1
            //获取手持发送对应到对应平台已发送待确认最小ID
            val find = LitePal.where(
                "address = ? and receiverAddress = ? and sendSuccess = ?",
                bdCardNumber, beiDouTargetId, "1"
            ).find(BeiDouSendMessageEntity::class.java)
            if (find.isNotEmpty()) {
                maxSentId = find.last().messageId
            }

            return maxSentId

        }

        /**
         * BD短信发送
         * @param comType :0 ~ 普通 1 ~ 紧急 0
         * @param comFont :0 ~ 代码 1 ~ 汉字 2 ~ 混发 2
         * @param des :收信方SIM卡地址 458893  八位
         * @param body :信息发送内容
         */
        fun send(
            mBDManager: BDManager,
            comType: Int,
            comFont: Int,
            des: String,
            body: String
        ) {
            try {
                val data = body.toByteArray(Charset.forName("GB2312"))
                var sendMessage = ""
                val len = data.size
                sendMessage = "\$BDSnd,$comType,$comFont,$des,$len,$body"

                //保存发送消息实体
                val dataId = LibccInterface.uncompressMessageHead(body).dataId
                val sendConfirmIdMessage = BeiDouSendMessageEntity()
                sendConfirmIdMessage.address = mBDManager.bdCardNumber
                sendConfirmIdMessage.receiverAddress = des
                sendConfirmIdMessage.messageId = dataId
                sendConfirmIdMessage.content = body
                sendConfirmIdMessage.time = System.currentTimeMillis().toString()
                sendConfirmIdMessage.sendMessage = sendMessage
                sendConfirmIdMessage.beiDouIsReceiver = false
                sendConfirmIdMessage.save()

            } catch (e: Exception) {
                Log.e("ManagerBeiDou", "send message error is $e")
            }
        }


        /**
         * @des 获取当前发送的消息ID
         * @param targetId 目标北斗平台ID
         * @time 2021/9/26 5:22 下午
         */
        private fun getCurrentMessageId(targetId: String, context: Context): Int {
            var messageId = 0
            val bdCardNumber = getBDCardNumber(context)
            if (bdCardNumber.isNotEmpty() && targetId.isNotEmpty()) {
                val find =
                    LitePal.where("address = ? and receiverAddress = ?", bdCardNumber, targetId)
                        .find(BeiDouSendMessageEntity::class.java)
                if (find.isNotEmpty()) {
                    messageId = find.last().messageId
                }
            }
            return messageId
        }


        /**
         * @des 获取本地北斗卡号
         * @time 2021/9/27 1:36 下午
         */
        private fun getBDCardNumber(context: Context): String {
            var bdCardNumber = ""
            try {
                //北斗平台发送的已收ID下发消息的接收地址 （本机地址）
                val bdManager =
                    context.getSystemService("beidou") as BDManager
                bdCardNumber = bdManager.bdCardNumber
            } catch (e: Exception) {
                Log.e(
                    "ManagerBeiDou",
                    "getBDCardNumber  error is $e"
                )
            }
            return bdCardNumber
        }


        /**
         * @des 设备绑定
         * @param beiDouTargetId 目标北斗平台ID
         * @param id 设备MAC地址
         * @time 2021/9/27 3:40 下午
         */
        private fun bindDevice(beiDouTargetId: String, context: Context, id: String) {
            val currentMessageId = getCurrentMessageId(beiDouTargetId, context)

            val messageHead = MessageHead(
                0,
                1,
                1,
                3,
                5,
                false,
                currentMessageId + 1
            )

            messageHead.order = 12
            val dataDeviceId = DataDeviceId(System.currentTimeMillis(), id)

            val compressResult = LibccInterface.compressDeviceid(messageHead, dataDeviceId)


            val bdManager =
                context.getSystemService("beidou") as BDManager
            val messageList = compressResult.messageList
            for (message in messageList) {
                send(bdManager, 0, 0, beiDouTargetId, message)
            }

        }


        /**
         * @des 设置北斗平台ID
         * @time 2021/9/27 1:37 下午
         */
        fun setBeiDouTargetNumber(id: String) {
            beiDouTargetId = id
        }
    }
}