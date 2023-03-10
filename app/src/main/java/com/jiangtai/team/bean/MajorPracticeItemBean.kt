package com.jiangtai.team.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlin.random.Random

/**
* @author xiezekai
* @des  专项任务实体类
* @create time 2021/8/4 3:28 下午
*/
class MajorPracticeItemBean:MultiItemEntity {
    var taskId : String = ""
    var taskName : String = ""
    //任务开始时间
    var startTime :Long = 0L
    //任务持续时间
    var continueTime : Int = 0
    //0:未开始; 1:已完成；2:进行中；
    var taskState :Int = 0


    var isFinish = false
    var isDesItem = false
    var nextInt  = 1
    init {
      nextInt  = Random.nextInt(8)
    }

    override fun getItemType(): Int {
         return if(isDesItem) 0 else 1
    }

}