package com.jiangtai.team.bean

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.application.App
import com.jiangtai.team.ui.data.*
import com.jiangtai.team.util.CommonUtil.getLoginUserId
import org.litepal.crud.LitePalSupport
import java.util.Random

class CountRecordBean :LitePalSupport() {

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    var loginId: String? = null
    //被记录的具体事项的记录时间
    var recordTime = ""
    //被记录的具体事项id
    var recordID = ""
    //数据类型
    var recordType = OIL_TYPE


    override fun save(): Boolean {
        loginId = getLoginUserId()
        return super.save()
    }


    fun startActivity(){
        when(recordType){
            OIL_TYPE ->{
                val intent = Intent(App.context, OilActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            AIR_TYPE ->{
                val intent = Intent(App.context, AirDropActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            DEVICE_TYPE ->{
                val intent = Intent(App.context, CarNormalActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            DEVICE_FILL_TYPE ->{
                val intent = Intent(App.context, DeviceActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            WEATHER_TYPE ->{
                val intent = Intent(App.context, WeatherActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            CAR_FIX_TYPE ->{
                val intent = Intent(App.context, CarFixActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            HELICOPTER_OIL_TYPE ->{
                val intent = Intent(App.context, HelicopterOilActivity::class.java)
                intent.putExtra("id",this.recordID)
                App.context.startActivity(intent)
            }
            else ->{
                ToastUtils.showShort("未知记录不支持跳转")
            }
        }
    }


    companion object{
        const val OIL_TYPE = 0
        const val AIR_TYPE = 1
        const val DEVICE_TYPE = 2
        const val DEVICE_FILL_TYPE = 3
        const val WEATHER_TYPE = 4
        const val CAR_FIX_TYPE = 5
        const val HELICOPTER_OIL_TYPE = 5



        fun getCountId(s: String):String{
            return s + Random().nextInt(100000)
        }


    }
}