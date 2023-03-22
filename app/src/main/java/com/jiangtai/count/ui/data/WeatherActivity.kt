package com.jiangtai.count.ui.data

import android.view.View
import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.bean.WeatherInfoBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.util.CommonUtil
import com.jiangtai.count.util.Preference
import com.jiangtai.count.util.RegexUtil
import com.jiangtai.count.util.ToJsonUtil
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.activity_weather.bt_commit
import kotlinx.android.synthetic.main.activity_weather.et_notice
import kotlinx.android.synthetic.main.activity_weather.iv_back
import kotlinx.android.synthetic.main.activity_weather.iv_delete
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

class WeatherActivity : BaseActivity() {

    private var isUpdate = false
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

        iv_delete.setOnClickListener {
            val id = intent.getStringExtra("id")
            if(id != null && id.isNotEmpty()){
                LitePal.deleteAll(
                    CountRecordBean::class.java,
                    "recordID = ?",
                    id
                )
                LitePal.deleteAll(
                    WeatherInfoBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.WEATHER_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
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

            var weatherInfoBean:WeatherInfoBean ?= null
            if(isUpdate){
                val id = intent.getStringExtra("id")
                if (id != null && id.isNotEmpty()) {
                    iv_delete.visibility = View.VISIBLE
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
                    weatherInfoBean = find.first()
                }
            } else {
                weatherInfoBean = WeatherInfoBean()
            }


            weatherInfoBean?.let {
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

                weatherInfoBean.save(isUpdate)
                commitToServer(weatherInfoBean)
                if(isUpdate){
                    ToastUtils.showShort("更新成功")
                } else {
                    ToastUtils.showShort("保存成功")
                }
                finish()
            }?:let{
                ToastUtils.showShort("该记录不存在!")
                finish()
            }
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
            iv_delete.visibility = View.VISIBLE
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


            bt_commit.text = "更新"
            isUpdate = true
        } else {
            iv_delete.visibility = View.GONE
        }


    }


    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private fun commitToServer(bean: WeatherInfoBean){
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }


        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(bean))
        val requestCallback = MyRetrofit.instance.api.sendWeatherInfo(requestBody)

        requestCallback.compose(ThreadSwitchTransformer()).subscribe(object : CallbackListObserver<NewBaseBean<Any>>(){
            override fun onSucceed(t: NewBaseBean<Any>?) {
                ToastUtils.showShort("请求成功")
            }

            override fun onFailed() {

            }

        })
    }
}