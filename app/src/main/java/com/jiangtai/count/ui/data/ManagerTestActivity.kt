package com.jiangtai.count.ui.data

import android.os.Handler
import android.os.Looper
import android.widget.EditText
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.easysocket.EasySocket
import com.easysocket.config.EasySocketOptions
import com.easysocket.entity.SocketAddress
import com.easysocket.interfaces.conn.SocketActionListener
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.TransformInfo
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.BCDDecode
import com.jiangtai.count.util.HexUtil
import com.jiangtai.count.util.Preference
import kotlinx.android.synthetic.main.activity_manager_test.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ManagerTestActivity : BaseActivity() {
    private var stateList: ArrayList<String> = ArrayList()
    private var sensorTypeList: ArrayList<String> = ArrayList()
    private var isConnectedSocket = false
    private var socketIp: String by Preference(Constant.SOCKET_IP, "")
    private var socketPort: Int by Preference(Constant.SOCKET_PORT, 8081)
    override fun attachLayoutRes(): Int {
        return R.layout.activity_manager_test
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)

        stateList.add("正常状态")
        stateList.add("求救状态")

        sensorTypeList.add("")
        iv_back.setOnClickListener {
            finish()
        }


        et_state.setOnClickListener {
            showPicker(stateList, "人员状态", et_state, callback = {

            })
        }

        bt_commit_watch.setOnClickListener {
            if (checkResult(true)) {
                sendTcp(true)
            }
        }


        bt_commit_xinbiao.setOnClickListener {
            if (checkResult(false)) {
                sendTcp(false)
            }
        }
        bt_connect.setOnClickListener {
            if(et_port.text.toString().isNotEmpty() && et_ip.text.toString().isNotEmpty()){
                socketIp = et_ip.text.toString()
                socketPort = et_port.text.toString().toInt()
                bt_connect.text = "连接中..."
                bt_connect.isEnabled = false
                initSocket()
            } else {
                ToastUtils.showShort("请填写完整")
            }
        }
        et_port.setText(socketPort.toString())
        et_ip.setText(socketIp)
    }


    private fun str2bcd(str:String):ByteArray{
        return BCDDecode.str2Bcd(str)
    }

    override fun initListener() {

    }


    private fun initSocket() {
        if (socketIp.isNotEmpty()) {
            val socketListener = object : SocketActionListener() {


                override fun onSocketConnFail(
                    socketAddress: SocketAddress?,
                    isNeedReconnect: Boolean
                ) {
                    super.onSocketConnFail(socketAddress, isNeedReconnect)
                    isConnectedSocket = false
                    onSocketConnectStateChange()
                    Handler(Looper.getMainLooper()).post {
                        ToastUtils.showShort("socket connect fail  ${socketAddress?.ip} : ${socketAddress?.port} ")
                    }
                }


                override fun onSocketConnSuccess(socketAddress: SocketAddress?) {
                    super.onSocketConnSuccess(socketAddress)
                    isConnectedSocket = true
                    onSocketConnectStateChange()
                }


                override fun onSocketResponse(socketAddress: SocketAddress?, readData: ByteArray?) {
                    super.onSocketResponse(socketAddress, readData)
                }

                override fun onSocketDisconnect(
                    socketAddress: SocketAddress?,
                    isNeedReconnect: Boolean
                ) {
                    super.onSocketDisconnect(socketAddress, isNeedReconnect)
                    isConnectedSocket = false
                    onSocketConnectStateChange()
                }

            }

            Handler(Looper.getMainLooper()).postDelayed({
                LogUtils.e("$socketIp : $socketPort")
                val options = EasySocketOptions.Builder()
                    .setSocketAddress(
                        SocketAddress(
                            socketIp,
                            socketPort,
                        )
                    )
                    .setConnectTimeout(4000)
                    .build()
               // EasySocket.getInstance().createConnection(options, this)
                EasySocket.getInstance().setDebug(true)
                EasySocket.getInstance().createConnection(options, this)
                EasySocket.getInstance().subscribeSocketAction(socketListener)

            }, 2000)
        }

    }

    private fun sendTcp(isWatch: Boolean) {
        val arrayList = ArrayList<Byte>()
        arrayList.add(0xC1.toByte())
        if(isWatch){
            arrayList.add(0x01.toByte())
        }else{
            arrayList.add(0x02.toByte())
        }


        val sNumber = et_number.text.toString()
        val str2bcd = str2bcd(sNumber).toList()
        arrayList.addAll(str2bcd)


        //序号
        arrayList.add(0x01.toByte())

        if(isWatch){

            arrayList.add(22.toByte())
            if(et_state.text.toString() == "正常状态"){
                arrayList.add(0x00.toByte())
            } else{
                arrayList.add(0x99.toByte())
            }

            //传感器类型
            arrayList.add(63.toByte())
            var latitude = 0.toDouble()
            var longitude = 0.toDouble()


            try {
                val latlng = et_latlng.text.toString()
                val split = latlng.split(",")
                longitude = split[0].toDouble()
                latitude = split[1].toDouble()
            }catch (e:Exception){

            }

            latLng2Dfm(longitude,true)?.forEach {
                //经度
                arrayList.add(it.toByte())
            }

            latLng2Dfm(latitude,false)?.forEach {
                //纬度
                arrayList.add(it.toByte())
            }


            val l = System.currentTimeMillis() - gettimeStemp("2018-01-01 00:00:00")

            val seconds = (l / 1000).toInt()

            HexUtil.intToBytes(seconds).forEach {
                //定位时间
                arrayList.add(it.toByte())
            }





            //心率
            arrayList.add(et_heart.text.toString().toInt().toByte())

            //血氧
            arrayList.add(et_oxygen.text.toString().toInt().toByte())

            //血压
            val toInt = et_blood.text.toString().toInt()
            val intToBytes = HexUtil.intToBytes(toInt, 2)
            intToBytes.reversed()
            intToBytes.forEach {
                arrayList.add(it)
            }


            //体温
            val iTemp = et_temperature.text.toString().toInt()
            val intToBytes1 = HexUtil.intToBytes(iTemp, 2)
            intToBytes1.reversed()
            intToBytes1.forEach {
                arrayList.add(it)
            }

            //气压
            val iPre = et_pressure.text.toString().toInt()
            val intToBytes2 = HexUtil.intToBytes(iPre, 2)
            intToBytes2.reversed()
            intToBytes2.forEach {
                arrayList.add(it)
            }
        } else {
            var latitude = 0.toDouble()
            var longitude = 0.toDouble()
            arrayList.add(12.toByte())

            try {
                val latlng = et_latlng_xinbiao.text.toString()
                val split = latlng.split(",")
                longitude = split[0].toDouble()
                latitude = split[1].toDouble()
            }catch (e:Exception){

            }

            latLng2Dfm(longitude,true)?.forEach {
                //经度
                arrayList.add(it.toByte())
            }

            latLng2Dfm(latitude,false)?.forEach {
                //纬度
                arrayList.add(it.toByte())
            }

            val l = System.currentTimeMillis() - gettimeStemp("2018-01-01 00:00:00")

            val seconds = (l / 1000).toInt()

            LogUtils.e("seconds $seconds")
            HexUtil.intToBytes(seconds).forEach {
                //定位时间
                arrayList.add(it.toByte())
            }
        }

        if(isConnectedSocket){
            saveLog(HexUtil.formatHexString(arrayList.toByteArray(),true))
            EasySocket.getInstance().upMessage(arrayList.toByteArray())
        } else{
            saveLog(HexUtil.formatHexString(arrayList.toByteArray(),true) + "\n socket 未连接 测试用")
            ToastUtils.showShort("socket 未连接")
        }

    }

    private fun saveLog(s: String){
        val transformInfo = TransformInfo()
        transformInfo.message = s
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val format = simpleDateFormat.format(Date(System.currentTimeMillis()))
        transformInfo.time = format
        transformInfo.type = "tcp"
        transformInfo.save()
    }

    private fun onSocketConnectStateChange() {
        Handler(Looper.getMainLooper()).post {
            if (isConnectedSocket) {
                bt_connect.text = "已连接"
            } else {
                bt_connect.text = "连接失败,请重试"
            }
            bt_connect.isEnabled = true
        }
    }

    private fun checkResult(isWatch: Boolean): Boolean {
        val numberS = et_number.text.toString()
        val stateS = et_state.text.toString()
        val heartS = et_heart.text.toString()
        val oxygenS = et_oxygen.text.toString()
        val bloodS = et_blood.text.toString()
        val temperatureS = et_temperature.text.toString()
        val pressureS = et_pressure.text.toString()
        if (isWatch) {
            val b = numberS.isNotEmpty() && stateS.isNotEmpty()  &&
                    heartS.isNotEmpty() && oxygenS.isNotEmpty() && bloodS.isNotEmpty() && temperatureS.isNotEmpty() && pressureS.isNotEmpty()
            if (!b) {
                if (!checkAndToast(et_number)) {
                    return false
                }

                if (!checkAndToast(et_state)) {
                    return false
                }



                if (!checkAndToast(et_heart)) {
                    return false
                }

                if (!checkAndToast(et_oxygen)) {
                    return false
                }

                if (!checkAndToast(et_blood)) {
                    return false
                }

                if (!checkAndToast(et_temperature)) {
                    return false
                }

                if (!checkAndToast(et_pressure)) {
                    return false
                }
            } else {
                return true
            }
        } else {
            return checkAndToast(et_number)
        }

        return false
    }


    fun binaryString(num: Int): String? {
        var num = num
        val result = StringBuilder()
        val flag = 1 shl 7
        for (i in 0..7) {
            val `val` = if (flag and num == 0) 0 else 1
            result.append(`val`)
            num = num shl 1
        }
        return result.toString()
    }


    private fun showPicker(
        data: ArrayList<String>,
        title: String,
        editText: EditText,
        callback: (s: String) -> Unit
    ) {

        //显示选择框
        val pvOptions: OptionsPickerView<String> =
            OptionsPickerBuilder(this) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
                callback(data[options1])
                editText.setText(data[options1])
            }.build<String>()


        //当前选中下标
        var currentIndex = 0

        val s = editText.text.toString()
        data.forEachIndexed { index, d ->
            if (d == s) {
                currentIndex = index
            }
        }

        pvOptions.setSelectOptions(currentIndex)
        pvOptions.setPicker(data)
        pvOptions.setTitleText(title)
        pvOptions.show()
    }


    override fun onResume() {
        super.onResume()
        try {
            onSocketConnectStateChange()
        } catch (e: Exception) {

        }

    }


    private fun checkAndToast(et: EditText): Boolean {
        return if (et.text.toString().isEmpty()) {
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }


    fun LngLatByte2Float(b: ByteArray): Float {
        if (b.size != 4) return 0f
        var d = 0x00
        for (i in b.indices) {
            val byte = b[b.size - i - 1]
            d = d or ((byte.toInt()).shl(8)  * i)
        }

        //将int值转换成小数表示的经纬度
        d /= 3
        val intPart = d / (60 * 10000)
        val divPart = d - intPart * 60 * 10000 //余数部分
        val floatPart = divPart.toFloat() / 600000
        return intPart + floatPart
    }


    fun latLng2Dfm(du: Double,isLongitude:Boolean): ArrayList<Byte>? {
        val arrayList = ArrayList<Byte>()


        val unit = if(isLongitude) 360 else 180
        val d = du / (unit.div(Math.pow(2.toDouble(), 32.toDouble())))


        val bytes = HexUtil.intToBytes(d.toInt())


        val formatHexString = HexUtil.formatHexString(bytes, true)

        LogUtils.e("formatHexString $formatHexString  $du  $isLongitude")
        bytes.forEach {
            arrayList.add(it)
        }

        return arrayList
    }


    private fun gettimeStemp(time: String?): Long {
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var timeStemp: Long = 0
        try {
            val date: Date = simpleDateFormat.parse(time)
            timeStemp = date.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeStemp
    }


    fun longToBytes(data: Long): ByteArray? {
        val res = ByteArray(8)
        for (i in res.indices) {
            res[i] = (data shr 8 * i).toByte()
        }
        return res
    }


    override fun onDestroy() {
        super.onDestroy()
                        try {
                    EasySocket.getInstance().destroyConnection()
                } catch (e: Exception) {

                }
    }

}