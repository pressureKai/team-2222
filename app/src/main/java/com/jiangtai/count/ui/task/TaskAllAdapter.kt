package com.jiangtai.count.ui.task

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.TaskBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.DateUtil
import com.jiangtai.count.widget.SlideWrapper

/**
 * Created by heCunCun on 2021/3/8
 */
class TaskAllAdapter(private val onSlideClickListener: OnSlideClickListener) : BaseQuickAdapter<TaskBean, BaseViewHolder>(R.layout.item_task_list) {
    override fun convert(helper: BaseViewHolder, item: TaskBean?) {
        item ?: return
        helper.setText(R.id.tv_task_name, item.taskName)
        helper.setText(R.id.tv_id, item.taskId)

        helper.setText(R.id.tv_start_time, DateUtil.changeLongToDate(item.startTime))
        helper.setText(R.id.tv_end_time, DateUtil.changeLongToDate(item.endTime))

        val slide = helper.getView<SlideWrapper>(R.id.slide)
        val ivTaskState = helper.getView<ImageView>(R.id.iv_task_state)
        when (item.state) {
            Constant.STATE_PRE -> {
                helper.setText(R.id.tv_task_state, "未开始")
                ivTaskState.setImageResource(R.mipmap.ic_state_pre)
            }
            Constant.STATE_RUNNING -> {
                helper.setText(R.id.tv_task_state, "进行中")
                ivTaskState.setImageResource(R.mipmap.ic_state_running)
            }
            Constant.STATE_FINISHED -> {
                helper.setText(R.id.tv_task_state, "已结束")
                ivTaskState.setImageResource(R.mipmap.ic_state_finish)
            }
        }

        slide.setEnableSlide(true)
        slide.setOnClickListener {
            onSlideClickListener.onClick(helper.position,0)
            slide.strongClose()
        }
        slide.setmSlideControlViewClickListener(object :SlideWrapper.SlideControlViewClickListener{
            override fun onSlideControlViewClickListener(type: Int) {
                onSlideClickListener.onClick(helper.position,1)
            }
        })
    }

    public interface OnSlideClickListener{
        fun onClick(position:Int,type:Int)
    }


}