package com.jiangtai.count.util

import android.content.BDManager
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.cld.mapapi.model.LatLng
import com.jiangtai.count.bean.BeiDouSendMessageBean
import com.jiangtai.count.bean.LocationReceiver
import com.jiangtai.count.bean.ReceiverConfirmIdMessage
import com.jiangtai.count.bean.SendConfirmIdMessage
import com.jiangtai.count.constant.Constant
import com.lhzw.libcc.*
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.ArrayList

open class BeiDouUtils {
    companion object {
        private var startID: Int by Preference(Constant.START_ID, 1)
        private var minSentConfirmID: Int by Preference(Constant.MIN_CONFIRM_ID, 1)
        private var maxSentConfirmID: Int by Preference(Constant.MAX_CONFIRM_ID, 1)

        /**
         * BD短信发送
         * @param comType :0 ~ 普通 1 ~ 紧急 0
         * @param comFont :0 ~ 代码 1 ~ 汉字 2 ~ 混发 2
         * @param des :收信方SIM卡地址 458893  八位
         * @param body :信息发送内容 输出：$BDRev,46,250287,test 0xA4 body
         */
        fun sendMMS(
            mBDManager: BDManager,
            comType: Int, comFont: Int, des: String, body: String
        ) {
            try {
                val uploadLocation = singlePoint()
                var bodyON = "empty"
                if (uploadLocation.isNotEmpty()) {
                    bodyON = uploadLocation
                }
                val data = body.toByteArray(Charset.forName("GB2312"))
                var sendMessage = ""
                val len = data.size
                sendMessage = "\$BDSnd,$comType,$comFont,$des,$len,$bodyON"
                LogUtils.e("BeiDouUtils sendMessage $sendMessage")
                //ToastUtils.showShort(sendMessage)
                //发送北斗消息
                mBDManager.sendBDCommand(sendMessage)
                startID = LibccInterface.uncompressMessageHead(body).dataId
                val sendConfirmIdMessage = SendConfirmIdMessage()
                sendConfirmIdMessage.address = des
                sendConfirmIdMessage.sendAddress = mBDManager.bdCardNumber
                sendConfirmIdMessage.dataId = startID.toString()
                sendConfirmIdMessage.sendState = 1.toString()
                sendConfirmIdMessage.sendMessageTime = System.currentTimeMillis().toString()
                sendConfirmIdMessage.save()
            }catch (e:Exception){
                LogUtils.e("send MMs error is $e")
            }
        }


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
                LogUtils.e("BeiDouUtils sendMessage $sendMessage")

           //     ToastUtils.showShort(sendMessage)
                //发送北斗消息
                mBDManager.sendBDCommand(sendMessage)
                startID = LibccInterface.uncompressMessageHead(body).dataId

                //具有状态的改变用来确定
                val sendConfirmIdMessage = SendConfirmIdMessage()
                sendConfirmIdMessage.address = des
                sendConfirmIdMessage.sendAddress = mBDManager.bdCardNumber
                sendConfirmIdMessage.dataId = startID.toString()
                sendConfirmIdMessage.sendState = 1.toString()
                sendConfirmIdMessage.sendMessageTime = System.currentTimeMillis().toString()
                sendConfirmIdMessage.save()

                //北斗消息发送历史
                val beiDouSendMessageBean = BeiDouSendMessageBean()
                beiDouSendMessageBean.content = body
                beiDouSendMessageBean.time = System.currentTimeMillis().toString()
                beiDouSendMessageBean.save()
                EventBus.getDefault().postSticky(beiDouSendMessageBean)
            }catch (e:Exception){

            }
        }

        /**
         * @des 上报位置
         * @time 2021/8/13 2:04 下午
         */
        fun uploadLocation(): ArrayList<String> {

            // 手持机位置信息
            val beanHandset = BeanHandset(116.385625, 39.995622, System.currentTimeMillis())

            // 任务号
            val taskId = 7

            // 手表、信标位置列表
            val positionList = ArrayList<BeanPosition>()

            for (i in 20 downTo 0) {
                // 取随机数
                val tempX: Double = 116.385625
                val tempY: Double = 39.995622
                //long tempTime = (long)getRandom(System.currentTimeMillis(), 3*60*60*1000, 0);
                //positionList.add(new BeanPosition(i, tempX, tempY, tempTime, (i*2)%3, i%5, i%3));
                positionList.add(
                    BeanPosition(
                        i, tempX, tempY,
                        System.currentTimeMillis(),
                        0,
                        0,
                        0
                    )
                )
            }

            startID += 1
            // 数据初始化
            val myMessageHead1 = MessageHead(
                0,
                1,
                1,
                3,
                5,
                false,
                startID
            )
            myMessageHead1.order = 16
            val myDataTaskPosition1 = DataTaskPosition(beanHandset, taskId, positionList)
            // 压缩
            val myCompressResult =
                LibccInterface.compressTaskPosition(myMessageHead1, myDataTaskPosition1)
            if (myCompressResult.errCode != CodeErr.SUCCEED) {
                return ArrayList()
            }



            LogUtils.e("BeiDou Point x 总的包个数 ${myCompressResult.messageList.size}")
            // 解压
            for (i in myCompressResult.messageList.indices) {
                // 解压消息头
                val myMessageHead2 =
                    LibccInterface.uncompressMessageHead(myCompressResult.messageList[i])
                myMessageHead2?.print()

                LogUtils.e("BeiDou Point x dataId ${myMessageHead2.dataId}")

                // 解压消息体
                val myDataPosition2 =
                    LibccInterface.uncompressPosition(myCompressResult.messageList[i])
                myDataPosition2?.print()
                LogUtils.e("BeiDou Point x 每个包的数据点为 ${myDataPosition2.pointList.size}")


                for(value in myDataPosition2.pointList){
                    LogUtils.e("BeiDou Point x is ${value.x} y is ${value.y}")
                }
            }


            return myCompressResult.messageList
        }


        // 取随机数时是否翻转
        // 偏移0：一般随机数
        // 偏移1：坐标x随机数
        // 偏移2：坐标y随机数
        private val isOverturns = arrayOf(false, false, false)


        /**
         * 计算随机数（只递增）
         * @param base     基数
         * @param max      最大偏差
         * @param digit    保留位数
         * @return
         */
        protected fun getRandom(base: Double, max: Double, digit: Int): Double {
            var ret = base
            ret += max * Math.random()
            val bg = BigDecimal(ret)
            ret = bg.setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()
            return ret
        }


        /**
         * 计算随机数（可倒退）
         * @param base     基数
         * @param max      最大偏差
         * @param digit    保留位数
         * @param type     0-一般随机数    1-x坐标随机数    2-y坐标随机数
         * @return
         */
        protected fun getRandom(base: Double, max: Double, digit: Int, type: Int): Double {
            var get = isOverturns.get(type)
            if (Math.random() > 0.9) {
                get = !get
            }
            var ret = base
            if (get) {
                ret += max * Math.random()
            } else {
                ret -= max * Math.random()
            }
            val bg = BigDecimal(ret)
            ret = bg.setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()
            return ret
        }


        /**
         * @des 任务位置上报
         * @param handsetLatLng 手持机经纬度
         * @param
         * @time 2021/8/17 1:44 下午
         */
        fun taskPositionUpload(
            handsetLatLng: LatLng,
            taskId: Int,
            positions: ArrayList<LocationReceiver>
        ): ArrayList<String> {
            var myCompressResult :CompressResult ?= null
            try {
                val beanHandset = BeanHandset(handsetLatLng.latitude, handsetLatLng.longitude, System.currentTimeMillis())

                // 数据初始化
                startID += 1
                val myMessageHead1 = MessageHead(
                    0,
                    1,
                    1,
                    3,
                    5,
                    false,
                    startID
                )
                myMessageHead1.order = 16


                val positionList = ArrayList<BeanPosition>()
                for(value in positions){
                    val tempX: Double = value.lat.toDouble()
                    val tempY: Double = value.lng.toDouble()
                    //shortId  短ID可能存在错误
                    try {
                        positionList.add(
                            BeanPosition(
                                0,
                                tempX,
                                tempY,
                                value.time.toLong(),
                                0,
                                0,
                                0
                            )
                        )
                    }catch (e:Exception){
                        LogUtils.e("parse to long error is $e value.time is ${value.time}")
                    }

                }
                val myDataTaskPosition1 = DataTaskPosition(beanHandset, taskId, positionList)

                 myCompressResult =
                    LibccInterface.compressTaskPosition(myMessageHead1, myDataTaskPosition1)
                if (myCompressResult.errCode != CodeErr.SUCCEED) {
                    return ArrayList()
                }
            }catch (e:Exception){

            }

            myCompressResult?.let {
               return  myCompressResult.messageList
            }
            return ArrayList()
        }


        /**
         * 离散点上报
         * @param handsetLatLng 手持机位置
         * @param expandType 扩展标识位 1
         * @param locationList  腕表，信标位置列表
         */
        fun singlePoint(
            handsetLatLng : LatLng,
            expandType: Int? = 1,
            locationList: ArrayList<LocationReceiver>
        ): ArrayList<String> {
            val beanHandset = BeanHandset(handsetLatLng.latitude, handsetLatLng.longitude, System.currentTimeMillis())

            startID += 1
            val myMessageHead1 = MessageHead(
                0,
                1,
                1,
                3,
                5,
                false,
                startID
            )
            myMessageHead1.order = 15

            val singlePointList = ArrayList<BeanSinglePoint>()
            for(value in locationList){
                val tempX: Double = getRandom(value.lat.toDouble(), 0.5, 6, 1)
                val tempY: Double = getRandom(value.lng.toDouble(), 0.5, 6, 2)


                //可能出错的地方为压缩算法中的用户ID
                singlePointList.add(
                    BeanSinglePoint(
                        value.userId,
                        tempX,
                        tempY,
                        System.currentTimeMillis(),
                        value.sosType.toInt(),
                        1,
                        0,
                        "60"//心率
                    )
                )
            }
            val myDataSinglePoint1 = DataSinglePoint(beanHandset, expandType!!, singlePointList)

            // 压缩
            val myCompressResult =
                LibccInterface.compressSinglePoint(myMessageHead1, myDataSinglePoint1)
            if (myCompressResult.errCode != CodeErr.SUCCEED) {
                return ArrayList<String>()
            }

            return myCompressResult.messageList
        }


        /**
         * 零散点上报（测试）
         */
        private fun singlePoint(): String {
            // 手持机位置信息
            val beanHandset = BeanHandset(116.385625, 39.995622, System.currentTimeMillis())

            // 扩展标识位
            val expandType = 1

            // 手表、信标列表
            val singlePointList = ArrayList<BeanSinglePoint>()

            for (i in 1 downTo 0) {
                // 取随机数
                val tempX: Double = getRandom(116.385625, 0.5, 6, 1)
                val tempY: Double = getRandom(39.995622, 0.5, 6, 2)

                singlePointList.add(
                    BeanSinglePoint(
                        "12345789A",
                        tempX,
                        tempY,
                        System.currentTimeMillis(),
                        i * 2 % 3,
                        i % 5,
                        0,
                        "60"//心率
                    )
                )
            }

            // 数据初始化
            val myMessageHead1 = MessageHead(
                1,
                1,
                1,
                3,
                5,
                true,
                startID
            )
            myMessageHead1.order = 15
            myMessageHead1.print()
            val myDataSinglePoint1 = DataSinglePoint(
                beanHandset,
                expandType,
                singlePointList
            )
            myDataSinglePoint1.print()

            // 压缩
            val myCompressResult =
                LibccInterface.compressSinglePoint(myMessageHead1, myDataSinglePoint1)
            myCompressResult.print()
            if (myCompressResult.errCode != CodeErr.SUCCEED) {
                return ""
            }

            return myCompressResult.messageList.first()
        }


        fun sendDeviceNumber(): ArrayList<String> {
            startID += 1
            val myMessageHead = MessageHead(
                0,
                1,
                1,
                3,
                5,
                false,
                startID
            )
            val myDataDeviceId = DataDeviceId(System.currentTimeMillis(), CommonUtil.getSNCode())
            val compressDeviceId = LibccInterface.compressDeviceid(myMessageHead, myDataDeviceId)
            return compressDeviceId.messageList
        }


        /**
         * @des 已收ID上报
         *   上报北斗平台下发的消息的确认序列
         *   minConfirmId 手持机发送的消息最小待确认ID
         *   maxConfirmId 手持机发送的消息最大待确认ID
         *   startID 上报序列的起始ID
         * @time 2021/8/23 11:38 上午
         */
        fun receivedIdUpload(): String {
            var string = ""
            Handler(Looper.getMainLooper()).post {
                try {
                    // 是否收到列表
                    val isReceivedList = ArrayList<Boolean>()

                    for (i in minSentConfirmID..maxSentConfirmID) {
                        val find = LitePal.where("dataId = ?", i.toString())
                            .find(ReceiverConfirmIdMessage::class.java)
                        if(find.isNotEmpty()){
                            isReceivedList.add(true)
                        } else {
                            isReceivedList.add(false)
                        }
                    }

                    startID += 1
                    // 数据初始化
                    val myMessageHead1 = MessageHead(
                        0,
                        1,
                        1,
                        3,
                        5,
                        true, startID
                    )
                    myMessageHead1.order = 10
                    myMessageHead1.print()
                    val myDataReceivedIdUpload1 = DataReceivedId(
                        getMinSendConfirmId(),
                        getMaxSendConfirmId(),
                        minSentConfirmID,
                        isReceivedList
                    )
                    myDataReceivedIdUpload1.print()

                    // 压缩
                    val myCompressResult =
                        LibccInterface.compressReceivedIdUpload(myMessageHead1, myDataReceivedIdUpload1)
                    myCompressResult.print()
                    if (myCompressResult.errCode != CodeErr.SUCCEED) {
                        string = ""
                        return@post
                    }


                    val messageList = myCompressResult.messageList
                    for(message in messageList){
                        // 解压消息头
                        val myMessageHead2 =
                            LibccInterface.uncompressMessageHead(message)
                        myMessageHead2?.print()

                        // 解压消息体
                        val myDataReceivedIdUpload2 = LibccInterface.uncompressReceivedIdUpload(
                            message
                        )

                        LogUtils.e("message is $message")
                        myDataReceivedIdUpload2?.print()
                    }

                    string =  myCompressResult.messageList[0]
                }catch (e:Exception){

                }
            }


            return string

        }



        /**
        * @des 获取最小已发送待确认Id
        * @time 2021/8/27 9:49 上午
        */
        private fun getMinSendConfirmId():Int{
            var minSentConfirmID = 1
            val find = LitePal.where("sendState = ?", "1").find(SendConfirmIdMessage::class.java)
            if(find.isNotEmpty()){
                try {
                    minSentConfirmID = find.first().dataId.toInt()
                    for(value in find){
                        if(minSentConfirmID > value.dataId.toInt()){
                            minSentConfirmID = value.dataId.toInt()
                        }
                    }
                }catch (e:java.lang.Exception){
                    minSentConfirmID = startID
                }

            } else {
                minSentConfirmID = startID
            }
            return minSentConfirmID
        }


        /**
         * @des 获取最大已发送待确认Id
         * @time 2021/8/27 9:49 上午
         */
        private fun getMaxSendConfirmId():Int{
            var maxSentConfirmID = 1
            val find = LitePal.where("sendState = ?", "1").find(SendConfirmIdMessage::class.java)
            if(find.isNotEmpty()){
                try {
                    maxSentConfirmID = find.first().dataId.toInt()
                    for(value in find){
                        if(maxSentConfirmID < value.dataId.toInt()){
                            maxSentConfirmID = value.dataId.toInt()
                        }
                    }
                }catch (e:java.lang.Exception){
                    maxSentConfirmID = startID
                }

            } else {
                maxSentConfirmID = startID
            }

            return maxSentConfirmID
        }

    }
}