package com.cetc.service433

/**
 * @des 发送433服务发送命令实体类
 * @time 2021/9/26 10:14 上午
 */
class SendEntity {
    companion object {
        //指令下发
        const val COMMAND = 1

        //任务下发
        const val TASK = 2

        //手持腕表设置
        const val WATCH = 3

        //位置上报
        const val LOCATION = 4

        //位置请求 （腕表进入SOS状态）
        const val SOS = 5
    }

    //发送指令类型
    var sendType = -1

    //用户ID
    var userId = ""

    //手持机ID
    var phoneId = ""

    //发送内容
    var content = ""


    //任务实体类
    var taskEntity: TaskEntity = TaskEntity()


    /**
     * @author xiezekai
     * @des 下发实体类
     * @create time 2021/9/26 10:51 上午
     */
    class TaskEntity {
        //计划ID
        var taskId = ""

        //任务名称
        var taskName = ""

        //任务中项目的ID
        var projectId = ""

        //任务中项目的名称
        var projectName = ""

        //任务中项目的内容
        var projectContent = ""

        //任务中项目的开始时间
        var startTime = ""

        //任务中项目的结束时间
        var endTime = ""

        //是否开启定位
        var isLocation = true
    }


}