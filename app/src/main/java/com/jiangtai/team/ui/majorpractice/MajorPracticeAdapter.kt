package com.jiangtai.team.ui.majorpractice

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.team.R
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.TaskBean
import com.jiangtai.team.widget.SlideWrapper
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author xiezekai
 * @des 专项计划列表 -- 适配器
 * @create time 2021/8/4 3:25 下午
 */
class MajorPracticeAdapter (private val onSlideClickListener: OnSlideClickListener):
    BaseQuickAdapter<TaskBean, BaseViewHolder>(R.layout.recycle_item_practice) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: TaskBean?) {
        val taskName = helper.getView<TextView>(R.id.task_name)
        val startTime = helper.getView<TextView>(R.id.start_time)
        val endTime = helper.getView<TextView>(R.id.end_time)
        val taskState = helper.getView<TextView>(R.id.task_state)
        val rwrs = helper.getView<TextView>(R.id.rwrs)
        val slide = helper.getView<SlideWrapper>(R.id.slide)
        val viewtow = helper.getView<View>(R.id.viewtow)
        val viewone = helper.getView<View>(R.id.viewone)
        val task_jxz = helper.getView<TextView>(R.id.task_jxz)
        val task_over = helper.getView<TextView>(R.id.task_over)

        helper.setVisible(R.id.task_state,true)
        item?.let {
             taskName.text = "计划名称："+it.taskName
            val find =
                LitePal.where("taskId = ?", it.taskId).find(Project::class.java)
            rwrs.text="任务数:"+find.size

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = Date(it.startTime)
                startTime.text = sdf.format(date)
                endTime.text = sdf.format(Date(it.endTime))
            } catch (e: Exception) {
                LogUtils.e("MajorPracticeAdapter parse error is $e")
            }


            when (it.state) {
                0 -> {
//                    val viewtow =
//                    val viewone =
//                    val task_jxz =
//                    val task_over
                    viewtow.setBackgroundColor(mContext.getColor(R.color.my_color))
                    viewone.setBackgroundColor(mContext.getColor(R.color.my_color))
                    task_over.setTextColor(mContext.getColor(R.color.my_color))
                    task_jxz.setTextColor(mContext.getColor(R.color.my_color))
                    taskState.setTextColor(mContext.getColor(R.color.theme_color))
                    //未开始
                 //   taskState.text = "未开始"
                }

                1 -> {
                    //进行中
                  //  taskState.text = "进行中"
                    viewtow.setBackgroundColor(mContext.getColor(R.color.my_color))
                    viewone.setBackgroundColor(mContext.getColor(R.color.theme_color))
                    task_over.setTextColor(mContext.getColor(R.color.my_color))
                    task_jxz.setTextColor(mContext.getColor(R.color.theme_color))
                    taskState.setTextColor(mContext.getColor(R.color.theme_color))
                }

                2 -> {
                    //已结束
                 //   taskState.text = "已结束"
                    viewtow.setBackgroundColor(mContext.getColor(R.color.theme_color))
                    viewone.setBackgroundColor(mContext.getColor(R.color.theme_color))
                    task_over.setTextColor(mContext.getColor(R.color.theme_color))
                    task_jxz.setTextColor(mContext.getColor(R.color.theme_color))
                    taskState.setTextColor(mContext.getColor(R.color.theme_color))
                }
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