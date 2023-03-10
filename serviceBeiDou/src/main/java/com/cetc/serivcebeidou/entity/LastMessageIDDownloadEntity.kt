package com.cetc.serivcebeidou.entity

import org.litepal.crud.LitePalSupport

/**
* @author xiezekai
* @des 最后一次已收ID下发实体类
* @create time 2021/9/26 8:18 下午
*/
class LastMessageIDDownloadEntity :LitePalSupport(){
    //北斗平台发送的最小待确认ID
    var minSentID :Int = 0
    //北斗平台发送的最大待确认ID
    var maxSentID :Int = 0
    //收发状态的起始ID
    var startID :Int = 0
    //发送方地址 （北斗平台地址）
    var address = ""
    //接收方地址 （本机地址）
    var receiverAddress = ""
}