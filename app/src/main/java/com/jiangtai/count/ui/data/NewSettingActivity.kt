package com.jiangtai.count.ui.data

import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.Preference
import kotlinx.android.synthetic.main.activity_new_login.bt_commit
import kotlinx.android.synthetic.main.activity_new_setting.*

class NewSettingActivity: BaseActivity() {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    override fun attachLayoutRes(): Int {
        return R.layout.activity_new_setting
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        et_server_ip.setText(serverIp)
        bt_commit.setOnClickListener {
            if(et_server_ip.text.toString().isNotEmpty()){
                serverIp = et_server_ip.text.toString()
                ToastUtils.showShort("保存成功")
                finish()
            } else {
                ToastUtils.showShort(et_server_ip.hint)
            }
        }
    }

    override fun initListener() {

    }
}