package com.cetc.serivcebeidou.service

import android.app.Service
import android.content.BDManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.cetc.serivcebeidou.ManagerBeiDou
import com.cetc.serivcebeidou.entity.BeiDouSendMessageEntity
import org.litepal.LitePal

/**
* @author xiezekai
* @des 北斗消息发送服务
* @create time 2021/9/27 4:40 下午
*/
class BeiDouSendMessageService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSendThread()
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startSendThread() {
        Thread {
            while (true) {
                //检查当前时间距离上一次发送北斗消息时间是否大于62秒
                if (System.currentTimeMillis() - getLastSendTime(applicationContext) > 62 * 1000) {
                    Handler(Looper.getMainLooper()).post {
                        val bdManager =
                            applicationContext.getSystemService("beidou") as BDManager
                        val bdCardNumber = bdManager.bdCardNumber
                        val sendMessageList = getSendMessageList(bdCardNumber)
                        if(sendMessageList.isNotEmpty()){
                            val first = sendMessageList.first()
                            bdManager.sendBDCommand(first.sendMessage)
                            setLastSendTime(applicationContext,System.currentTimeMillis())
                            first.sendSuccess = true
                            first.save()
                        } else {
                            //空闲时间 -> 已收ID上报(存在已收ID一直上报的情况)
                            ManagerBeiDou.sendMessageIDUpload(applicationContext)
                        }
                    }
                } else {
                    //睡眠一段时间，防止重复监测（已经发送完一个消息，等待62秒）
                    Thread.sleep(System.currentTimeMillis() - getLastSendTime(applicationContext))
                }
            }
        }.start()
    }


    /**
    * @des 获取未发送数据列表
    * @time 2021/9/27 11:48 上午
    */
    private fun getSendMessageList(bdCardNumber: String?) : ArrayList<BeiDouSendMessageEntity> {
        val list = ArrayList<BeiDouSendMessageEntity>()
        bdCardNumber?.let {
            val find =
                LitePal.where("address = ? and sendSuccess = ?", bdCardNumber,"0")
                    .find(BeiDouSendMessageEntity::class.java)
            list.addAll(find)
        }


        return list
    }


    /**
     * @des 获取最后一次发送北斗消息的时间
     * @time 2021/9/27 11:04 上午
     */
    private fun getLastSendTime(context: Context): Long {
        var lastSendTime = 0L
        val sharedReadable = context.getSharedPreferences(
            "beidou",
            Context.MODE_MULTI_PROCESS
        )
        val sharedWritable = sharedReadable?.edit()
        sharedReadable?.let {
            lastSendTime = it.getLong("last_commit", 0L)
        }
        return lastSendTime
    }

    /**
     * @des 设置最后一次发送北斗消息时间
     * @time 2021/9/27 11:04 上午
     */
    private fun setLastSendTime(context: Context, long: Long) {
        val sharedReadable = context.getSharedPreferences(
            "beidou",
            Context.MODE_MULTI_PROCESS
        )
        val sharedWritable = sharedReadable?.edit()
        sharedWritable?.let {
            it.putLong("last_commit", long)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}