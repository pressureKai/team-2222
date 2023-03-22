package com.jiangtai.count.ui.data

import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.activity_search_data.*
import kotlinx.android.synthetic.main.fragment_data.list
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchDataActivity : BaseActivity() {
    private var dataItemClickListener:DataAdapter.ItemClickListener ?= null
    private val recordList = ArrayList<CountRecordBean>()
    override fun attachLayoutRes(): Int {
        return R.layout.activity_search_data
    }

    override fun useEventBus(): Boolean {
        return true
    }
    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        dataItemClickListener = object :DataAdapter.ItemClickListener{
            override fun itemClick(countBean: CountRecordBean) {
                countBean.startActivity()
            }
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = DataAdapter(this,ArrayList(),dataItemClickListener!!)

        iv_back.setOnClickListener {
            finish()
        }
        tv_search.setOnClickListener {
            val sSearch = et_search.text.toString()
            if(sSearch.isNotEmpty()){
                getData(sSearch)
            } else {
                ToastUtils.showShort(et_search.text.toString())
            }
        }
    }

    override fun initListener() {

    }


    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onDeleteData(e: DeleteBean) {
        EventBus.getDefault().removeStickyEvent(e)


        var beDeleteIndex = -1

        recordList.forEachIndexed { index, countRecordBean ->
            if(countRecordBean.recordID == e.beDeleteID){
                beDeleteIndex = index
            }
        }
        LogUtils.e("remove index $beDeleteIndex  ${e.beDeleteID}")
        if(beDeleteIndex != -1){
            recordList.removeAt(beDeleteIndex)
            runOnUiThread {
                list.adapter = DataAdapter(this,recordList,dataItemClickListener!!)
            }
        }

    }


    private fun getData(s :String){
        val find = LitePal.where("loginId = ? ", CommonUtil.getLoginUserId()).find(
            CountRecordBean::class.java
        )

        recordList.clear()
        find.forEach { get->
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd  hh:mm")
            val sDate = simpleDateFormat.format(Date(get.recordTime.toLong()))
            val message = when (get.recordType) {
                CountRecordBean.DEVICE_TYPE -> {
                    "$sDate${"车辆日常"}记录单"
                }
                CountRecordBean.CAR_FIX_TYPE -> {
                    "$sDate${"车辆维修"}记录单"
                }
                CountRecordBean.DEVICE_FILL_TYPE -> {
                    "$sDate${"设施监控"}记录单"
                }
                CountRecordBean.OIL_TYPE -> {
                    "$sDate${"YL日清"}记录单"
                }
                CountRecordBean.WEATHER_TYPE -> {
                    "$sDate${"气象信息"}记录单"
                }
                CountRecordBean.HELICOPTER_OIL_TYPE -> {
                    "$sDate${"直升机加油"}记录单"
                }
                CountRecordBean.AIR_TYPE -> {
                    "$sDate${"空投物资采集"}记录单"
                }

                else -> {
                    "$sDate${"未知"}记录单"
                }
            }

            if(message.contains(s)){
                recordList.add(get)
            }
        }

        if(recordList.size == 0){
            ToastUtils.showShort("暂无记录")
        } else {
            recordList.reverse()
            list.adapter = DataAdapter(this,recordList,dataItemClickListener!!)
        }

    }
}