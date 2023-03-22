package com.jiangtai.count.ui.beidoureceiver

import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.BeiDouReceiveBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.Preference
import com.lhzw.libcc.LibccInterface
import kotlinx.android.synthetic.main.activity_beidou_receiver_history.iv_back
import kotlinx.android.synthetic.main.activity_beidou_receiver_history.list
import kotlinx.android.synthetic.main.activity_beidou_receiver_history.toolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

class BeiDouReceiverHistoryActivity: BaseActivity() {
    private var startID: Int by Preference(Constant.START_ID,1)
    override fun attachLayoutRes(): Int {
        return R.layout.activity_beidou_receiver_history
    }


    override fun useEventBus(): Boolean {
        return true
    }

    override fun initData() {
        val find = LitePal.findAll(BeiDouReceiveBean::class.java)

        if(find.size > 0){
            (list.adapter as BeiDouReceiverHistoryAdapter).setNewData(find)
        } else {
            (list.adapter as BeiDouReceiverHistoryAdapter).setNewData(find)
            (list.adapter as BeiDouReceiverHistoryAdapter).setEmptyView(R.layout.empty_view, list)
        }
        (list.adapter as BeiDouReceiverHistoryAdapter).notifyDataSetChanged()
    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)
        iv_back.setOnClickListener {
            finish()
        }
        list.layoutManager = LinearLayoutManager(this)
        val majorProjectAdapter = BeiDouReceiverHistoryAdapter()

        list.adapter = majorProjectAdapter
        (list.adapter as BeiDouReceiverHistoryAdapter).setEmptyView(R.layout.empty_view, list)
    }

    override fun initListener() {

    }


    inner class BeiDouReceiverHistoryAdapter:BaseQuickAdapter<BeiDouReceiveBean,BaseViewHolder>(R.layout.recycler_beidou_history){
        override fun convert(helper: BaseViewHolder, item: BeiDouReceiveBean?) {
            val content = helper.getView<TextView>(R.id.content)
            val time = helper.getView<TextView>(R.id.time)

            item?.let {
                try {
                    LogUtils.e("content is ${item.content}")
                    val split = item.content.split("content is")
                    for(value in split){
                        LogUtils.e("s is $value")
                    }
                    val s = split.last().trim().replace("\\n","")

                    if(s.isNotEmpty()){
                        val uncompressCommand = LibccInterface
                            .uncompressCommand(s)

                        var time = ""
                        try {
                            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                            if(item.time.isNotEmpty()){
                                val date = Date( uncompressCommand.time.toLong())
                                time =  sdf.format(date)
                            }

                        } catch (e: Exception) {
                            LogUtils.e(" parse error is $e ${item.time}")
                        }

                        val uncompressMessageHead = LibccInterface.uncompressMessageHead(s)
                        val dataId = uncompressMessageHead.dataId
                        content.text = "dataId is $dataId current dataId is $startID $time"
                    } else {
                        content.text = item.content
                    }

                }catch (e:Exception){
                    content.text = item.content
                }


                try {
                    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                    if(item.time.isNotEmpty()){
                        val date = Date(item.time.trim().toLong())
                        time.text = sdf.format(date)
                    }

                } catch (e: Exception) {
                    LogUtils.e(" parse error is $e ${item.time}")
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = true)
    fun refreshTaskState(e: BeiDouReceiveBean) {
        EventBus.getDefault().removeStickyEvent(e)
        runOnUiThread {
            initData()
        }
    }
}