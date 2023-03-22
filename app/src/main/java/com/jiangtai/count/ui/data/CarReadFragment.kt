package com.jiangtai.count.ui.data

import android.view.View
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_car_fix.bt_commit

class CarReadFragment: BaseFragment() {
    private var isUpdate = false
    override fun attachLayoutRes(): Int {
        return R.layout.fragment_car_read
    }

    override fun initView(view: View) {
        bt_commit.setOnClickListener {

        }
    }

    override fun lazyLoad() {

    }


}