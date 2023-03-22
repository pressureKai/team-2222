package com.jiangtai.count.application

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.jiangtai.count.util.LogCatStrategy
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.simple.spiderman.SpiderMan
import org.litepal.LitePal
import kotlin.properties.Delegates

/**
 * Created by heCunCun on 2021/3/4
 */
class App : MultiDexApplication() {
    companion object {
        var context: Context by Delegates.notNull()
            private set

        private var sInstance :Context ?= null
        fun getMineContext() :Context?{
            return  sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sInstance = applicationContext
        SpiderMan.init(this)//崩溃日志
        //初始化数据库
        LitePal.initialize(context)
        initLoggerConfig()

    }

    /**
     * 初始化LOGGER配置
     */
    private fun initLoggerConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .logStrategy(LogCatStrategy())
            .tag("hcc")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                //change from xiezekai
                return false
//               return BuildConfig.DEBUG
            }
        })
    }



}