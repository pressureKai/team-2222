package com.jiangtai.team.ui.data

import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity: BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_weather
    }

    override fun initData() {

    }

    override fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        bt_commit.setOnClickListener {

        }
    }

    override fun initListener() {

    }

    private fun commit(){
        if(checkResult()){

        }
    }

    private fun checkResult():Boolean{
        val sTotalCloudCover = et_total_cloud_cover.text.toString()
        val sLowCloudCover = et_low_cloud_cover.text.toString()
        val sScCloudCover = et_sc_cloud_cover.text.toString()
        val sScCloudHeight = et_sc_cloud_height.text.toString()
        val sFnCloudCover = et_fn_cloud_cover.text.toString()
        val sFnCloudHeight = et_fn_cloud_height.text.toString()
        val sWindWay = et_wind_way.text.toString()
        val sWindSpeed = et_wind_speed.text.toString()
        val sVisibility = et_visibility.text.toString()
        val sCurrentWeather = et_current_weather.text.toString()
        val sOutsideTemperature = et_outside_temperature.text.toString()
        val sHumidity = et_humidity.text.toString()
        val sVapourPressure = et_vapour_pressure.text.toString()
        val sLocalPressure = et_local_pressure.text.toString()
        val sSeaPressure = et_sea_pressure.text.toString()
        val sNotice = et_notice.text.toString()


        return false
    }
}