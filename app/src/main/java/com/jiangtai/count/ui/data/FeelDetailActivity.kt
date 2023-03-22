package com.jiangtai.count.ui.data

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.FeelListBean
import com.jiangtai.count.util.AnimatorUtil
import com.jiangtai.count.util.CircleBuilder
import kotlinx.android.synthetic.main.activity_feel_detail.*
import kotlinx.android.synthetic.main.activity_feel_detail.iv_back
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList


class FeelDetailActivity : BaseActivity(), AMapLocationListener {
    private var aMap: AMap? = null
    private var circleRadius = 1000
    private val airDropMap: ConcurrentHashMap<Marker, String> = ConcurrentHashMap()
    private var locationLatLng: LatLng? = null
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    private var isFirstMoveForLocation = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_feel_detail
    }


    override fun initData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        requestLocation()
    }

    override fun initView() {
        initImmersionBar(dark = true)
        aMap = mapView.map
        aMap?.uiSettings?.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER
        aMap?.mapType = AMap.MAP_TYPE_NORMAL


        val feelListBean = intent.getParcelableExtra<FeelListBean>("result")

        if(feelListBean != null){
            top_latitude.text = feelListBean.topLatitude.toString()
            bottom_latitude.text = feelListBean.bottomLatitude.toString()
            top_longitude.text  =  feelListBean.topLongitude.toString()
            bottom_longitude.text = feelListBean.bottomLongitude.toString()
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
                val format = simpleDateFormat.format(Date(feelListBean.time.toLong()))
                find_time.text  = format
            }catch (e:Exception){
                find_time.text  =  ""
            }
            createRectangle(feelListBean)
        }
        var locationStyle: MyLocationStyle? = null
        locationStyle = MyLocationStyle();//初始化定位蓝点样式
        locationStyle.interval(2000);//设置连续定位模式下的的定位间隔，值在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒
        locationStyle.showMyLocation(true);
        locationStyle.strokeColor(Color.TRANSPARENT);
        locationStyle.radiusFillColor(Color.TRANSPARENT);
        locationStyle.strokeWidth(0f);
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_new_location))
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
        aMap?.myLocationStyle = locationStyle //设置定位蓝点的style

        iv_back.setOnClickListener {
            finish()
        }
        locationLatLng = LatLng(
            0.toDouble(), 0.toDouble()
        )
        //addWaveAnimation(locationLatLng, aMap)
        aMap?.uiSettings?.isScaleControlsEnabled = true



    }

    override fun initListener() {

    }


    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }


    override fun onDestroy() {
        removeCircleWave()
        mapView.onDestroy()
        super.onDestroy()
    }


    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    //请求定位
    private fun requestLocation() {
        aMap?.let {
            if (checkPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            ) {
                it.isMyLocationEnabled = true
                it.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

                if (mLocationClient == null) {
                    //初始化定位
                    mLocationClient = AMapLocationClient(this)
                    //初始化定位参数
                    mLocationOption = AMapLocationClientOption()
                    //设置定位回调监听
                    mLocationClient!!.setLocationListener(this)
                    //设置为高精度定位模式
                    mLocationOption!!.locationMode =
                        AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                    //设置定位参数
                    mLocationClient!!.setLocationOption(mLocationOption)
                    // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                    // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                    // 在定位结束后，在合适的生命周期调用onDestroy()方法
                    // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                    mLocationClient!!.startLocation() //启动定位
                }
            } else {
                requestPermission(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 0x33
                )
            }
        }
    }



    private fun addMarker(center: LatLng,feelListBean: FeelListBean) {
        val inflate = ViewGroup.inflate(this, R.layout.item_marker_text, null)
        val tvDes = inflate.findViewById<TextView>(R.id.tv_des)
        tvDes.text = feelListBean.type + "发现目标"+ feelListBean.targetType
        val icon = MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromView(inflate))
        aMap?.addMarker(icon)
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
    private fun addWaveAnimation(latLng: LatLng?, aMap: AMap?) {
        circleList?.clear()
        circleList = ArrayList()
        var radius = 50
        for (i in 0..1) {
            radius += circleRadius * i
            (circleList as ArrayList<Circle?>).add(
                CircleBuilder.addCircle(latLng, radius.toDouble(), aMap)
            )
        }
        valueAnimator = null
        valueAnimator = AnimatorUtil.getValueAnimator(
            0, circleRadius
        ) { animation ->
            val value = animation.animatedValue as Int
            for (i in (circleList as ArrayList<Circle?>).indices) {
                // 50 或 100
                val nowRadius = circleRadius + circleRadius * i
                val circle = (circleList as ArrayList<Circle?>)[i]!!
                val radius1 = (value + nowRadius).toDouble()
                circle.radius = radius1
                var strokePercent = 200
                var fillPercent = 20
                if (value < circleRadius / 2) {
                    //边界值 alpha
                    strokePercent = ((value.toFloat() / circleRadius.toFloat()) * 255).toInt()
                    fillPercent = strokePercent * 20 / 50
                } else {
                    //边界值 alpha
                    strokePercent =
                        255 - ((value.toFloat() / circleRadius.toFloat()).toFloat() * 255).toInt()
                    fillPercent =
                        255 - ((value.toFloat() / circleRadius.toFloat()).toFloat() * 255).toInt()
                }
                circle.strokeColor = CircleBuilder.getStrokeColor(strokePercent)
                circle.fillColor = CircleBuilder.getFillColor(fillPercent)
            }
        }
    }


    /**
     * 移除水波纹动画
     */
    private fun removeCircleWave() {
        valueAnimator?.cancel()
        if (circleList != null) {
            for (circle in circleList!!) {
                circle?.remove()
            }
            circleList!!.clear()
        }
    }

    override fun onLocationChanged(p0: AMapLocation?) {
        p0?.let {
            val latitude = it.latitude
            val longitude = it.longitude
            locationLatLng = LatLng(latitude, longitude)
            Handler(Looper.getMainLooper()).postDelayed({
                removeCircleWave()
             //   addWaveAnimation(locationLatLng, aMap)
             //   test()
                if (!isFirstMoveForLocation) {
             //       aMap?.moveCamera(CameraUpdateFactory.changeLatLng(locationLatLng))
                    isFirstMoveForLocation = true
                }
            }, 2000)

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x33) {
                requestLocation()
            }
        }
    }
    private fun createRectangle(
        feelListBean: FeelListBean
    ) {
        val latLngs: MutableList<LatLng> = ArrayList()

        val leftLatLng = LatLng(feelListBean.topLatitude, feelListBean.topLongitude)
        val rightLatLng = LatLng(feelListBean.topLatitude, feelListBean.bottomLongitude)
        val rightBottomLatLng = LatLng(feelListBean.bottomLatitude, feelListBean.bottomLongitude)
        val leftBottomLatLng = LatLng(feelListBean.bottomLatitude, feelListBean.topLongitude)
        latLngs.add(leftLatLng)
        latLngs.add(rightLatLng)
        latLngs.add(rightBottomLatLng)
        latLngs.add(leftBottomLatLng)

        aMap!!.addPolygon(
            PolygonOptions()
                .addAll(latLngs)
                .fillColor(resources.getColor(R.color.alpha_color)).strokeWidth(0f)
        )



        val calculateLineDistance = AMapUtils.calculateLineDistance(leftLatLng, rightBottomLatLng)
        val centerLatitude = (feelListBean.topLatitude + feelListBean.bottomLatitude) / 2
        val centerLongitude = (feelListBean.topLongitude + feelListBean.bottomLongitude) / 2
        val mapCenterPoint = LatLng(centerLatitude,centerLongitude)



        addMarker(LatLng(feelListBean.bottomLatitude - 0.1,(feelListBean.bottomLongitude + feelListBean.topLongitude)/ 2),feelListBean)
        Log.e("calculateLineDistance","calculateLineDistance is $calculateLineDistance")
        mapCenterPoint.let {
            //146090.9
            var zoom = 17f
            if(calculateLineDistance < 500){
                zoom = 17f
            } else if(calculateLineDistance > 500 && calculateLineDistance < 5000){
                zoom = 12f
            } else if(calculateLineDistance > 5000 && calculateLineDistance < 30000){
                zoom = 9f
            } else if(calculateLineDistance > 30000 && calculateLineDistance < 50000){
                zoom = 8f
            } else if(calculateLineDistance > 50000 && calculateLineDistance < 100000){
                zoom = 7f
            }else if(calculateLineDistance > 100000 && calculateLineDistance < 200000){
                zoom = 6f
            }else if(calculateLineDistance > 200000 && calculateLineDistance < 500000){
                zoom = 5f
            }else if(calculateLineDistance > 1000000){
                zoom = 3f
            }


            Handler(Looper.getMainLooper()).postDelayed({
                aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it,zoom))
            },2000)

        }

    }

}