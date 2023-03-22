package com.jiangtai.count.bean


import org.litepal.crud.LitePalSupport

class UploadMajorScoreData:LitePalSupport() {
    var personId = ""
    var times = 0L
    var startTime = ""
    var endTime = ""
    var level = "合格"
    var projectId = ""
    var taskId = ""
}