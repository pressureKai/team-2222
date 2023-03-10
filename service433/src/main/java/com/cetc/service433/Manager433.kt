package com.cetc.service433

import android.content.Context
import android.content.IntentFilter
import android.content.LoRaManager
import android.content.SndData
import android.util.Log
import java.lang.Exception
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @des 433服务管理
 * @time 2021/9/26 10:24 上午
 */
class Manager433 {
    companion object {
        var service433Receiver: Service433Receiver? = null
        fun send(sendEntity: SendEntity, context: Context, sendListener: SendListener?) {
            val loraManager:LoRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            when (sendEntity.sendType) {
                SendEntity.COMMAND -> {
                    //指令下发
                    command(sendEntity, loraManager, sendListener)
                }
                SendEntity.TASK -> {
                    //任务管理
                    task(sendEntity, loraManager, sendListener)
                }
                SendEntity.WATCH -> {
                    //手持腕表设置
                    watch(sendEntity, loraManager, sendListener)
                }
                SendEntity.LOCATION -> {
                    //任务位置上报
                    requestLocation(sendEntity, loraManager, sendListener)
                }
                SendEntity.SOS -> {
                    //SOS位置上报（发送命令腕表进入SOS状态）
                    sosLocation(sendEntity, loraManager, sendListener)
                }
                -1 -> {
                    //未知命令
                }
            }
        }

        /**
         * @des 下发任务
         * @time 2021/9/26 11:04 上午
         */
        private fun task(
            sendEntity: SendEntity,
            loraManager: LoRaManager,
            sendListener: SendListener?
        ) {
            try {
                val arrayList = ArrayList<Byte>()
                val bytes = arrayOf(
                    0xE8.toByte(),
                    0xEB.toByte(),
                    0x54.toByte(),
                    110.toByte(),
                    sendEntity.phoneId.toByte(),
                    sendEntity.taskEntity.taskId.toByte()
                )

                arrayList.clear()
                arrayList.addAll(bytes)


                //计划名称字节字符串数组
                val taskNameByteArray =
                    stringContentToCustomSize(sendEntity.taskEntity.taskName, 20)
                arrayList.addAll(taskNameByteArray)

                //项目(任务)ID
                arrayList.add(parseToInt(sendEntity.taskEntity.projectId).toByte())


                //任务名称字节字符串数组
                val projectNameByteArray =
                    stringContentToCustomSize(sendEntity.taskEntity.projectName, 20)
                arrayList.addAll(projectNameByteArray)


                //任务内容字节字符串数组
                val projectContentByteArray =
                    stringContentToCustomSizeSpecial(sendEntity.taskEntity.projectContent, 54)
                arrayList.addAll(projectContentByteArray)


                //开始时间字节字符串数组
                val startTimeByteArray = getTimeT016(sendEntity.taskEntity.startTime.toLong())
                arrayList.addAll(startTimeByteArray)


                //结束时间字节字符串数组
                val endTimeByteArray = getTimeT016(sendEntity.taskEntity.endTime.toLong())
                arrayList.addAll(endTimeByteArray)

                var locationByte = 0x00.toByte()
                if (sendEntity.taskEntity.isLocation) {
                    locationByte = 0x01.toByte()
                }
                arrayList.add(locationByte)

                //crc 校验位
                arrayList.add(0x00.toByte())
                arrayList.add(0x00.toByte())

                //发送命令  5  arrayList.size  5  arrayList.size - 2  3
                val calculateCrc =
                    CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                arrayList[arrayList.size - 2] = shortToBytes[0]
                arrayList[arrayList.size - 1] = shortToBytes[1]

                val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                loraManager.sendContent(sndData)

                showMessage(arrayList)
                sendListener?.sendFinish("")
            } catch (e: Exception) {
                sendListener?.sendFinish(e.toString())
            }
        }


        /**
         * @des 下发命令
         * @time 2021/9/26 11:04 上午
         */
        private fun command(
            sendEntity: SendEntity,
            loraManager: LoRaManager,
            sendListener: SendListener?
        ) {

            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x56.toByte(),
                7.toByte(),
                sendEntity.phoneId.toByte(),
                parseToInt(sendEntity.taskEntity.taskId).toByte(),
                parseToInt(sendEntity.taskEntity.projectId).toByte()
            )

            arrayList.addAll(bytes)
            //命令内容字符串字节数组
            val contentByteArrayList = stringContentToCustomSize(sendEntity.content, 4)
            arrayList.addAll(contentByteArrayList)
            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())


