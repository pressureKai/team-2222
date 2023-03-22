package com.jiangtai.count.ui.majorpracticedetail

import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author xiezekai
 * @des  专项任务列表子项列表适配器
 * @create time 2021/8/5 10:27 上午
 */
class MajorPracticeItemDetailAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.recycler_major_item_practice_detail) {
    override fun convert(helper: BaseViewHolder, item: String?) {
        val taskName = helper.getView<TextView>(R.id.task_name)
        val taskTime = helper.getView<TextView>(R.id.task_time)
        val taskContinueTime = helper.getView<TextView>(R.id.task_continue_time)

        taskName.text = item!!
        try {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val date = Date()
            taskTime.text = sdf.format(date)
        } catch (e: Exception) {
            LogUtils.e("MajorPracticeItemDetailAdapter parse error is $e")
        }

        taskContinueTime.text = "30h"
    }
}