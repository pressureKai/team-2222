package com.jiangtai.team.bean

import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/18
 */
data class LocationInfoBean(
    var lat: Double,
    var lng: Double,
    var heartRate: Int,
    val personId: String,
    val projectId: String,
    var time:Long,
    var taskId:String
) : LitePalSupport() {
    val id: Long = 1

    override fun toString(): String {
        return "lat is $lat \n " +
                "long is $lng \n " +
                "heartRate is $heartRate \n " +
                "personId is $personId \n " +
                "projectId is $projectId \n " +
                "time is $time \n " +
                "taskId is $taskId \n"
    }
}