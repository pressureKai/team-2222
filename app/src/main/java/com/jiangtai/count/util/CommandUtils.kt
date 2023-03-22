package com.jiangtai.count.util

import android.content.Context
import android.content.LoRaManager
import android.content.SndData
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import java.lang.Exception
import java.nio.charset.Charset

//命令
class CommandUtils {
    companion object {
        /**
         * @des 射击开始命令
         * @time 2021/8/11 1:41 下午
         */
        fun shootStart(context: Context, phoneId: Int, isLocation: Boolean) {
            var startLocation = 0x00
            if (isLocation) {
                startLocation = 0x01
            }
            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x51.toByte(),
                2.toByte(),
                phoneId.toByte(),
                startLocation.toByte(),
                0,
                0
            )
            val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 6)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            bytes[6] = shortToBytes[0]
            bytes[7] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送射击开始命令 $formatHexString")
            //  context.showToast("发送射击开始命令 $formatHexString")
            val sndData = SndData(bytes.toByteArray(), bytes.size)
            loRaManager.sendContent(sndData)
        }


        fun nowStart(
            context: Context,
            phoneId: Int,
            planId: Int,
            taskId: Int
        ) {
            Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    LogUtils.e("send  taskId $taskId")
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x5B.toByte(),
                        //110 change to 196
                        4.toByte(),
                        phoneId.toByte(),
                        planId.toByte(),
                        taskId.toByte(),
                        1.toByte()
                    )

                    arrayList.clear()
                    arrayList.addAll(bytes)

                    arrayList.add(0x00.toByte())
                    arrayList.add(0x00.toByte())

                    val calculateCrc =
                        CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)

                    LogUtils.e("立即开始 $formatHexString size is ${arrayList.size}")
                    //         context.showToast("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")

                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)

                    loRaManager.sendContent(sndData)
                } catch (e: Exception) {

                }
            }
        }


        fun nowEnd(
            context: Context,
            phoneId: Int,
            planId: Int,
            taskId: Int,
        ) {
            Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    LogUtils.e("send  taskId $taskId")
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x5B.toByte(),
                        //110 change to 196
                        4.toByte(),
                        phoneId.toByte(),
                        planId.toByte(),
                        taskId.toByte(),
                        2.toByte()
                    )

                    arrayList.clear()
                    arrayList.addAll(bytes)

                    arrayList.add(0x00.toByte())
                    arrayList.add(0x00.toByte())

                    val calculateCrc =
                        CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)

                    LogUtils.e("立即结束 $formatHexString size is ${arrayList.size}")
                    //         context.showToast("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")

                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)

                    loRaManager.sendContent(sndData)
                } catch (e: Exception) {

                }
            }
        }

        /**
         * @des 射击结束命令
         * @time 2021/8/11 1:43 下午
         */
        fun shootEnd(context: Context, phoneId: Int) {
            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x52.toByte(),
                1,
                phoneId.toByte(),
                0,
                0
            )
            val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 5)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            bytes[5] = shortToBytes[0]
            bytes[6] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送射击结束命令 $formatHexString")
            val sndData = SndData(bytes.toByteArray(), bytes.size)
            loRaManager.sendContent(sndData)
        }

        /**
         * @des 查询射弹次数 （单个查询）
         * @time 2021/8/10 4:25 下午
         */
        fun inquiryShootNumber(context: Context, phoneId: String, userId: String) {
            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val arrayList = ArrayList<Byte>()

            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x53.toByte(),
                17.toByte(),
                parseToInt(phoneId).toByte()
            )
            arrayList.addAll(bytes)

            val userIdBytes = HexUtil.decodeHex(userId.toCharArray())

            for(value in userIdBytes){
                arrayList.add(value)
            }

            if(userIdBytes.size < 16){
                val i = 16 - userIdBytes.size
                for(value in 0.until(i)){
                    arrayList.add(0x00.toByte())
                }
            }

            arrayList.add(0.toByte())
            arrayList.add(0.toByte())
            val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送查询射弹次数命令 $formatHexString")
            //      context.showToast("发送查询射弹次数命令 $formatHexString")
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loRaManager.sendContent(sndData)
        }





        /**
         * @des 查询射弹次数 （单个查询）
         * @time 2021/8/10 4:25 下午
         */
        fun sign(context: Context, phoneId: String ?= "255", taskId : String ?= "0") {
            Handler(Looper.getMainLooper()).post {
                val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                val arrayList = ArrayList<Byte>()
                val bytes = arrayOf(
                    0xE8.toByte(),
                    0xEB.toByte(),
                    0x5A.toByte(),
                    2.toByte(),
                    parseToInt(phoneId!!).toByte(),
                    parseToInt(taskId!!).toByte()
                )
                arrayList.addAll(bytes)
                arrayList.add(0.toByte())
                arrayList.add(0.toByte())
                val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                arrayList[arrayList.size - 2] = shortToBytes[0]
                arrayList[arrayList.size - 1] = shortToBytes[1]
                val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                //发送任务开始命令
                LogUtils.e("发送无感打卡指令 $formatHexString")
                //      context.showToast("发送查询射弹次数命令 $formatHexString")
                val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                loRaManager.sendContent(sndData)
            }
        }




        /**
         * @des 专项计划下发  总共112个字节
         * @time 2021/8/10 7:00 下午
         *@param phoneId 手持机ID 1
         *@param taskId 计划ID 1
         *@param taskName 计划名称 20
         *@param projectId 任务ID 1
         *@param projectName 任务名称 20
         *@param projectContent 任务内容 50
         *@param startTime 开始时间 6
         *@param endTime 结束时间 6
         *@param isLocation 是否开启定位 1
         */
        //专项计划下发
        fun majorTraining2(
            phoneId: Int,
            taskId: Int,
            taskName: String,
            projectId: String,
            projectName: String,
            projectContent: String,
            startTime: String,
            endTime: String,
            isLocation: Boolean,
            context: Context
        ) {
            android.os.Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    LogUtils.e("send  taskId $taskId")
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x54.toByte(),
                        //110 change to 196
                        196.toByte(),
                        phoneId.toByte(),
                        taskId.toByte()
                    )

                    arrayList.clear()
                    arrayList.addAll(bytes)


                    LogUtils.e("current 6 byte size is ${arrayList.size}")

                    //计划名称字节字符串数组
                    val taskNameByteArray = stringContentToCustomSize(taskName, 20)
                    arrayList.addAll(taskNameByteArray)


                    LogUtils.e("current  26 byte size is ${arrayList.size}")
                    //项目(任务)ID
                    arrayList.add(parseToInt(projectId).toByte())

                    LogUtils.e("send  projectId ${parseToInt(projectId)}")


                    LogUtils.e("current 27 byte size is ${arrayList.size}")
                    //任务名称字节字符串数组
                    val projectNameByteArray = stringContentToCustomSize(projectName, 20)
                    arrayList.addAll(projectNameByteArray)

                    LogUtils.e("current 47 byte size is ${arrayList.size}")
                    //任务内容字节字符串数组  change 54 to 140
                    val projectContentByteArray = stringContentToCustomSizeSpecial(projectContent, 140)
                    arrayList.addAll(projectContentByteArray)

                    LogUtils.e("current 187 byte size is ${arrayList.size}")


                    //开始时间字节字符串数组
                    val startTimeByteArray = CommonUtil.getTimeT016(startTime.toLong())
                    arrayList.addAll(startTimeByteArray)
                    LogUtils.e("current 193 byte size is ${arrayList.size}")

                    //结束时间字节字符串数组
                    val endTimeByteArray =  CommonUtil.getTimeT016(endTime.toLong())
                    arrayList.addAll(endTimeByteArray)

                    LogUtils.e("current 199 byte size is ${arrayList.size}")


                    var locationByte = 0x00.toByte()
                    if (isLocation) {
                        locationByte = 0x01.toByte()
                    }
                    arrayList.add(locationByte)

                    LogUtils.e("current 200 byte size is ${arrayList.size}")
                    //crc 校验位
                    arrayList.add(0x00.toByte())
                    arrayList.add(0x00.toByte())
                    LogUtils.e("current  202 byte size is ${arrayList.size}")

                    //发送命令
                    val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                    //发送任务开始命令
                    Handler(Looper.getMainLooper()).postDelayed({
//                       ToastUtils.showShort(formatHexString)
                    },1000)
                    LogUtils.e("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")
                    //         context.showToast("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")

                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)

                    loRaManager.sendContent(sndData)

                    ToastUtils.showShort("任务下发完成")
                }catch (e:Exception){

                }
            }
        }


        fun majorTraining(
            phoneId: Int,
            taskId: Int,
            taskName: String,
            projectId: String,
            projectName: String,
            projectContent: String,
            startTime: String,
            endTime: String,
            isLocation: Boolean,
            context: Context
        ) {
            android.os.Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    LogUtils.e("send  taskId $taskId")
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x54.toByte(),
                        //110 change to 196
                        110.toByte(),
                        phoneId.toByte(),
                        taskId.toByte()
                    )

                    arrayList.clear()
                    arrayList.addAll(bytes)


                    LogUtils.e("current 6 byte size is ${arrayList.size}")

                    //计划名称字节字符串数组
                    val taskNameByteArray = stringContentToCustomSize(taskName, 20)
                    arrayList.addAll(taskNameByteArray)


                    LogUtils.e("current  26 byte size is ${arrayList.size}")
                    //项目(任务)ID
                    arrayList.add(parseToInt(projectId).toByte())

                    LogUtils.e("send  projectId ${parseToInt(projectId)}")


                    LogUtils.e("current 27 byte size is ${arrayList.size}")
                    //任务名称字节字符串数组
                    val projectNameByteArray = stringContentToCustomSize(projectName, 20)
                    arrayList.addAll(projectNameByteArray)

                    LogUtils.e("current 47 byte size is ${arrayList.size}")
                    //任务内容字节字符串数组  change 54 to 140
                    val projectContentByteArray = stringContentToCustomSizeSpecial(projectContent, 54)
                    arrayList.addAll(projectContentByteArray)

                    LogUtils.e("current 101 byte size is ${arrayList.size}")


                    //开始时间字节字符串数组
                    val startTimeByteArray = CommonUtil.getTimeT016(startTime.toLong())
                    arrayList.addAll(startTimeByteArray)
                    LogUtils.e("current 107 byte size is ${arrayList.size}")

                    //结束时间字节字符串数组
                    val endTimeByteArray =  CommonUtil.getTimeT016(endTime.toLong())
                    arrayList.addAll(endTimeByteArray)

                    LogUtils.e("current 113 byte size is ${arrayList.size}")


                    var locationByte = 0x00.toByte()
                    if (isLocation) {
                        locationByte = 0x01.toByte()
                    }
                    arrayList.add(locationByte)

                    LogUtils.e("current 114 byte size is ${arrayList.size}")
                    //crc 校验位
                    arrayList.add(0x00.toByte())
                    arrayList.add(0x00.toByte())
                    LogUtils.e("current 116 byte size is ${arrayList.size}")

                    //发送命令
                    val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                    //发送任务开始命令
                    Handler(Looper.getMainLooper()).postDelayed({
//                        ToastUtils.showShort(formatHexString)
                    },1000)
                    LogUtils.e("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")
                    //         context.showToast("发送专项计划下发命令 $formatHexString size is ${arrayList.size}")

                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)

                    loRaManager.sendContent(sndData)

                    ToastUtils.showShort("任务下发完成")
                }catch (e:Exception){

                }
            }
        }


        //行动命令下发
        fun actionCommand(
            context: Context,
            phoneId: Int,
            taskId: String,
            projectId: String,
            content: String
        ) {
            LogUtils.e("projectId is $projectId taskId is $taskId phoneId $phoneId")
            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x56.toByte(),
                7.toByte(),
                phoneId.toByte(),
                parseToInt(taskId).toByte(),
                parseToInt(projectId).toByte()
            )

            arrayList.addAll(bytes)
            //命令内容字符串字节数组
            val contentByteArrayList = stringContentToCustomSize(content, 4)
            arrayList.addAll(contentByteArrayList)
            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())


            //发送命令
            val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送行动${content}下发命令 $formatHexString")
            //          context.showToast("发送行动${content}下发命令 $formatHexString")
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loRaManager.sendContent(sndData)
        }




        //行动命令下发
        fun actionCommand2(
            context: Context,
            phoneId: Int,
            taskId: String,
            projectId: String,
            userId:String,
            content: String
        ) {
            LogUtils.e("projectId is $projectId taskId is $taskId phoneId $phoneId")
            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x59.toByte(),
                27.toByte(),
                phoneId.toByte(),
                parseToInt(taskId).toByte(),
                parseToInt(projectId).toByte()
            )

            arrayList.addAll(bytes)

            //添加用户ID
            val userIdBytes = HexUtil.decodeHex(userId.toCharArray())
            for(value in userIdBytes){
                arrayList.add(value.toByte())
            }

            if(userIdBytes.size < 16){
                val i = 16 - userIdBytes.size
                for(index in 0.until(i)){
                    arrayList.add(0x00.toByte())
                }
            }


            //命令内容字符串字节数组
            val contentByteArrayList = stringContentToCustomSize(content, 8)
            arrayList.addAll(contentByteArrayList)
            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())


            //发送命令
            val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送行动${content}下发命令 $formatHexString")
            //          context.showToast("发送行动${content}下发命令 $formatHexString")
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loRaManager.sendContent(sndData)
        }

        /**
         * @des 查询专项计划成绩
         * @time 2021/8/11 12:15 下午
         * @param phoneId 手持机ID
         * @param userId USER ID
         * @param taskId 计划ID
         * @param projectId 任务ID
         */
        //查询专项计划成绩
        fun inquiryTrainingScore(
            context: Context,
            phoneId: String,
            userId: String,
            taskId: String,
            projectId: String
        ) {
            android.os.Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x55.toByte(),
                        19.toByte(),
                        parseToInt(phoneId).toByte()
                    )

                    arrayList.addAll(bytes)
                    val userIdBytes = HexUtil.decodeHex(userId.toCharArray())

                    for(value in userIdBytes){
                        arrayList.add(value)
                    }

                    if(userIdBytes.size < 16){
                        val i = 16 - userIdBytes.size
                        for(value in 0.until(i)){
                            arrayList.add(0x00.toByte())
                        }
                    }


                    //计划ID
                    arrayList.add(parseToInt(taskId).toByte())
                    arrayList.add(parseToInt(projectId).toByte())


                    //crc 校验位
                    arrayList.add(0x00.toByte())
                    arrayList.add(0x00.toByte())


                    //发送命令
                    val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                    //发送任务开始命令
                    LogUtils.e("发送查询专项计划成绩命令 $formatHexString")
                    //                  context.showToast("发送查询专项计划成绩命令 $formatHexString")
                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                    loRaManager.sendContent(sndData)
                }catch (e:Exception){
                    LogUtils.e("CommandUtils $e")
                }
            }

        }


        /**
         * @des 查询出车记录
         * @time 2021/8/11 12:15 下午
         * @param phoneId 手持机ID
         * @param userId USER ID
         * @param taskId 计划ID
         * @param projectId 任务ID
         */
        //查询出车记录
        fun inquiryVehicleHistory(
            context: Context,
            phoneId: String,
            userId: String,
            taskId: String,
            projectId: String
        ) {

            val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
            val arrayList = ArrayList<Byte>()
            val bytes = arrayOf(
                0xE8.toByte(),
                0xEB.toByte(),
                0x53.toByte(),
                19.toByte(),
                parseToInt(phoneId).toByte()
            )

            arrayList.addAll(bytes)
            val userIdBytes = stringUsrIdToByteSizeSixteen(userId)
            arrayList.addAll(userIdBytes)

            arrayList.add(parseToInt(taskId).toByte())
            arrayList.add(parseToInt(projectId).toByte())


            //crc 校验位
            arrayList.add(0x00.toByte())
            arrayList.add(0x00.toByte())


            //发送命令
            val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            arrayList[arrayList.size - 2] = shortToBytes[0]
            arrayList[arrayList.size - 1] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
            //发送任务开始命令
            LogUtils.e("发送查询出车记录命令 $formatHexString")
            val sndData = SndData(arrayList.toByteArray(), arrayList.size)
            loRaManager.sendContent(sndData)
        }

        /**
         * @des 搜救指令
         * @time 2021/8/11 12:23 下午
         */
        //搜救指令
        fun searchCommand(context: Context, phoneId: Int, userId: String) {
            try {
                val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                val arrayList = ArrayList<Byte>()
                val bytes = arrayOf(
                    0xE8.toByte(),
                    0xEB.toByte(),
                    0x57.toByte(),
                    17.toByte(),
                    phoneId.toByte()
                )

                arrayList.addAll(bytes)
                val userIdBytes = HexUtil.decodeHex(userId.toCharArray())
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
                val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                arrayList[arrayList.size - 2] = shortToBytes[0]
                arrayList[arrayList.size - 1] = shortToBytes[1]
                val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                //发送任务开始命令
                LogUtils.e("发送搜救指令 $formatHexString")
                val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                loRaManager.sendContent(sndData)
            }catch (e:Exception){
                LogUtils.e("CommandUtils error is $e")
            }

        }


        /**
         * @des 查询设备位置信息
         * @time 2021/8/11 12:25 下午
         * @param taskId 射弹 0xFF
         * @param userId 用户ID，十六进制表示，不够的在ID前面用0X00补充；
        当发送的USER ID是全0，则表示是要求全员上报位置信息；
         */
        //查询设备信息
        fun inquiryDeviceCommand(context: Context,
                                 phoneId: Int,
                                 taskId: Int? = 0xFF,
                                 userId: String) {
            android.os.Handler(Looper.getMainLooper()).post {
                try {
                    LogUtils.e("taskId is $taskId")
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x58.toByte(),
                        18.toByte(),
                        phoneId.toByte(),
                        taskId!!.toByte()
                    )
                    arrayList.addAll(bytes)
                    val userIdBytes = HexUtil.decodeHex(userId.toCharArray())
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
                    val calculateCrc = CrcUtil.Calculate_Crc(arrayList.toByteArray(), 0, arrayList.size - 2)
                    val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                    arrayList[arrayList.size - 2] = shortToBytes[0]
                    arrayList[arrayList.size - 1] = shortToBytes[1]
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)
                    //发送任务开始命令
                    LogUtils.e("发送查询设备位置信息指令 $formatHexString")
                    //     context.showToast("发送查询设备位置信息指令 $formatHexString")
                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                    loRaManager.sendContent(sndData)
                }catch (e:Exception){
                    LogUtils.e("error is $e")
                }
            }
        }

        //查询设备信息
        fun inquiryDeviceCommand2(
            context: Context,
            phoneId: Int,
            taskId: Int? = 0xFF
        ) {
            Handler(Looper.getMainLooper()).post {
                try {
                    val loRaManager = context.getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val arrayList = ArrayList<Byte>()
                    val bytes = arrayOf(
                        0xE8.toByte(),
                        0xEB.toByte(),
                        0x58.toByte(),
                        18.toByte(),
                        phoneId.toByte(),
                        taskId!!.toByte()
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
                    val formatHexString = HexUtil.formatHexString(arrayList.toByteArray(), true)

                    //发送任务开始命令
                    LogUtils.e("发送查询设备位置信息指令 $formatHexString")
                    val sndData = SndData(arrayList.toByteArray(), arrayList.size)
                    loRaManager.sendContent(sndData)
                } catch (e: Exception) {
                    LogUtils.e("error is $e")
                }
            }
        }



        /**
         * @des 将传入的字符串ID转换为16位Byte数组
         * @time 2021/8/10 4:37 下午
         */
        fun stringUsrIdToByteSizeSixteen(id: String): ArrayList<Byte> {
            val bytes = ArrayList<Byte>()
            val toCharArray = id.trim().toCharArray()

            if (toCharArray.size <= 16) {
                val leftSize = 16 - toCharArray.size
                for (index in 0.until(leftSize)) {
                    bytes.add(0.toByte())
                }
            }

            for (char in toCharArray) {
                bytes.add(HexUtil.charToByte(char))
            }


            return bytes
        }


        /**
         * @des 将字符串内容转换成指定大小的字节数组
         * @time 2021/8/11 11:05 上午
         * @param content 代转换的字符串
         * @param arraySize 输出字节数组大小
         */
        private fun intContentToCustomSize(content: String, arraySize: Int): ArrayList<Byte> {
            val bytes = ArrayList<Byte>()
            val contentCharArray = content.toCharArray()

            for (char in contentCharArray) {
                bytes.add(HexUtil.charToByte(char))
            }

            if (contentCharArray.size <= arraySize) {
                //剩余不足补 0x00
                val leftSize = arraySize - contentCharArray.size
                for (index in 0.until(leftSize)) {
                    bytes.add(0x00.toByte())
                }
            }
            LogUtils.e("CommandUtils bytes size is ${bytes.size}")

            return bytes
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


            LogUtils.e("CommandUtils bytes size is ${bytes.size}")

            return bytes
        }


        private fun stringContentToCustomSizeSpecial(content: String, arraySize: Int): ArrayList<Byte>{
            val bytes = ArrayList<Byte>()
            val toCharArray = content.toCharArray()
            val contentByteArray = ArrayList<Byte>()
            for(value in toCharArray){
                val toByteArray = value.toString().toByteArray(
                    Charset.forName("gb2312"))
                val size = toByteArray.size

                if(size == 1){
                    contentByteArray.add(0x30.toByte())
                    contentByteArray.add(toByteArray.first())
                } else {
                    for(value in toByteArray){
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
        private fun parseToInt(string:String):Int{
            var int = 0
            if(string.isEmpty()){
                return int
            }
            try {
                int = Integer.parseInt(string)
            }catch (e:Exception){
                try {
                    int = string.toFloat().toInt()
                }catch (e:Exception){

                }

            }

            return int
        }
    }



}