package com.jiangtai.count.ui.person

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.Score
import com.jiangtai.count.constant.Constant

/**
 * Created by heCunCun on 2021/3/8
 */
class PersonScoreDetailAdapter:BaseQuickAdapter<Score,BaseViewHolder>(R.layout.item_person_score_detail) {
    override fun convert(helper: BaseViewHolder, item: Score?) {
       item?:return
        helper.setText(R.id.tv_name,item.projectName)
        helper.setText(R.id.tv_id,item.projectId)
       when(item.state){
           Constant.STATE_PRE->{
               helper.setText(R.id.tv_task_state,"未开始")
           }
           Constant.STATE_RUNNING->{
               helper.setText(R.id.tv_task_state,"考核中")
           }
           Constant.STATE_FINISHED->{
               helper.setText(R.id.tv_task_state,"已完成")
           }
       }
        helper.setText(R.id.tv_score,item.score)
    }
}