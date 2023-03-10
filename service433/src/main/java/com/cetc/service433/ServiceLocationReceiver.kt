package com.cetc.service433

class ServiceLocationReceiver {
    //纬度
    var lat = "0"
    //经度
    var lng = "0"
    //是否为SOS
    var isSOS = false
    //计划Id
    var taskId = ""
    //任务ID
    var projectId = ""
    //时间戳
    var time = ""
    //用户ID
    var userId = ""
    //心率
    var heartRate = ""
    //手持机ID
    var phoneId = ""
    //搜救类型
    var sosType = ""
    //0:主动发送 1:搜救响应
    var responseType = "0"
}