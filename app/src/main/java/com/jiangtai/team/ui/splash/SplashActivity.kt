package com.jiangtai.team.ui.splash

import android.Manifest
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.login.LoginActivity
import com.jiangtai.team.ui.login.NewLoginActivity
import com.jiangtai.team.ui.main.MainActivity
import com.jiangtai.team.util.CommandUtils
import com.jiangtai.team.util.KeyUtil
import com.jiangtai.team.util.Preference
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_splash.*
import org.litepal.LitePal


/**
 * Created by heCunCun on 2021/3/8
 */
class SplashActivity : BaseActivity() {
    private val PERMISS_REQUEST_CODE = 0x100

    private lateinit var countDownTimer: CountDownTimer
    private var timeCountInMilliSeconds = 2500L

    private var serverIp: String by Preference(Constant.SERVER_IP, "")

    override fun attachLayoutRes(): Int = R.layout.activity_splash

    override fun initData() {
        Glide.with(this).load(R.drawable.splash).into(iv_splash)
    }

    override fun initView() {
        startCountDownTimer()
        CommandUtils.stringUsrIdToByteSizeSixteen("")
        //请求权限
        if (checkPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            LitePal.getDatabase()
            Logger.e("已获取定位权限")
        } else {
            requestPermission(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISS_REQUEST_CODE
            )
        }
        val sha1 = KeyUtil.getSHA1(this)
        Log.e("SHA", "SHA1=====>$sha1")


    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                val intent = Intent(this@SplashActivity, NewLoginActivity::class.java)
//                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        countDownTimer.start()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISS_REQUEST_CODE) {

            LitePal.getDatabase()
        }
    }

    override fun initStateBarColor() {
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }

    override fun initListener() {
//        img.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
////            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        iv_splash.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
////            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        tv_start.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
////            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }
}