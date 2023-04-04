package com.jiangtai.count.ui.data

import android.app.CPManager
import android.content.Context
import android.content.LoRaManager
import android.content.SndData
import android.widget.EditText
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.TransformInfo
import com.jiangtai.count.util.BCDDecode
import com.jiangtai.count.util.HexUtil
import kotlinx.android.synthetic.main.activity_source_manager.*
import kotlinx.android.synthetic.main.activity_source_manager.bt_commit
import kotlinx.android.synthetic.main.activity_source_manager.et_number
import kotlinx.android.synthetic.main.activity_source_manager.iv_back
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SourceManagerActivity : BaseActivity() {


    private val transportList :ArrayList<String> = ArrayList()
    private val bandwidthList :ArrayList<String> = ArrayList()
    private val spList :ArrayList<String> = ArrayList()


    private var cp : CPManager ?= null
    override fun attachLayoutRes(): Int {
        return R.layout.activity_source_manager
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        //信道
        transportList.add("422.0")
        transportList.add("422.2")
        transportList.add("422.4")
        transportList.add("422.6")
        transportList.add("422.8")
        transportList.add("423.0")
        transportList.add("423.2")
        transportList.add("423.4")
        transportList.add("423.6")
        transportList.add("423.8")
        transportList.add("424.0")


        //带宽
        bandwidthList.add("62.5")
        bandwidthList.add("125")
        bandwidthList.add("250")
        bandwidthList.add("500")




        //扩频因子
        spList.add("5")
        spList.add("6")
        spList.add("7")
        spList.add("8")
        spList.add("9")
        spList.add("10")
        spList.add("11")
        spList.add("12")



        iv_back.setOnClickListener {
            finish()
        }


        et_transport.setOnClickListener {
            showPicker(transportList,"信道",et_transport){

            }
        }

        et_bandwidth.setOnClickListener {
            showPicker(bandwidthList,"带宽",et_bandwidth){

            }
        }

        et_sp.setOnClickListener {
            showPicker(spList,"扩频因子",et_sp){

            }
        }


        bt_commit.setOnClickListener {
            if (checkResult()) {
                //人员编码
                val byteList = ArrayList<Byte>()

                val str2bcd = str2bcd(et_number.text.toString())
                val transportStr = et_transport.text.toString()
                val bandwidthStr = et_bandwidth.text.toString()
                val stStr = et_st.text.toString()
                val capacityStr = et_capacity.text.toString()

                var transportI = 0
                for(i in 0 until transportList.size){
                    val c = transportList[i]
                    if(c == transportStr){
                        transportI = i
                        break
                    }
                }


                var bandwidthI = 0
                for(i in 0 until bandwidthList.size){
                    val c = bandwidthList[i]
                    if(c == bandwidthStr){
                        bandwidthI = i
                        break
                    }
                }


                byteList.add(0xA3.toByte())
                byteList.addAll(str2bcd.toList())
                byteList.add(transportI.toByte())
                byteList.add(bandwidthI.toByte())
                byteList.add(stStr.toByte())
                byteList.add(capacityStr.toByte())



                try {
                    val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
                    val sndData = SndData(byteList.toByteArray(), byteList.size)
                    saveLog(HexUtil.formatHexString(byteList.toByteArray(),true) + "\n 400M 未连接 测试用")
                    loRaManager.sendContent(sndData)
                } catch (e: java.lang.Exception) {

                }

            }
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

    private fun checkResult(): Boolean {

        val check = (et_number.text.isNotEmpty() && et_transport.text.isNotEmpty()
                && et_bandwidth.text.isNotEmpty() && et_sp.text.isNotEmpty()
                && et_st.text.isNotEmpty() && et_capacity.text.isNotEmpty())

        if (!check) {
            if (!checkAndToast(et_number)) {
                return false
            }
            if (!checkAndToast(et_transport)) {
                return false
            }
            if (!checkAndToast(et_bandwidth)) {
                return false
            }
            if (!checkAndToast(et_sp)) {
                return false
            }
            if (!checkAndToast(et_st)) {
                return false
            }
            if (!checkAndToast(et_capacity)) {
                return false
            }
        }

        return check
    }

    override fun initListener() {

    }

    private fun str2bcd(str: String): ByteArray {
        return BCDDecode.str2Bcd(str)
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


    private fun saveLog(s: String){
        val transformInfo = TransformInfo()
        transformInfo.message = s
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val format = simpleDateFormat.format(Date(System.currentTimeMillis()))
        transformInfo.time = format
        transformInfo.type = "400M"
        transformInfo.save()
    }
}