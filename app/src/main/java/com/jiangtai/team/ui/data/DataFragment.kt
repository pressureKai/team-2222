package com.jiangtai.team.ui.data

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_data.*
import org.litepal.LitePal

@SuppressLint("ValidFragment")
class DataFragment(private val dataType:Int) : BaseFragment() {
    private var dataItemClickListener:DataAdapter.ItemClickListener ?= null


    override fun attachLayoutRes(): Int {
        return R.layout.fragment_data
    }

    override fun initView(view: View) {

        dataItemClickListener = object :DataAdapter.ItemClickListener{
            override fun itemClick(countBean: CountRecordBean) {
                countBean.startActivity()
            }
        }
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = DataAdapter(requireContext(),ArrayList(),dataItemClickListener!!)
        getData()
    }

    override fun lazyLoad() {

    }


    private fun getData(){
        val find = LitePal.where("recordType = ? and loginId = ? ", dataType.toString(), CommonUtil.getLoginUserId()).find(
            CountRecordBean::class.java
        )

        if(find.size == 0){
            tv_empty.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            list.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            list.adapter = DataAdapter(requireContext(),find,dataItemClickListener!!)
        }
    }



}