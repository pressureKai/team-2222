package com.jiangtai.count.bean

/**
* @author xiezekai
* @des  专项任务实体类
* @create time 2021/8/4 3:28 下午
*/
class MajorPracticeBean {
    var taskId : String = ""
    var taskName : String = ""
    //任务开始时间
    var startTime :Long = 0L
    //任务持续时间
    var continueTime : Int = 0
    //当前任务状态
    var taskState :Int = 0
    //专项任务中具体项目列表
    var taskItems :ArrayList<MajorPracticeItemBean> = ArrayList()
}