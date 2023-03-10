package com.cetc.service433

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.RcvData
import android.util.Log

/**
* @des 433数据接收
* @time 2021/9/26 10:41 上午
*/
class Service433Receiver(private val receiverListener: ReceiverListener) :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val parser = it.getParcelableExtra<RcvData>("result")
            val content = parser.getrcvdata().toList()
            Log.e("Service433Receiver","content is $content")

            //命令字
            when (content[0]) {
                0x66.toByte() -> {
                    //位置信息上报
                    val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())

                    //计划中具体的任务ID
                    val taskId =
                        HexUtil.bcd2Str(content.subList(3, 4).toByteArray())


                    //用户ID
                    val userId =
                        HexUtil.formatHexString(content.subList(4, 20).toByteArray())
                            .toUpperCase()


                    //经度
                    val lng =
                        HexUtil.bytesToInt2(content.subList(20, 24).toByteArray(), 0)


                    //纬度
                    val lat = HexUtil.bytesToInt2(
                        content.subList(24, 28).toByteArray(), 0
                    )

                    //心率
                    val heartRate =
                        HexUtil.bytes1ToInt2(content.subList(28, 29).toByteArray(), 0)

                    val locationReceiver = ServiceLocationReceiver()
                    locationReceiver.userId = userId
                    locationReceiver.lat = (lat / 10000000.toDouble()).toDouble().toString()
                    locationReceiver.lng = (lng / 10000000.toDouble()).toDouble().toString()
                    locationReceiver.isSOS = false
                    locationReceiver.phoneId = pId
                    locationReceiver.projectId = taskId
                    locationReceiver.heartRate = heartRate.toString()
                    receiverListener.onLocationReceiver(locationReceiver)
                }



                0x64.toByte() -> {
                    //搜救指令上报
                    val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())

                    //用户ID
                    val userId =
                        HexUtil.formatHexString(content.subList(3, 19).toByteArray())
                            .toUpperCase()


                    //响应类型
                    val responseType =
                        HexUtil.formatHexString(content.subList(19, 20).toByteArray())
                            .toUpperCase()


                    //经度
                    val lng =
                        HexUtil.bytesToInt2(content.subList(20, 24).toByteArray(), 0)


                    //纬度
                    val lat = HexUtil.bytesToInt2(
                        content.subList(24, 28).toByteArray(), 0
                    )

                    //心率
                    val heartRate =
                        HexUtil.bytes1ToInt2(content.subList(28, 29).toByteArray(), 0)


                    val locationReceiver = ServiceLocationReceiver()
                    locationReceiver.userId = userId
                    locationReceiver.lat = (lat / 10000000.toDouble()).toDouble().toString()
                    locationReceiver.lng = (lng / 10000000.toDouble()).toDouble().toString()
                    locationReceiver.isSOS = true
                    locationReceiver.sosType = responseType
                    locationReceiver.phoneId = pId
                    locationReceiver.heartRate = heartRate.toString()
                    locationReceiver.responseType = responseType

                    receiverListener.onLocationReceiver(locationReceiver)
                }
            }



        }
    }
}