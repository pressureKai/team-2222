package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.CarFixBean
import com.jiangtai.team.bean.DeviceInfoBean
import com.jiangtai.team.util.CommonUtil
import kotlinx.android.synthetic.main.activity_car_fix.*
import kotlinx.android.synthetic.main.activity_car_fix.bt_commit
import kotlinx.android.synthetic.main.activity_car_fix.et_name
import kotlinx.android.synthetic.main.activity_car_fix.et_notice
import kotlinx.android.synthetic.main.activity_car_fix.et_number
import kotlinx.android.synthetic.main.activity_car_fix.et_type
import kotlinx.android.synthetic.main.activity_car_fix.iv_back
import org.litepal.LitePal.where


class CarFixActivity: BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_car_fix
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
            val sType = et_type.text.toString()
            val sFixPart = et_fix_part.text.toString()
            val sFixTime = et_fix_time.text.toString()
            val sPerson = et_person.text.toString()
            val sNotice = et_notice.text.toString()


            val carFixBean = CarFixBean()

            carFixBean.wxdx = sNumber
            carFixBean.wxsj = sFixTime
            carFixBean.zblb = sType
            carFixBean.wxpj = sFixPart
            carFixBean.wxry = sPerson
            carFixBean.bz = sNotice

            val find = where("VID = ? and loginId = ? ", sNumber, CommonUtil.getLoginUserId()).find(
                DeviceInfoBean::class.java
            )

            if(find.size == 0){
                ToastUtils.showShort("您还未录入该设备的日常信息")
            }
            //保存维修信息
            carFixBean.save()

        }
    }

    private fun checkResult():Boolean{
        val sNumber = et_number.text.toString()
        val sType = et_type.text.toString()
        val sFixType = et_fix_part.text.toString()
        val sFixTime = et_fix_time.text.toString()
        val sPerson = et_person.text.toString()
        val sNotice = et_notice.text.toString()

        val isChecked = sNumber.isNotEmpty()  && sType.isNotEmpty()
                && sFixType.isNotEmpty() && sFixTime.isNotEmpty() && sPerson.isNotEmpty() && sNotice.isNotEmpty()

        if(!isChecked){
            if(!checkAndToast(et_number)){
                return false
            }
            if(!checkAndToast(et_name)){
                return false
            }
            if(!checkAndToast(et_type)){
                return false
            }
            if(!checkAndToast(et_fix_part)){
                return false
            }
            if(!checkAndToast(et_fix_time)){
                return false
            }
            if(!checkAndToast(et_person)){
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