package com.jiangtai.count.request

/**
 * Created by heCunCun on 2021/3/29
 */
data class ScoreUploadModel(
    var taskId: String,
    var taskStartTm: Long,
    var taskEndTm: Long,
    var subjectId: String,
    var subjectStartTm: Long,
    var subjectEndTm: Long,
    var personId: String,
    var score: String,
    var startTm: Long,
    var finishTm: Long
)