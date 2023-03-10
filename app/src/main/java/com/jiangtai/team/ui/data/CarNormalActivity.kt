package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.CarFixBean
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.bean.DeviceInfoBean
import com.jiangtai.team.util.CommonUtil
import com.jiangtai.team.util.GsonInstance
import kotlinx.android.synthetic.main.activity_car_normal.*
import org.litepal.LitePal
import java.util.*

class CarNormalActivity: BaseActivity() {
    private var isStandBy = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_car_normal
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
        tv_standby.setOnClickListener {

        }

        tv_execute.setOnClickListener {

        }
    }


    private fun commit(){
        if(checkResult()){
            val sNumber = et_number.text.toString()
            val sType = et_type.text.toString()
            //剩余油量
            val sOil = et_oil.text.toString()
            //累计摩托小时
            val sHour = et_hour.text.toString()
            //备注
            val sNotice = et_notice.text.toString()

            val deviceInfoBean = DeviceInfoBean()

            deviceInfoBean.vid = sNumber
            deviceInfoBean.zblb = sType
            deviceInfoBean.mtxs = sHour
            deviceInfoBean.yyl = sOil
            deviceInfoBean.bz = sNotice

            val find = LitePal.where(
                "WXDX = ? and loginId = ? ",
                sNumber,
                CommonUtil.getLoginUserId()
            ).find(
                CarFixBean::class.java
            )
            if(find.size  == 0){
                deviceInfoBean.gzbj = "正常"
            } else {
                deviceInfoBean.gzbj = "损坏"
                GsonInstance.instance?.gson?.let {
                    val toJson = it.toJson(find.first())
                    deviceInfoBean.zbwxxx = toJson
                }
            }

            deviceInfoBean.save()
            ToastUtils.showShort("保存成功")

            finish()
        }
    }

    private fun checkResult():Boolean{
        val sNumber = et_number.text.toString()
        val sType = et_type.text.toString()
        //剩余油量
        val sOil = et_oil.text.toString()
        //累计摩托小时
        val sHour = et_hour.text.toString()
        //备注
        val sNotice = et_notice.text.toString()

        val isChecked = sNumber.isNotEmpty()
                && sType.isNotEmpty() && sOil.isNotEmpty()
                && sHour.isNotEmpty() && sNotice.isNotEmpty()

        if(!isChecked){
            if(!checkAndToast(et_number)){
                return false
            }

            if(!checkAndToast(et_type)){
                return false
            }

            if(!checkAndToast(et_oil)){
                return false
            }

            if(!checkAndToast(et_hour)){
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