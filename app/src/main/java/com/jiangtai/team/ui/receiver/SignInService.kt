package com.jiangtai.team.ui.receiver

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.ui.signIn.SignlnFragmentUtil
import com.jiangtai.team.util.CommandUtils
import com.jiangtai.team.util.Preference
import java.lang.Exception

class SignInService : Service() {
    private var signTime: Long by Preference(Constant.SIGN_TIME, 10000L)
    //0:全部1:分组2:任务
    private var signType: Int by Preference(Constant.SIGN_TYPE, 2)

    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")

    private var phoneId: String by Preference(Constant.PHONE_ID, "")

    override fun onBind(p0: Intent?): IBinder? {
        return  null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSignInService()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
    * @des 开启发送考勤打卡命令线程
    * @time 2021/11/2 10:53 上午
    */
    private fun startSignInService(){
        Thread{
            while (mIsStart){
                when(signType){
                    0 ->{
                        CommandUtils.sign(context = applicationContext)
                    }
                    1-> {
                        CommandUtils.sign(context = applicationContext,phoneId = phoneId)
                    }
                    2 ->{
                        CommandUtils.sign(context = applicationContext,phoneId, SignlnFragmentUtil.projectstringid)
                    }
                }
                Thread.sleep(signTime)
            }
        }.start()
    }


    companion object {
        private var mIsStart: Boolean by Preference(Constant.SIGN_START, false)
        fun startCurrentThread(context: Context) {
            try {
                mIsStart = true
                val intent = Intent()
                intent.setClass(context, SignInService::class.java)
                context.startService(intent)
            } catch (e: Exception) {
                LogUtils.e("CheckDistanceService startCurrentThreadError is $e")
            }
        }

        fun stopCurrentThread() {
            mIsStart = false
        }
    }
}