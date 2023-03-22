package com.jiangtai.count.bean

import org.litepal.crud.LitePalSupport

class SendConfirmIdMessage:LitePalSupport(){
    //目标地址
    var address = ""
    //发送地址
    var sendAddress = ""
    //messageId
    var dataId = ""
    //  1：已发送  3：发送成功 4：发送失败
    var sendState = ""
    // 发送时间
    var sendMessageTime = ""
    var minSendId = ""
    var maxSendId = ""
}