package com.jiangtai.count.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.ArrayMap
import android.widget.RemoteViews
import com.blankj.utilcode.util.LogUtils
import com.jiangtai.count.R

/**
 * 通知管理工具类
 */
class NotificationHelper(private val mContext: Context) {
    private val manager: NotificationManager = mContext
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var map: MutableMap<Int, Notification>? = null
    init {
        map = ArrayMap()
    }

    fun showNotification(notificationId: Int,name:String,lat:String,lng:String) {
        if (!map!!.containsKey(notificationId)) {
            // 设置通知的显示视图
            val remoteViews = RemoteViews(
                mContext.packageName,
                R.layout.notification_sos
            )

            remoteViews.setTextViewText(R.id.name,"$name 发送了SOS")
            remoteViews.setTextViewText(R.id.lat,"纬度为 $lat")
            remoteViews.setTextViewText(R.id.lng,"经度为 $lng")

            // 创建通知对象
            val notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel("$notificationId",mContext.resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
                manager.createNotificationChannel(mChannel)
                Notification.Builder(mContext)
                    .setChannelId("$notificationId")
                    .setContentTitle("SOS").setContentText("").setCustomContentView(remoteViews)
                    .setLargeIcon(Icon.createWithResource(mContext, R.mipmap.logo))
                    .setSmallIcon(Icon.createWithResource(mContext, R.mipmap.logo)).build()
            } else {
                NotificationCompat.Builder(mContext)
                    .setContentTitle("SOS").setContentText("").setCustomContentView(remoteViews)
                    .setSmallIcon(R.mipmap.logo)
                    .setOngoing(true).setChannelId("$notificationId").build()
            }
            manager.notify(notificationId, notification)
            map!![notificationId] = notification
        }
    }


    fun cancel(notificationId: Int) {
        try {
            val manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
            map!!.remove(notificationId)
        }catch (e:Exception){
            LogUtils.e("NotificationHelper","notification error is $notificationId")
        }

    }
}