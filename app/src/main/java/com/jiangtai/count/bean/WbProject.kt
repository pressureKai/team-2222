package com.jiangtai.count.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.jiangtai.count.constant.Constant
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/10
 * 考核小科目表
 */
data class WbProject(
    //原表字段
    var projectId: String = "",
    var projectName: String = "",
    //自定义字段
    var state: Int = Constant.STATE_PRE,//科目状态
    var taskId: String = "",//所属task id
    var startTime: Long = 0,//开始时间
    var endTime: Long = 0,//结束时间
    var projectContent:String = "",//任务内容
    var peopleId:String = "",//人员ID
    @Column(ignore = true)
    var isDesItem :Boolean = false
) : LitePalSupport(), MultiItemEntity {
    val id: Long = 1
    override fun getItemType(): Int {
        return  if(isDesItem) 0 else 1
    }
}