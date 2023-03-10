package com.cetc.serivcebeidou.entity

import org.litepal.crud.LitePalSupport

/**
* @des 已收ID下发传递过来的Boolean值实体类
* @time 2021/9/26 8:40 下午
*/
class MessageIDDownloadReceiverListEntity:LitePalSupport() {
    //接收方地址
    var receiverAddress = ""
    //发送方地址
    var address = ""
    //是否被接收
    var isReceiver: Boolean = false
}