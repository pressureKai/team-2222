package com.jiangtai.team.bean

import org.litepal.crud.LitePalSupport

/**
 * 接收消息-存库
 */
class ReceiverConfirmIdMessage:LitePalSupport() {
    //目标地址
    var address = ""
    //发送地址
    var sendAddress = ""
    //messageId
    var dataId = ""
    // 0：待发送 1：已发送 2：发送成功待确认 3：发送成功 4：发送失败
    var sendState = ""
    // 发送时间
    var sendMessageTime = ""
    var minSendId = ""
    var maxSendId = ""
}