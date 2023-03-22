package com.jiangtai.count.request

/**
 * Created by heCunCun on 2021/3/29
 */
data class NormalScoreUploadModel(
    val subjectId: String?="",
    val personId: String?="",
    val score: String?="",
    val startTm: Long?=0,
    val finishTm: Long?
)