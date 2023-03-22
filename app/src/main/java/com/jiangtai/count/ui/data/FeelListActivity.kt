package com.jiangtai.count.ui.data

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.FeelListBean
import com.jiangtai.count.ui.adapter.FeelListAdapter
import kotlinx.android.synthetic.main.activity_feel_list.*
import java.util.*
import kotlin.collections.ArrayList

class FeelListActivity : BaseActivity() {


    override fun attachLayoutRes(): Int {
        return R.layout.activity_feel_list
    }

    override fun initData() {

    }

    override fun initView() {
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = FeelListAdapter()

        (list.adapter as FeelListAdapter).setOnItemClickListener { adapter, view, position ->
            val feelListBean = adapter.data[position] as FeelListBean
            val intent = Intent(this@FeelListActivity,FeelDetailActivity::class.java)
            intent.putExtra("result",feelListBean)
            startActivity(intent)
        }
        iv_back.setOnClickListener {
            finish()
        }
        test()
    }

    fun test() {

        val arrayList = ArrayList<FeelListBean>()
        for (i in 1..100) {

            val feelListBean = FeelListBean()

            val topLongitude = Random().nextDouble() + 114
            val bottomLongitude = Random().nextDouble() + 113

            val bottomLatitude = Random().nextDouble() + 22
            val topLatitude = Random().nextDouble() + 23




            feelListBean.topLatitude = topLatitude
            feelListBean.bottomLatitude = bottomLatitude
            feelListBean.topLongitude = topLongitude
            feelListBean.bottomLongitude = bottomLongitude



            feelListBean.time = System.currentTimeMillis().toString()
            feelListBean.type = "磁场"
            feelListBean.targetType = "车辆"
            arrayList.add(feelListBean)
        }

        (list.adapter as FeelListAdapter).addData(arrayList)
        (list.adapter as FeelListAdapter).notifyDataSetChanged()
    }

    override fun initListener() {

    }
}