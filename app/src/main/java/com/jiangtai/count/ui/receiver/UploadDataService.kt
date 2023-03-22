package com.jiangtai.count.ui.receiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.application.App.Companion.context
import com.jiangtai.count.bean.LocationReceiver
import com.jiangtai.count.bean.UploadDataBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.GsonInstance
import com.jiangtai.count.util.Preference
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UploadDataService :Service(){
    private var mUploadData: Boolean by Preference(Constant.UPLOAD_DATA, false)
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startUploadDataThread()
        return super.onStartCommand(intent, flags, startId)
    }



    private fun startUploadDataThread(){
        var index = 0
        Thread{
            while (mUploadData){
                index++
                Thread.sleep(100)
                Log.e("upload","uploadData $index")
            }
            stopSelf()
        }.start()
    }


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        EventBus.getDefault().unregister(this)
        return false
    }


    //发送普通位置上报方法
    //发送离散点位置上报方法
    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun mineReceiver(e: UploadDataBean) {
        EventBus.getDefault().removeStickyEvent(e)
        val arrayList = ArrayList<LocationReceiver>()
        arrayList.add(e.locationReceiver!!)
        val toJson = GsonInstance.instance!!.gson!!.toJson(arrayList)
        Log.e("mineReceiver",toJson)
    }


    companion object{
        private var mUploadData: Boolean by Preference(Constant.UPLOAD_DATA, false)

        fun startUploadDataService(){
            try {
                mUploadData = true
                ToastUtils.showShort("startUploadDataService")
                val intent = Intent()
                intent.setClass(context, UploadDataService::class.java)
                context.startService(intent)
            }catch (e:java.lang.Exception){

            }
        }

        fun stopUploadDataService(){
            ToastUtils.showShort("stopUploadDataService")
            mUploadData = false
        }

    }
}