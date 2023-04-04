package com.jiangtai.count.ui.data

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.FeelListBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.ui.adapter.FeelListAdapter
import com.jiangtai.count.util.Preference
import dismissLoading
import kotlinx.android.synthetic.main.activity_feel_list.*
import showLoading
import java.util.*
import kotlin.collections.ArrayList

class FeelListActivity : BaseActivity() {

    private var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")

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
        getFeelListFromServer()
       // test()
    }


    private fun getFeelListFromServer(){
        showLoading()
        val taskList = MyRetrofit.instance.api.getTargetList(loginUserId)
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<kotlin.collections.List<FeelListBean>>>() {
                override fun onSucceed(data: NewBaseBean<List<FeelListBean>>) {
                   dismissLoading()
                    runOnUiThread {
                        try {
                            val data1 = data.data
                            (list.adapter as FeelListAdapter).addData(data1)
                            (list.adapter as FeelListAdapter).notifyDataSetChanged()
                        }catch (e:Exception){
                            ToastUtils.showShort("error is $e")
                        }
                    }
                }

                override fun onFailed() {
                   dismissLoading()
                }
            })
    }

    fun test() {

        val arrayList = ArrayList<FeelListBean>()
        for (i in 1..100) {

            val feelListBean = FeelListBean()

            val topLongitude = Random().nextDouble() + 114
            val bottomLongitude = Random().nextDouble() + 113

            val bottomLatitude = Random().nextDouble() + 22
            val topLatitude = Random().nextDouble() + 23




            feelListBean.wdmin = topLatitude
            feelListBean.wdmax = bottomLatitude
            feelListBean.jdmin = topLongitude
            feelListBean.jdmax = bottomLongitude



            feelListBean.time = System.currentTimeMillis().toString()
            feelListBean.mblb = "磁场"
            feelListBean.fxmb = "车辆"
            arrayList.add(feelListBean)
        }

        (list.adapter as FeelListAdapter).addData(arrayList)
        (list.adapter as FeelListAdapter).notifyDataSetChanged()
    }

    override fun initListener() {

    }
}