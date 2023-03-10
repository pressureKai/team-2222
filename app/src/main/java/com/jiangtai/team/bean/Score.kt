package com.jiangtai.team.bean

import com.jiangtai.team.constant.Constant
import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/11
 * 考核分数表
 */
data class Score(
    var taskId :String?="",//任务id
    var personId:String?="",//人员id
    var projectId: String ="",//科目id
    var projectName: String="",//科目名
    var score:String="",//成绩
    var state:Int=Constant.STATE_PRE,//状态
    var startTime:Long =0,//开始时间
    var endTime:Long=0//结束时间
): LitePalSupport() {
    val id: Long = 1

    override fun toString(): String {
        return "taskId is $taskId \n personId is $personId \n" +
                " projectId is $projectId \n projectName is $projectName \n " +
                "score is $score \n" +
                "state is $state \n" +
                "startTime is $startTime \n" +
                "endTime is $endTime \n"
    }
}
