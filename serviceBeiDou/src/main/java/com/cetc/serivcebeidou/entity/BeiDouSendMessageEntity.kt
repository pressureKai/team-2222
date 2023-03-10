package com.cetc.serivcebeidou.entity

import org.litepal.crud.LitePalSupport

/**
* @des 北斗发送历史
* @time 2021/9/26 5:01 下午
*/
class BeiDouSendMessageEntity:LitePalSupport() {
    //发送的内容
    var content = ""
    //发送的时间
    var time = ""
    //发送的消息ID 65536  --> 1
    var messageId :Int = 0
    //发送的地址 （发送方地址）
    var address = ""
    //接收的地址 （目标地址）
    var receiverAddress = ""
    //北斗平台是否已经接收到此消息
    var beiDouIsReceiver:Boolean = false
    //需要发送的内容
    var sendMessage:String = ""
    //是否已发送
    var sendSuccess : Boolean = false
}