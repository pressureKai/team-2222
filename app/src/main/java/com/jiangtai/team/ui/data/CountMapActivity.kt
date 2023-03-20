package com.jiangtai.team.ui.data

import android.animation.ValueAnimator
import android.os.Bundle
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.LatLng
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.util.AnimatorUtil
import com.jiangtai.team.util.CircleBuilder
import kotlinx.android.synthetic.main.activity_count_map.*


class CountMapActivity: BaseActivity() {

    private var aMap :AMap ?= null
    override fun attachLayoutRes(): Int {
        return R.layout.activity_count_map
    }


    override fun initData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        requestLocation()
    }

    override fun initView() {

         aMap = mapView.map

        aMap?.mapType =  AMap.MAP_TYPE_NORMAL
        val latLng = LatLng(
            0.toDouble(), 0.toDouble()
        )
        addWaveAnimation (latLng, aMap)

        aMap?.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
    }

    override fun initListener() {

    }


    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }


    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }


    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    //请求定位
    private fun requestLocation(){

    }


    /**
     * 画水波纹
     */


    private var circleList //圆集合
            : MutableList<Circle?>? = null

    private var valueAnimator //动画工具
            : ValueAnimator? = null

    /**
     * 添加水波纹效果
     *
     * @param latLng 要展示扩散效果的点经纬度
     * AMap aMap：高德地图
     */
    fun addWaveAnimation(latLng: LatLng?, aMap: AMap?) {
        circleList = ArrayList()
        var radius = 50
        for (i in 0..3) {
            radius = radius + 50 * i
            (circleList as ArrayList<Circle?>).add(CircleBuilder.addCircle(latLng, radius.toDouble(), aMap))
        }
        valueAnimator = AnimatorUtil.getValueAnimator(0, 50
        ) { animation ->
            val value = animation.getAnimatedValue() as Int
            for (i in (circleList as ArrayList<Circle?>).indices) {
                val nowradius = 50 + 50 * i
                val circle = (circleList as ArrayList<Circle?>)[i]!!
                val radius1 = (value + nowradius).toDouble()
                circle.radius = radius1
                var strokePercent = 200
                var fillPercent = 20
                if (value < 25) {
                    strokePercent = value * 8
                    fillPercent = value * 20 / 50
                } else {
                    strokePercent = 200 - value * 4
                    fillPercent = 20 - value * 20 / 50
                }
                if (circle.fillColor != CircleBuilder.getStrokeColor(strokePercent)) {
                    circle.strokeColor = CircleBuilder.getStrokeColor(strokePercent)
                    circle.fillColor = CircleBuilder.getFillColor(fillPercent)
                }
            }
        }
    }

    /**
     * 移除水波纹动画
     */
    fun removeCircleWave() {
        valueAnimator?.cancel()
        if (circleList != null) {
            for (circle in circleList!!) {
                circle?.remove()
            }
            circleList!!.clear()
        }
    }

}