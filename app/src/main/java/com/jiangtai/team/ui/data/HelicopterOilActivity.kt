package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.HelicopterOilInfoBean
import kotlinx.android.synthetic.main.activity_helicopter_oil.*
import kotlinx.android.synthetic.main.activity_helicopter_oil.bt_commit
import kotlinx.android.synthetic.main.activity_helicopter_oil.et_notice
import kotlinx.android.synthetic.main.activity_helicopter_oil.et_number
import kotlinx.android.synthetic.main.activity_helicopter_oil.iv_back
import kotlinx.android.synthetic.main.activity_oil.*

class HelicopterOilActivity : BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_helicopter_oil
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
            val sTime = et_time.text.toString()
            val sOilCover = et_oil_cover.text.toString()
            val sType = et_type.text.toString()
            val sBrandNumber = et_brand_number.text.toString()
            val sPurity = et_oil_purity.text.toString()
            val sOilWater = et_oil_water.text.toString()
            val sNotice = et_notice.text.toString()
            val sOilPerson = et_add_oil_person.text.toString()
            val sOilCaptain = et_oil_captain.text.toString()
            val sOilOther = et_oil_other.text.toString()
            val helicopterOilInfoBean = HelicopterOilInfoBean()
            helicopterOilInfoBean.ygch = sNumber
            helicopterOilInfoBean.jysj = sTime
            helicopterOilInfoBean.jylx = sType
            helicopterOilInfoBean.jyl = sOilCover
            helicopterOilInfoBean.yppph = sBrandNumber
            helicopterOilInfoBean.ypcd = sPurity
            helicopterOilInfoBean.yphsl = sOilWater
            helicopterOilInfoBean.tjj = "无"
            helicopterOilInfoBean.bz = sNotice
            helicopterOilInfoBean.jyy = sOilPerson
            helicopterOilInfoBean.jz = sOilCaptain
            helicopterOilInfoBean.qtry = sOilOther


            helicopterOilInfoBean.save()
            ToastUtils.showShort("保存成功")

            finish()
        }
    }

    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sTime = et_time.text.toString()
        val sOilCover = et_oil_cover.text.toString()
        val sType = et_type.text.toString()
        val sBrandNumber = et_brand_number.text.toString()
        val sPurity = et_oil_purity.text.toString()
        val sOilWater = et_oil_water.text.toString()
        val sNotice = et_notice.text.toString()
        val sOilPerson = et_add_oil_person.text.toString()
        val sOilCaptain = et_oil_captain.text.toString()
        val sOilOther = et_oil_other.text.toString()


        val isChecked = sNumber.isNotEmpty() && sTime.isNotEmpty()
                && sOilCover.isNotEmpty() && sType.isNotEmpty()
                && sBrandNumber.isNotEmpty() && sPurity.isNotEmpty()
                && sOilWater.isNotEmpty() && sNotice.isNotEmpty()
                && sOilPerson.isNotEmpty() && sOilCaptain.isNotEmpty() && sOilOther.isNotEmpty()


        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }

            if (!checkAndToast(et_time)) {
                return false
            }
            if (!checkAndToast(et_oil_cover)) {
                return false
            }
            if (!checkAndToast(et_type)) {
                return false
            }
            if (!checkAndToast(et_brand_number)) {
                return false
            }
            if (!checkAndToast(et_oil_purity)) {
                return false
            }
            if (!checkAndToast(et_oil_water)) {
                return false
            }
            if (!checkAndToast(et_notice)) {
                return false
            }
            if (!checkAndToast(et_add_oil_person)) {
                return false
            }
             if (!checkAndToast(et_oil_captain)) {
                return false
            }
             if (!checkAndToast(et_oil_other)) {
                return false
            }

        }


        return isChecked
    }

    override fun initListener() {

    }


    private fun checkAndToast(et: EditText): Boolean {
        return if (et.text.toString().isEmpty()) {
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }
}