package com.jiangtai.count.ui.data

import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import kotlinx.android.synthetic.main.activity_manager_test.*

class ManagerTestActivity : BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_manager_test
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun initListener() {

    }
}