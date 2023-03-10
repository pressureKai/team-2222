package com.jiangtai.team.bean

class MqttBean {
    var inputs :ArrayList<MqttItemBean> = ArrayList()
    var function = ""
    var messageId = ""
    var deviceId = ""

    class MqttItemBean {
//        var type = ""
//        var values = ""
//        var content = ""
//
//        var TYPE = ""
//        var VALUES = ""
//        var CONTENT = ""

        var name = ""
        var value = ""
    }


    class bean{
        var values = ""
        var type = ""
        var content = ""
    }

}