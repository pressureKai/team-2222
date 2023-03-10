package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.bean.OilInfoBean
import kotlinx.android.synthetic.main.activity_oil.*
import java.util.*

class OilActivity : BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_oil
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        bt_commit.setOnClickListener {
            commit()
        }
    }

    private fun commit(){
        if(checkResult()){
            val sNumber = et_number.text.toString()
            val sName = et_name.text.toString()
            val sDate = et_date.text.toString()
            val sVolumeNumber = et_volume_number.text.toString()
            val sApparentDensity = et_apparent_density.text.toString()
            val sSurplusHeight = et_surplus_height.text.toString()
            val sOilTemperature = et_oil_temperature.text.toString()
            val sNotice = et_notice.text.toString()


            val oilInfoBean = OilInfoBean()

            oilInfoBean.gh = sNumber
            oilInfoBean.ypmc = sName
            oilInfoBean.sjsj = sDate
            oilInfoBean.rjbh = sVolumeNumber
            oilInfoBean.smd = sApparentDensity
            oilInfoBean.yg = sSurplusHeight
            oilInfoBean.yw = sOilTemperature
            oilInfoBean.bz = sNotice
            oilInfoBean.save()

            ToastUtils.showShort("保存成功")
            finish()
        }
    }


    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sName = et_name.text.toString()
        val sDate = et_date.text.toString()
        val sVolumeNumber = et_volume_number.text.toString()
        val sApparentDensity = et_apparent_density.text.toString()
        val sSurplusHeight = et_surplus_height.text.toString()
        val sOilTemperature = et_oil_temperature.text.toString()
        val sNotice = et_notice.text.toString()



        val isChecked = sNumber.isNotEmpty() && sName.isNotEmpty()
                && sDate.isNotEmpty() && sVolumeNumber.isNotEmpty()
                && sApparentDensity.isNotEmpty() && sSurplusHeight.isNotEmpty()
                && sOilTemperature.isNotEmpty() && sNotice.isNotEmpty()


        if (!isChecked) {
             if(!checkAndToast(et_number)){
                 return false
             }
             if(!checkAndToast(et_name)){
                 return false
             }
             if(!checkAndToast(et_date)){
                 return false
             }
             if(!checkAndToast(et_volume_number)){
                 return false
             }
             if(!checkAndToast(et_apparent_density)){
                 return false
             }
             if(!checkAndToast(et_surplus_height)){
                 return false
             }
             if(!checkAndToast(et_oil_temperature)){
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



    private fun checkAndToast(et:EditText):Boolean{
        return if(et.text.toString().isEmpty()){
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }
}