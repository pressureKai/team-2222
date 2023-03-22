package com.jiangtai.count.ui.achievement

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.LocationInfoBean
import com.jiangtai.count.bean.Person
import com.jiangtai.count.bean.Project
import com.jiangtai.count.constant.Constant
import org.litepal.LitePal

/**
 * Created by heCunCun on 2021/3/8
 */
class AchievementAdapter : BaseQuickAdapter<Person, BaseViewHolder>(R.layout.item_achievement) {
    override fun convert(helper: BaseViewHolder, item: Person?) {
        item ?: return
        helper.setText(R.id.tv_name, item.name)
        helper.setText(R.id.tv_id, item.personId)
        val tvTaskState = helper.getView<TextView>(R.id.tv_task_state)
        val ivLocation = helper.getView<ImageView>(R.id.iv_location)

        val taskId = item.taskId
        val projectId = item.projectId
        val projects =
            LitePal.where("taskId like ? and projectId like ?", taskId, projectId)
                .find(Project::class.java)

        var projectState = Constant.STATE_RUNNING

        projectState = if(projects.isNotEmpty()){
            projects[0].state
        } else {
            Constant.STATE_FINISHED
        }
        when (item.state) {
            Constant.STATE_PRE -> {
                tvTaskState.text = "未开始"
                tvTaskState.setTextColor(mContext.getColor(R.color.color_gray_BDBDBD))
                ivLocation.visibility = View.INVISIBLE
                helper.setText(R.id.tv_task_state, "未开始")

                if(projectState == Constant.STATE_FINISHED){
                    helper.setText(R.id.tv_task_state, "考核已结束")
                }
            }
            Constant.STATE_RUNNING -> {
                if (item.projectId == "E100" && projectState == Constant.STATE_RUNNING) {
                    ivLocation.visibility = View.VISIBLE
                } else {
                    ivLocation.visibility = View.INVISIBLE
                }

                tvTaskState.setTextColor(mContext.getColor(R.color.color_blue_2979FF))
                tvTaskState.text = "考核中"

                if(projectState == Constant.STATE_FINISHED){
                    helper.setText(R.id.tv_task_state, "考核已结束")
                }
            }
            Constant.STATE_FINISHED -> {
                tvTaskState.setTextColor(mContext.getColor(R.color.color_gray_BDBDBD))
                tvTaskState.text = "已完成"
                if(getLastPoint(item.taskId, item.personId, item.projectId)){
                    Glide.with(ivLocation).load(R.mipmap.ic_state_to_start).into(ivLocation)
                    ivLocation.visibility = View.VISIBLE
                } else {
                    Glide.with(ivLocation).load(R.mipmap.ic_loc_map).into(ivLocation)
                    ivLocation.visibility = View.INVISIBLE
                }
            }
        }
        val tvScore = helper.getView<TextView>(R.id.tv_score)
        if (item.score != "") {
            helper.addOnClickListener(R.id.tv_score)
            helper.setText(R.id.tv_score, item.score)
            tvScore.setTextColor(mContext.getColor(R.color.color_blue_2979FF))
            tvScore.paint.isFakeBoldText = false
            tvScore.setBackgroundResource(R.color.color_gray_FAFAFA)
        } else {
            //没成绩可以录入
            helper.addOnClickListener(R.id.tv_score)
            tvScore.setTextColor(mContext.getColor(R.color.white))
            tvScore.setPadding(20, 5, 20, 5)
            tvScore.paint.isFakeBoldText = true
            tvScore.text = "录入"
            tvScore.setBackgroundResource(R.drawable.shape_bg_btn_blue29_round)
        }
    }


    private fun getLastPoint(taskId: String, personId: String, projectId: String): Boolean {
        val find =
            LitePal.where("taskId=? and personId=? and projectId=?", taskId, personId, projectId)
                .find(LocationInfoBean::class.java)
        return find.size > 0
    }
}