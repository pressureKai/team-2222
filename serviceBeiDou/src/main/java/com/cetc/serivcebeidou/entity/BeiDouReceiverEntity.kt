package com.cetc.serivcebeidou.entity

import org.litepal.crud.LitePalSupport

/**
 * @des 北斗接收历史实体类
 * @time 2021/9/26 4:58 下午
 */
class BeiDouReceiverEntity : LitePalSupport() {
    //接收的内容
    var content = ""

    //接收的时间
    var time = ""

    //发送方地址
    var address = ""

    //接收方地址
    var receiverAddress = ""

    //接收的消息ID
    var receiverMessageId = 0
}