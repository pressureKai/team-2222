package com.cetc.serivcebeidou.receiver

import android.content.BDManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cetc.serivcebeidou.ManagerBeiDou
import com.cetc.serivcebeidou.entity.BeiDouReceiverEntity
import com.cetc.serivcebeidou.entity.BeiDouSendMessageEntity
import com.cetc.serivcebeidou.entity.MessageIDDownloadReceiverListEntity
import com.cetc.serivcebeidou.entity.LastMessageIDDownloadEntity
import com.cetc.serivcebeidou.listener.OnServiceBeiDouReceiverListener
import com.lhzw.libcc.LibccInterface
import org.litepal.LitePal
import java.lang.Exception
import java.nio.charset.Charset

/**
 * @des 北斗消息接收广播
 * @time 2021/9/26 5:19 下午
 */
class ServiceBeiDouReceiver(
    private var onServiceBeiDouReceiverListener: OnServiceBeiDouReceiverListener ?= null
) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let { intent ->
            val byteArrayExtra = intent.getByteArrayExtra(ManagerBeiDou.RevBDInfo)
            var message = ""
            byteArrayExtra?.let {
                message = String(it, Charset.forName("gb2312"))
            }
            try {
                val type = intent.getStringExtra(ManagerBeiDou.RevBDType)
                type?.let {
                    when (it) {
                        "0" -> {
                            //指令
                            parseOrder(message, intent, p0)
                        }
                        "1" -> {
                            //北斗信号强度
                            parseSingle(intent)
                        }
                        "2" -> {
                            //短消息
                            parseBeiDouMessage(message)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("ServiceBeiDouReceiver", "on beiDouReceiver error is $e")
            }


            if (message.isNotEmpty()) {
                p0?.let {
                    saveMessage(p0, message, intent)
                }
            }
        }
    }



    /**
     * @des 保存接收到的北斗消息
     * @time 2021/9/26 6:45 下午
     */
    private fun saveMessage(context: Context, message: String, intent: Intent) {
        //北斗消息发送方地址
        val address = intent.getStringExtra(ManagerBeiDou.RevBDAddress)
        val beiDouReceiverEntity = BeiDouReceiverEntity()
        beiDouReceiverEntity.content = message
        beiDouReceiverEntity.address = address
        beiDouReceiverEntity.time = System.currentTimeMillis().toString()

        try {
            //解析消息ID
            val uncompressMessageHead =
                LibccInterface.uncompressMessageHead(message)
            beiDouReceiverEntity.receiverMessageId = uncompressMessageHead.dataId
        } catch (e: Exception) {
            Log.e("ServiceBeiDOuReceiver", "saveMessage parse dataId error is $e")
        }

        try {
            val bdManager =
                context.getSystemService("beidou") as BDManager
            beiDouReceiverEntity.receiverAddress =
                bdManager.bdCardNumber
        } catch (e: Exception) {
            Log.e("ServiceBeiDOuReceiver", "saveMessage parse get bdCardNumber error is $e")
        }
        beiDouReceiverEntity.save()
    }

    /**
     * @des 解析指令
     * @time 2021/9/26 6:36 下午
     */
    private fun parseOrder(message: String, intent: Intent, context: Context?) {
        try {
            val uncompressMessageHead =
                LibccInterface.uncompressMessageHead(message)
            uncompressMessageHead.dataId
            when (val order = uncompressMessageHead.order) {
                ManagerBeiDou.RECEIVED_ID_DOWNLOAD -> {
                    //已收ID下发
                    context?.let {
                        parseReceiverIdDownload(message, intent, context)
                    }
                }
                else -> {
                    //解析到具体的指令
                    onServiceBeiDouReceiverListener?.onServiceBeiDouReceiverListener(order.toString())
                }
            }
        } catch (e: Exception) {
            Log.e("ServiceBeiDouReceiver", "parseOrder error is $e")
        }
    }


    /**
     * @des 解析已收ID下发
     * @time 2021/9/26 7:34 下午
     */
    private fun parseReceiverIdDownload(message: String, intent: Intent, context: Context) {
        saveLastMessageIDDownload(message, intent, context)
        //对已发送的消息进行消息确认()
        val uncompressReceivedIdDownload =
            LibccInterface.uncompressReceivedIdDownload(message)
        //北斗平台返回的接收状态序列
        val receivedList = uncompressReceivedIdDownload.isReceivedList
        //接收状态序列的起始ID
        val startId = uncompressReceivedIdDownload.startId

        //发送方地址 （北斗平台地址）
        var address = ""
        try {
            address = intent.getStringExtra(ManagerBeiDou.RevBDAddress)
        } catch (e: Exception) {
            Log.e("ServiceBeiDouReceiver", "saveLastMessageIDDownload get send Address error is $e")
        }

        //北斗平台发送的已收ID下发消息的接收地址 （本机地址）
        var receiverAddress = ""
        try {
            val bdManager =
                context.getSystemService("beidou") as BDManager
            receiverAddress =
                bdManager.bdCardNumber
        } catch (e: Exception) {
            Log.e(
                "ServiceBeiDOuReceiver",
                "parseReceiverIdDownload parse get bdCardNumber error is $e"
            )
        }

        if(address.isNotEmpty() && receiverAddress.isNotEmpty()){
            for((index,value) in receivedList.withIndex()){
                //真实的消息ID
                val messageId = index + startId
                //获取由本机发送出去至对应平台的北斗消息，做数据发送状态的更新
                val find = LitePal.where("messageId = ? and address = ? and receiverAddress = ?",
                    messageId.toString(),
                    receiverAddress,
                    address)
                    .find(BeiDouSendMessageEntity::class.java)

                if(find.isNotEmpty()){
                    for(sendMessage in find){
                        sendMessage.beiDouIsReceiver = value
                        sendMessage.save()
                    }
                }
            }
        }

    }


    /**
     * @des 保存已收ID下发
     * @time 2021/9/27 8:42 上午
     */
    private fun saveLastMessageIDDownload(message: String, intent: Intent, context: Context) {
        val uncompressReceivedIdDownload =
            LibccInterface.uncompressReceivedIdDownload(message)
        val receivedList = uncompressReceivedIdDownload.isReceivedList
        val startId = uncompressReceivedIdDownload.startId
        val minSentId = uncompressReceivedIdDownload.minSentId
        val maxSentId = uncompressReceivedIdDownload.maxSentId

        //对已收ID下发进行存储（对于相同的address和receiverAddress只保留一条数据）
        val lastMessageIDDownloadEntity = LastMessageIDDownloadEntity()

        try {
            //发送方地址 （北斗平台地址）
            val address = intent.getStringExtra(ManagerBeiDou.RevBDAddress)
            lastMessageIDDownloadEntity.address = address
        } catch (e: Exception) {
            Log.e("ServiceBeiDouReceiver", "parseReceiverIdDownload get send Address error is $e")
        }


        try {
            //北斗平台发送的已收ID下发消息的接收地址 （本机地址）
            val bdManager =
                context.getSystemService("beidou") as BDManager
            lastMessageIDDownloadEntity.receiverAddress =
                bdManager.bdCardNumber
        } catch (e: Exception) {
            Log.e(
                "ServiceBeiDOuReceiver",
                "parseReceiverIdDownload parse get bdCardNumber error is $e"
            )
        }

        lastMessageIDDownloadEntity.maxSentID = maxSentId
        lastMessageIDDownloadEntity.minSentID = minSentId
        lastMessageIDDownloadEntity.startID = startId
        lastMessageIDDownloadEntity.save()


        if (lastMessageIDDownloadEntity.address.isNotEmpty()
            && lastMessageIDDownloadEntity.receiverAddress.isNotEmpty()
        ) {
            val find = LitePal.where(
                "receiverAddress = ? and address = ?",
                lastMessageIDDownloadEntity.address,
                lastMessageIDDownloadEntity.receiverAddress
            ).find(MessageIDDownloadReceiverListEntity::class.java)
            if (find.isNotEmpty()) {
                for (value in find) {
                    value.delete()
                }
            }
            for (value in receivedList) {
                val idDownloadMessageReceiverListEntity = MessageIDDownloadReceiverListEntity()
                idDownloadMessageReceiverListEntity.address = lastMessageIDDownloadEntity.address
                idDownloadMessageReceiverListEntity.receiverAddress =
                    lastMessageIDDownloadEntity.receiverAddress
                idDownloadMessageReceiverListEntity.isReceiver = value
                idDownloadMessageReceiverListEntity.save()
            }
        }
    }


    /**
     * @des 解析信号强度
     * @time 2021/9/26 6:37 下午
     */
    private fun parseSingle(intent: Intent) {
        val sig = intent.getStringExtra(ManagerBeiDou.SIG_RET)
        sig?.let {
            try {
                val position = it.toInt()
                val s = ManagerBeiDou.SIG_INFO[position]
                val value = intent.getStringExtra(s)
                onServiceBeiDouReceiverListener?.onServiceBeiDouReceiverListener(value)
            } catch (e: Exception) {
                Log.e("ServiceBeiDouReceiver", "onParseSingle error is $e")
            }
        }
    }

    /**
     * @des 解析北斗短消息
     * @time 2021/9/26 6:38 下午
     */
    private fun parseBeiDouMessage(message: String) {
        //北斗短消息字符串
        var bdMessage = ""
        try {
            val uncompressMessage = LibccInterface.uncompressMessage(message)
            bdMessage = uncompressMessage.message
            onServiceBeiDouReceiverListener?.onServiceBeiDouReceiverListener(bdMessage)
        } catch (e: Exception) {
            Log.e("ServiceBeiDouReceiver", "onParseBeiDouMessage error is $e")
        }
    }




    fun setListenerNull(){
        onServiceBeiDouReceiverListener = null
    }

}