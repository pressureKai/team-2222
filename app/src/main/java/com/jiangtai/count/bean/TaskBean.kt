package com.jiangtai.count.bean

import com.jiangtai.count.constant.Constant
import org.litepal.crud.LitePalSupport


/**
 * Created by heCunCun on 2021/3/10
 * 任务表
 */
data class TaskBean(
    //原表字段
    var persons: List<Person>?= null,
    var projects: List<Project>?=null,
    var taskId: String="",
    var taskName: String="",
    //自定义字段
    var startTime: Long = 0,
    var endTime: Long = 0,
    var state: Int = Constant.STATE_PRE,
    var isMajor: Boolean = false,
    //是否开启位置定位
    var isLocation :Boolean = false
) : LitePalSupport(){
    val id: Long = 1
}