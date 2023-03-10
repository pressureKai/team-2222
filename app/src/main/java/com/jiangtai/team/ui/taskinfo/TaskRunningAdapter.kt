package com.jiangtai.team.ui.taskinfo

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.team.R
import com.jiangtai.team.bean.Project

/**
 * Created by heCunCun on 2021/3/8
 */
class TaskRunningAdapter:BaseQuickAdapter<Project,BaseViewHolder>(R.layout.item_task_info) {
    override fun convert(helper: BaseViewHolder, item: Project?) {
        item?:return
        helper.addOnClickListener(R.id.iv_more)
        helper.addOnClickListener(R.id.iv_change_state)
        helper.setImageResource(R.id.iv_change_state,R.mipmap.ic_state_to_finish)
        helper.setVisible(R.id.iv_change_state,false)
        helper.setText(R.id.tv_task_name,item.projectName)
        helper.setText(R.id.tv_id,item.projectId)
    }
}