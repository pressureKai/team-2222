package com.jiangtai.team.bean

class ResponseData {
    var success = false
    var message = ""
    var code = 200
    var timestamp = 0L
    var result:Result ?= null


    inner class Result{
        var cmd = ""
        var sessionid = ""
        var status = ""
        var reason = ""
        var registerid = ""
        var ISSuccess = false
    }
}