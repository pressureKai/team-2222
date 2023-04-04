package com.jiangtai.count.ui.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.FeelListBean
import java.text.SimpleDateFormat
import java.util.Date

class FeelListAdapter : BaseQuickAdapter<FeelListBean, BaseViewHolder>(R.layout.item_feel_list)  {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: FeelListBean?) {
        val desTextView = helper.getView<TextView>(R.id.tv_des)
        val timeTextView = helper.getView<TextView>(R.id.tv_time)

        item?.let {
            desTextView.text = "${item.mblb}发现目标${item.fxmb}"
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
                val format = simpleDateFormat.format(Date(item.time.toLong()))
                timeTextView.text = format
            }catch (e:Exception){
                timeTextView.text = ""
            }

        }

    }
}