            //发送命令
            val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loraManager.sendContent(sndData)
            showMessage(arrayList)
            sendListener?.sendFinish("")
        }


        /**
         * @des 请求全员位置上报
         * @time 2021/9/26 11:10 上午
         */
        private fun requestLocation(
            sendEntity: SendEntity,
            loraManager: LoRaManager,
            sendListener: SendListener?
        ) {
            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x58.toByte(),
                18.toByte(),
                parseToInt(sendEntity.phoneId).toByte(),
                parseToInt(sendEntity.taskEntity.projectId).toByte()
            )
            arrayList.addAll(bytes)


            val i = 16
            for (index in 0.until(i)) {
                arrayList.add(0x00.toByte())
            }

            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())

            //发送命令
            val calculateCrc =
                CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loraManager.sendContent(sndData)

            sendListener?.sendFinish("")
            showMessage(arrayList)
        }


        /**
         * @des 请求全员位置上报
         * @time 2021/9/26 11:10 上午
         */
        private fun sosLocation(
            sendEntity: SendEntity,
            loraManager: LoRaManager,
            sendListener: SendListener?
        ) {
            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x57.toByte(),
                17.toByte(),
                parseToInt(sendEntity.phoneId).toByte(),
            )
            arrayList.addAll(bytes)

            val userIdBytes = HexUtil.decodeHex(sendEntity.userId.toCharArray())
            for(value in userIdBytes){
                arrayList.add(value.toByte())
            }

            if(userIdBytes.size < 16){
                val i = 16 - userIdBytes.size
                for(index in 0.until(i)){
                    arrayList.add(0x00.toByte())
                }
            }

            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())

            //发送命令
            val calculateCrc =
                CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loraManager.sendContent(sndData)

            sendListener?.sendFinish("")
            showMessage(arrayList)
        }


        /**
         * @des 手持腕表设置
         * @time 2021/9/26 11:15 上午
         */
        private fun watch(
            sendEntity: SendEntity,
            loraManager: LoRaManager,
            sendListener: SendListener?
        ) {
            try {
                val arrayList = ArrayList<Byte>()
                val bytes = arrayOf(
                    0xE8.toByte(),
                    0xEB.toByte(),
                    0x65.toByte(),
                    17.toByte()
                )
                arrayList.addAll(bytes)
                val userIdBytes = HexUtil.decodeHex(sendEntity.userId.toCharArray())
                for (value in userIdBytes) {
                    arrayList.add(value.toByte())
                }

                if (userIdBytes.size < 16) {
                    val i = 16 - userIdBytes.size
                    for (index in 0.until(i)) {
                        arrayList.add(0x00.toByte())
                    }
                }

                arrayList.add(parseToInt(sendEntity.content).toByte())
                //crc 校验位
                arrayList.add(0x00.toByte())
                arrayList.add(0x00.toByte())


                //发送命令
                val calculateCrc =
                    CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                arrayList[arrayList.size - 2] = shortToBytes[0]
                arrayList[arrayList.size - 1] = shortToBytes[1]

                val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                loraManager.sendContent(sndData)

                sendListener?.sendFinish("")

                showMessage(arrayList)
            } catch (e: Exception) {
                sendListener?.sendFinish(e.toString())
            }

        }


        private fun parseToInt(string: String): Int {
            var int = 0
            if (string.isEmpty()) {
                return int
            }
            try {
                int = Integer.parseInt(string)
            } catch (e: Exception) {
                try {
                    int = string.toFloat().toInt()
                } catch (e: Exception) {

                }

            }
            return int
        }


        /**
         * @des 将字符串内容转换成指定大小的字节数组
         * @time 2021/8/11 11:05 上午
         * @param content 代转换的字符串
         * @param arraySize 输出字节数组大小
         */
        private fun stringContentToCustomSize(content: String, arraySize: Int): ArrayList<Byte> {
            val bytes = ArrayList<Byte>()

            val contentByteArray = content.toByteArray(Charset.forName("gb2312"))

            for (value in contentByteArray) {
                bytes.add(value)
            }

            if (contentByteArray.size <= arraySize) {
                //剩余不足补 0x00
                val leftSize = arraySize - contentByteArray.size
                for (index in 0.until(leftSize)) {
                    bytes.add(0x00.toByte())
                }
            }

            return bytes
        }


        private fun stringContentToCustomSizeSpecial(
            content: String,
            arraySize: Int
        ): ArrayList<Byte> {
            val bytes = ArrayList<Byte>()
            val toCharArray = content.toCharArray()
            val contentByteArray = ArrayList<Byte>()
            for (value in toCharArray) {
                val toByteArray = value.toString().toByteArray(
                    Charset.forName("gb2312")
                )
                val size = toByteArray.size

                if (size == 1) {
                    contentByteArray.add(0x30.toByte())
                    contentByteArray.add(toByteArray.first())
                } else {
                    for (value in toByteArray) {
                        contentByteArray.add(value)
                    }
                }
            }


            bytes.addAll(contentByteArray)
            if (contentByteArray.size <= arraySize) {
                //剩余不足补 0x00
                val leftSize = arraySize - contentByteArray.size
                for (index in 0.until(leftSize)) {
                    bytes.add(0x00.toByte())
                }
            }
            return bytes
        }


        private fun getTimeT016(time: Long): ArrayList<Byte> {
            val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
            val str = format.format(Date(time))

            val bytes = ArrayList<Byte>()

            val split = str.split("-")

            for ((index, value) in split.withIndex()) {
                if (index == 0) {
                    bytes.add((value.toInt() - 2000).toByte())
                } else {
                    bytes.add(value.toInt().toByte())
                }
            }


            return bytes

        }


        private fun showMessage(arrayList: ArrayList<Byte>) {
            val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
            Log.e("Manager433", formatHexString)
        }

        fun initService(context: Context, receiverListener: ReceiverListener) {
            service433Receiver = Service433Receiver(receiverListener)
            val intentFilter = IntentFilter();
            intentFilter.addAction("android.intent.action.narrowband_info")
            context.registerReceiver(service433Receiver, intentFilter)
        }

        fun unRegisterReceiver(context: Context) {
            context.unregisterReceiver(service433Receiver)
        }
    }


}