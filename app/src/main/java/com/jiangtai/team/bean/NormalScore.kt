package com.jiangtai.team.bean

import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/12
 *  日常计划成绩表
 */
data class NormalScore(
    var personId: String? = "",
    var projectId: String? = "",
    var projectName: String? = "",
    var uploadTime: Long? = 0,
    var startTime:Long?=0,
    var score: String? = ""
) : LitePalSupport() {
    val id: Long = 1

    override fun toString(): String {
        return "personId is $personId \n " +
                "projectId is $projectId \n " +
                "projectName is $projectName \n " +
                "uploadTime is $uploadTime \n " +
                "startTime is $startTime \n " +
                "score is $score \n"
    }
}
