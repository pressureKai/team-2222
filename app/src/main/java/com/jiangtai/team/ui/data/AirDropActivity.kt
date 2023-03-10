package com.jiangtai.team.ui.data

import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.AirDropInfo
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.util.CommonUtil
import kotlinx.android.synthetic.main.activity_air_drop.*
import java.util.*

class AirDropActivity: BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_air_drop
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }

        bt_commit.setOnClickListener {
             if(checkResult()){
                 val sEtNumber = et_number.text.toString()
                 val sEtName = et_name.text.toString()
                 val sEtNotice = et_notice.text.toString()

                 val airDropInfo = AirDropInfo()
                 airDropInfo.ryid = CommonUtil.getLoginUserId()
                 airDropInfo.wzbh = sEtNumber
                 airDropInfo.wzjszt = "已接收"
                 airDropInfo.sjsj = airDropInfo.recordTime
                 airDropInfo.wzmc = sEtName
                 airDropInfo.wzbz = sEtNotice
                 airDropInfo.save()

                 ToastUtils.showShort("接收成功")
                 finish()
             }
        }
    }

    private fun checkResult() :Boolean{
        val sEtNumber = et_number.text.toString()
        val sEtName = et_name.text.toString()
        val sEtNotice = et_notice.text.toString()

        val isChecked = sEtNumber.isNotEmpty() && sEtName.isNotEmpty() &&  sEtNotice.isNotEmpty()
        if(!isChecked){
            if(sEtNumber.isEmpty()){
                ToastUtils.showShort("请输入编号")
                return false
            }

            if(sEtName.isEmpty()){
                ToastUtils.showShort("请输入名称")
                return false
            }

            if(sEtName.isEmpty()){
                ToastUtils.showShort("请输入名称")
                return false
            }
        } else {
            return true
        }

        return false

    }

    override fun initListener() {

    }
}