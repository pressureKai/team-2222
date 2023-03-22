package com.jiangtai.count.bean

import org.litepal.crud.LitePalSupport

class LocationReceiver :LitePalSupport(){
    var lat = "0"
    var lng = "0"
    var isSOS = false
    var taskId = ""
    var projectId = ""
    var time = ""
    var userId = ""
    var heartRate = ""
    var phoneId = ""
    var sosType = ""
}