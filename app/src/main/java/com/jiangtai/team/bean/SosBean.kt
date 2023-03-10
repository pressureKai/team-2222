package com.jiangtai.team.bean

import org.litepal.crud.LitePalSupport

data class SosBean(var personId :String = "",
                   var lat:String = "",
                   var lng:String = ""):LitePalSupport() {
    var id = 0
    var heartRate = ""
    var userId = ""
}