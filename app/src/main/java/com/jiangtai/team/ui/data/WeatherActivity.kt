package com.jiangtai.team.ui.data

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.WeatherInfoBean
import com.jiangtai.team.util.CommonUtil
import kotlinx.android.synthetic.main.activity_weather.*
import org.litepal.LitePal

class WeatherActivity : BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_weather
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }
        bt_commit.setOnClickListener {
            commit()
        }
        reshowData()
    }

    override fun initListener() {

    }

    private fun commit() {
        if (checkResult()) {

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


            val weatherInfoBean = WeatherInfoBean()

            weatherInfoBean.zyl = sTotalCloudCover
            weatherInfoBean.dyl = sLowCloudCover
            weatherInfoBean.scl = sScCloudCover
            weatherInfoBean.scg = sScCloudHeight
            weatherInfoBean.fnl = sFnCloudCover
            weatherInfoBean.fng = sFnCloudHeight
            weatherInfoBean.fx = sWindWay
            weatherInfoBean.fs = sWindSpeed
            weatherInfoBean.njd = sVisibility
            weatherInfoBean.dqtq = sCurrentWeather
            weatherInfoBean.qw = sOutsideTemperature
            weatherInfoBean.xdsd = sHumidity
            weatherInfoBean.sqy = sVapourPressure
            weatherInfoBean.bzqy = sLocalPressure
            weatherInfoBean.hpmqy = sSeaPressure
            weatherInfoBean.bz = sNotice

            weatherInfoBean.save()
            ToastUtils.showShort("保存成功")
            finish()
        }
    }

    private fun checkResult(): Boolean {
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


        val isChecked =
            sTotalCloudCover.isNotEmpty() && sLowCloudCover.isNotEmpty() && sScCloudCover.isNotEmpty() &&
                    sScCloudHeight.isNotEmpty() && sFnCloudCover.isNotEmpty() && sFnCloudHeight.isNotEmpty() &&
                    sWindWay.isNotEmpty() && sWindSpeed.isNotEmpty() && sVisibility.isNotEmpty() &&
                    sCurrentWeather.isNotEmpty() && sOutsideTemperature.isNotEmpty() && sHumidity.isNotEmpty() &&
                    sVapourPressure.isNotEmpty() && sLocalPressure.isNotEmpty() && sSeaPressure.isNotEmpty() && sNotice.isNotEmpty()



        if (!isChecked) {
            if (!checkAndToast(et_total_cloud_cover)) {
                return false
            }
            if (!checkAndToast(et_low_cloud_cover)) {
                return false
            }
            if (!checkAndToast(et_sc_cloud_cover)) {
                return false
            }
            if (!checkAndToast(et_sc_cloud_height)) {
                return false
            }
            if (!checkAndToast(et_fn_cloud_cover)) {
                return false
            }
            if (!checkAndToast(et_fn_cloud_height)) {
                return false
            }
            if (!checkAndToast(et_wind_way)) {
                return false
            }
            if (!checkAndToast(et_wind_speed)) {
                return false
            }
            if (!checkAndToast(et_visibility)) {
                return false
            }
            if (!checkAndToast(et_current_weather)) {
                return false
            }
            if (!checkAndToast(et_outside_temperature)) {
                return false
            }
            if (!checkAndToast(et_humidity)) {
                return false
            }
            if (!checkAndToast(et_vapour_pressure)) {
                return false
            }
            if (!checkAndToast(et_local_pressure)) {
                return false
            }
            if (!checkAndToast(et_sea_pressure)) {
                return false
            }
            if (!checkAndToast(et_notice)) {
                return false
            }
        }

        return isChecked
    }


    private fun checkAndToast(et: EditText): Boolean {
        return if (et.text.toString().isEmpty()) {
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }


    private fun reshowData() {

        val id = intent.getStringExtra("id")
        if (id != null && id.isNotEmpty()) {

            val find = LitePal.where(
                "recordID = ? and loginId = ? ",
                id.toString(),
                CommonUtil.getLoginUserId()
            ).find(
                WeatherInfoBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }
            val weatherInfoBean = find.first()



            et_total_cloud_cover.setText(weatherInfoBean.zyl)
            et_low_cloud_cover.setText( weatherInfoBean.dyl)
            et_sc_cloud_cover.setText(weatherInfoBean.scl)
            et_sc_cloud_height.setText(weatherInfoBean.scg)
            et_fn_cloud_cover.setText(  weatherInfoBean.fnl)
            et_fn_cloud_height.setText(weatherInfoBean.fng)
            et_wind_way.setText(weatherInfoBean.fx)
            et_wind_speed.setText(weatherInfoBean.fs)
            et_visibility.setText( weatherInfoBean.njd )
            et_current_weather.setText( weatherInfoBean.dqtq)
            et_outside_temperature.setText( weatherInfoBean.qw)
            et_humidity.setText(weatherInfoBean.xdsd)
            et_vapour_pressure.setText(weatherInfoBean.sqy)
            et_local_pressure.setText( weatherInfoBean.bzqy)
            et_sea_pressure.setText(weatherInfoBean.hpmqy)
            et_notice.setText( weatherInfoBean.bz)

        }


    }
}