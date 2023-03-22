package com.jiangtai.count.ui.beidoureceiver

import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.BeiDouSendMessageBean
import com.lhzw.libcc.LibccInterface
import kotlinx.android.synthetic.main.activity_beidou_receiver_history.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

class BeiDouSendHistoryActivity:BaseActivity() {
    override fun attachLayoutRes(): Int {
       return R.layout.activity_beidou_receiver_history
    }

    override fun initData() {
        val find = LitePal.findAll(BeiDouSendMessageBean::class.java)
        if(find.size > 0){
            (list.adapter as BeiDouSendHistoryAdapter).setNewData(find)
        } else {
            (list.adapter as BeiDouSendHistoryAdapter).setNewData(find)
            (list.adapter as BeiDouSendHistoryAdapter).setEmptyView(R.layout.empty_view, list)
        }
        (list.adapter as BeiDouSendHistoryAdapter).notifyDataSetChanged()
    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)
        iv_back.setOnClickListener {
            finish()
        }
        list.layoutManager = LinearLayoutManager(this)
        val majorProjectAdapter = BeiDouSendHistoryAdapter()

        list.adapter = majorProjectAdapter
        (list.adapter as BeiDouSendHistoryAdapter).setEmptyView(R.layout.empty_view, list)
    }

    override fun initListener() {

    }


    inner class BeiDouSendHistoryAdapter :
        BaseQuickAdapter<BeiDouSendMessageBean,BaseViewHolder>(R.layout.recycler_beidou_send_message){
        override fun convert(helper: BaseViewHolder, item: BeiDouSendMessageBean?) {
            val messageId = helper.getView<TextView>(R.id.message_id)
            val messageOrder = helper.getView<TextView>(R.id.message_order)
            val messageTime = helper.getView<TextView>(R.id.send_time)




            item?.let {
                try {
                    val uncompressMessageHead = LibccInterface.uncompressMessageHead(it.content)
                    val dataId = uncompressMessageHead.dataId
                    val order = uncompressMessageHead.order
                    messageId.text = dataId.toString()
                    messageOrder.text = order.toString()
                }catch (e:java.lang.Exception){

                }

                try {
                    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                    if(item.time.isNotEmpty()){
                        val date = Date(item.time.trim().toLong())
                        messageTime.text = sdf.format(date)
                    }

                } catch (e: Exception) {
                    LogUtils.e(" parse error is $e ${item.time}")
                }
            }


        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = true)
    fun refreshTaskState(e: BeiDouSendMessageBean) {
        EventBus.getDefault().removeStickyEvent(e)
        runOnUiThread {
            initData()
        }
    }
}