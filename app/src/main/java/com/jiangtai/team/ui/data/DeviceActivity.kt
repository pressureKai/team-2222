package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.DeviceFeelInfoBean
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.activity_device.bt_commit
import kotlinx.android.synthetic.main.activity_device.et_notice
import kotlinx.android.synthetic.main.activity_device.et_number
import kotlinx.android.synthetic.main.activity_device.iv_back

class DeviceActivity: BaseActivity() {
    private var smellMessage = "正常"
    private var deviceType = "DY库"
    override fun attachLayoutRes(): Int {
        return R.layout.activity_device
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }

        bt_commit.setOnClickListener {
            if(checkResult()){

                val sNumber = et_number.text.toString()
                val sTime = et_time.text.toString()
                val sTemperature = et_temperature.text.toString()
                val sNotice = et_notice.text.toString()

//                private String SSBH;
//                private String QWXX;
//                private String WD;
//                private String SJSJ;
//                private String SSLB;

                val deviceFeelInfoBean = DeviceFeelInfoBean()
                deviceFeelInfoBean.ssbh = sNumber
                deviceFeelInfoBean.qwxx = smellMessage
                deviceFeelInfoBean.sslb = deviceType
                deviceFeelInfoBean.wd = sTemperature
                deviceFeelInfoBean.sjsj = sTime
                deviceFeelInfoBean.bz = sNotice

                deviceFeelInfoBean.save()
            }
        }
    }

    private fun checkResult():Boolean{
        val sNumber = et_number.text.toString()
        val sTime = et_time.text.toString()
        val sTemperature = et_temperature.text.toString()
        val sNotice = et_notice.text.toString()

        val isChecked = sNumber.isNotEmpty() && sTime.isNotEmpty() && sTemperature.isNotEmpty() && sNotice.isNotEmpty()


        if(!isChecked){
            if(!checkAndToast(et_number)){
                return false
            }
            if(!checkAndToast(et_time)){
                return false
            }
            if(!checkAndToast(et_temperature)){
                return false
            }
            if(!checkAndToast(et_notice)){
                return false
            }
        }

        return isChecked

    }
    override fun initListener() {

    }

    private fun checkAndToast(et: EditText):Boolean{
        return if(et.text.toString().isEmpty()){
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }
}