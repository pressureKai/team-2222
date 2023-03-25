package com.jiangtai.count.ui.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.bean.TransformInfo
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_data.*
import org.litepal.LitePal

@SuppressLint("ValidFragment")
class LogDataFragment(val dataType: Int) : BaseFragment() {
    private var dataItemClickListener: LogDataAdapter.ItemClickListener? = null


    override fun attachLayoutRes(): Int {
        return R.layout.fragment_data
    }

    override fun initView(view: View) {

        dataItemClickListener = object : LogDataAdapter.ItemClickListener {
            override fun itemClick(countBean: CountRecordBean) {

            }
        }
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = LogDataAdapter(requireContext(), ArrayList(), dataItemClickListener!!)
        getData()
    }

    override fun lazyLoad() {

    }


    private fun getData() {
        val find = LitePal.findAll(
            TransformInfo::class.java
        )

        if (find.size == 0) {
            tv_empty.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            list.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            find.reverse()
            list.adapter = LogDataAdapter(requireContext(), find, dataItemClickListener!!)
        }
    }




}