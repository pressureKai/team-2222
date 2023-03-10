package com.jiangtai.team.ui.data

import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import kotlinx.android.synthetic.main.activity_helicopter_oil.*

class HelicopterOilActivity:BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_helicopter_oil
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun initListener() {

    }
}