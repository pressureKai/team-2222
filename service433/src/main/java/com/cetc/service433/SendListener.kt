package com.cetc.service433

/**
* @author xiezekai
* @des  发送命令回调类
* @create time 2021/9/26 10:29 上午
*/
interface SendListener {
    fun sendFinish(message:String?)
}