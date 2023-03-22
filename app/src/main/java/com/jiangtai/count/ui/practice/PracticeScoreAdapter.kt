package com.jiangtai.count.ui.practice

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.NormalScore
import com.jiangtai.count.util.DateUtil
import com.jiangtai.count.widget.SlideWrapper

/**
 * Created by heCunCun on 2021/3/9
 */
class PracticeScoreAdapter(private val onSlideClickListener: OnSlideClickListener):BaseQuickAdapter<NormalScore,BaseViewHolder>(R.layout.item_practice_score) {
    override fun convert(helper: BaseViewHolder, item: NormalScore?) {
        item?:return
        helper.setText(R.id.tv_task_name,item.projectName)
        helper.setText(R.id.tv_start_time,DateUtil.changeLongToDate(item.startTime!!))
        helper.setText(R.id.tv_end_time,DateUtil.changeLongToDate(item.uploadTime!!))

        val slide = helper.getView<SlideWrapper>(R.id.slide)

        if (item.score!=""){
            helper.setText(R.id.tv_score,item.score.toString())
        }else{
            helper.setText(R.id.tv_score,"计划中")
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