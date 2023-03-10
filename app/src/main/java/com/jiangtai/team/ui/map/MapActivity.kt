package com.jiangtai.team.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.LoRaManager
import android.content.SndData
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.cld.mapapi.map.CldMap
import com.cld.mapapi.map.MapStatusType
import com.cld.mapapi.map.MarkerOptions
import com.cld.mapapi.map.OverlayOptions
import com.cld.mapapi.model.LatLng
import com.cld.mapapi.util.DistanceUtil
import com.cld.nv.location.CldCoordUtil
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.LocationInfoBean
import com.jiangtai.team.bean.SosBean
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.event.RefreshLocEvent
import com.jiangtai.team.util.CrcUtil
import com.jiangtai.team.util.HexUtil
import com.jiangtai.team.util.Preference
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_map.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by heCunCun on 2021/3/22
 */
class MapActivity : BaseActivity() {
    override fun useEventBus(): Boolean = true
    private var pointList = CopyOnWriteArrayList<LatLng>()
    private var sosList = CopyOnWriteArrayList<LatLng>()
    private var loadingList = CopyOnWriteArrayList<LatLng>()
    private var sosLoadingList = CopyOnWriteArrayList<LatLng>()
    private var sosListOptions = CopyOnWriteArrayList<OverlayOptions>()
    private var loadListOptions = CopyOnWriteArrayList<OverlayOptions>()
    private var center: LatLng? = null
    private var cldMap: CldMap? = null
    private var personId = ""
    private var projectId = ""
    private var taskId = ""
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var subscribe: Disposable? = null
    private var isInterrupt = false
    val arrayList = CopyOnWriteArrayList<LatLng>()
    private var mZoomSize = 0
    override fun attachLayoutRes(): Int = R.layout.activity_map

    override fun initData() {
        refreshData()
    }



    @SuppressLint("CheckResult")
    private fun refreshData() {
        val locList =
            LitePal.where("taskId=? and personId=? and projectId=?", taskId, personId, projectId)
                .find(LocationInfoBean::class.java)

        val sosBeanList = LitePal.where("personId = ?", personId).find(SosBean::class.java)


        if (locList.isNotEmpty()) {
            tv_heart_rate.text = locList.last().heartRate.toString()
        }

        pointList.clear()

        for (loc in locList) {
            if (loc.lat != 0.toDouble() && loc.lng != 0.toDouble()) {
                pointList.add(CldCoordUtil.wgs84ToCLDEx(loc.lat, loc.lng, 0.0))
            }
        }


        sosList.clear()
        //添加sos信息
        for (sos in sosBeanList) {
            try {
                if (sos.lat.toDouble() != 0.toDouble() && sos.lng.toDouble() != 0.toDouble()) {
                    sosList.add(
                        CldCoordUtil.wgs84ToCLDEx(
                            sos.lat.toDouble(),
                            sos.lng.toDouble(),
                            0.0
                        )
                    )
                }
            } catch (e: Exception) {
                LogUtils.e("save sos error is $e")
            }
        }




        try {
            Thread {
                val mapLoadList = ArrayList<MarkerOptions>()

                for (value in pointList) {
                    if (!isLoaded(loadingList, value)) {
                        if (!isPositionLoaded(loadListOptions, value)) {
                            val icon = MarkerOptions().rotate(0).position(value)
                                .icon(resources.getDrawable(R.mipmap.ic_position))
                            loadListOptions.add(icon)
                            loadingList.add(value)
                            mapLoadList.add(icon)
                        }
                    }
                }

                val sosLoadList = ArrayList<MarkerOptions>()

                for (value in sosList) {
                    if (!isLoaded(sosLoadingList, value)) {
                        if (!isPositionLoaded(sosListOptions, value)) {
                            val icon = MarkerOptions().rotate(0).position(value)
                                .icon(resources.getDrawable(R.mipmap.sos))
                            sosListOptions.add(icon)
                            sosLoadingList.add(value)
                            sosLoadList.add(icon)
                        }
                    }
                }



                for (value in sosLoadList) {
                    Thread.sleep(200)
                    cldMap?.addOverlay(value)
                }

                for (value in mapLoadList) {
                    Thread.sleep(200)
                    cldMap?.addOverlay(value)
                }
            }.start()

        } catch (e: Exception) {

        }



        Handler(Looper.getMainLooper()).post {
            if(mZoomSize == 0){
                mZoomSize = pointList.size
                cldMap?.zoomToSpan(pointList) // 缩放到合适比例
            } else {
                val i = pointList.size - mZoomSize
                if(i > 20 && pointList.size < 100){
                    mZoomSize = pointList.size
                    cldMap?.zoomToSpan(pointList) // 缩放到合适比例
                }
            }
        }

    }


    private fun isLoaded(
        loadingList: CopyOnWriteArrayList<LatLng>,
        latLng: LatLng,
        isNeedFilter: Boolean? = true
    ): Boolean {
        var isLoaded = false

        if (isNeedFilter!!) {
            for(value in loadingList){
                if(DistanceUtil.getDistance(value,latLng) < 5){
                    isLoaded = true
                    break
                }
            }
        }

        return isLoaded
    }


    private fun isPositionLoaded(
        loadingList: CopyOnWriteArrayList<OverlayOptions>,
        latLng: LatLng
    ): Boolean {
        var isLoaded = false

        for (value in loadingList) {
            if ((value.position.latitude).toString() == (latLng.latitude).toString()
                && (value.position.longitude).toString() == (latLng.longitude).toString()
            ) {
                isLoaded = true
                break
            }
        }
        return isLoaded
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun setCenter() {
        if (center == null) {
            center = CldCoordUtil.wgs84ToCLDEx(30.867198, 120.102398, 0.0)
            Log.e("center", "center = $center")
        }
        cldMap?.setMapCenter(center)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        setCenter()
        mZoomSize = 0
        refreshData()
    }

    override fun onDestroy() {
        super.onDestroy()
        switchReport(false)
        report = false
        isInterrupt = true
        subscribe = null
        try {
            cldMap?.removeAllOverlay()
        } catch (e: Exception) {

        }
        mapView.destroy()
    }

    private var report = true
    override fun initView() {
        initImmersionBar(view = toolbar,dark = false)
        val name = intent.getStringExtra("name")
        tv_name.text = "学员轨迹:$name"
        personId = intent.getStringExtra("personId")
        projectId = intent.getStringExtra("projectId")
        taskId = intent.getStringExtra("taskId")
        switchReport(true)
        cldMap = mapView.map
        cldMap?.mapDisplayMode = 2
        mapView.isNestedScrollingEnabled = false
        mapView.setTurnMap2ChangeMode(false)
        cldMap?.mapRotateAngle = 0
        cldMap?.mapViewMode = MapStatusType.MAP_TYPE_2DNorth
        cldMap?.setLocationIconEnabled(false)
        cldMap?.enableRouteOverview(false)
        cldMap?.setOnMapRouteSelectListener {
            LogUtils.e("地图旋转了")
        }
        cldMap?.setOnMapDoubleFingerOverlookAngleListener {

        }
        cldMap?.isTrafficEnabled = false
        setCenter()
        Thread {
            while (report) {
                askReport()
                Thread.sleep(5000)
            }
        }.start()
    }

    override fun initListener() {
        iv_back.setOnClickListener {
            finish()
        }
        //change from xiezekai
        iv_send.visibility = View.INVISIBLE
        iv_send.setOnClickListener {
            //发上报位置
        }
    }


    @SuppressLint("WrongConstant")
    private fun switchReport(start: Boolean) {
        try {
            val switchByte = if (start) {
                0x01.toByte()
            } else {
                0x02.toByte()
            }
            val projectBytes = HexUtil.decodeHex(projectId.toCharArray())
            val personIdBytes = HexUtil.decodeHex(personId.toCharArray())
            val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
            val bytes = arrayOf(
                0xE7.toByte(),
                0xEA.toByte(),
                0x54.toByte(),
                20,
                switchByte,
                projectBytes[0],
                projectBytes[1],
                (phoneId.toInt() and 0xff).toByte(),
                personIdBytes[0],
                personIdBytes[1],
                personIdBytes[2],
                personIdBytes[3],
                personIdBytes[4],
                personIdBytes[5],
                personIdBytes[6],
                personIdBytes[7],
                personIdBytes[8],
                personIdBytes[9],
                personIdBytes[10],
                personIdBytes[11],
                personIdBytes[12],
                personIdBytes[13],
                personIdBytes[14],
                personIdBytes[15],
                0,
                0
            )
            val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 24)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            bytes[24] = shortToBytes[0]
            bytes[25] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
            Log.e("SEND", "SEND==>$formatHexString")
            //showToast("SEND==>$formatHexString")
            val sndData = SndData(bytes.toByteArray(), bytes.size)
            loRaManager.sendContent(sndData)
        } catch (e: Exception) {
            LogUtils.e("MapActivity", "error is $e")
        }

    }

    @SuppressLint("WrongConstant")
    private fun askReport() {
        //change from xiezekai 0x03 to 0x01
        try {
            val switchByte = 0x01.toByte()
            val projectBytes = HexUtil.decodeHex(projectId.toCharArray())
            val personIdBytes = HexUtil.decodeHex(personId.toCharArray())
            val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
            val bytes = arrayOf(
                0xE7.toByte(),
                0xEA.toByte(),
                0x54.toByte(),
                20,
                switchByte,
                projectBytes[0],
                projectBytes[1],
                (phoneId.toInt() and 0xff).toByte(),
                personIdBytes[0],
                personIdBytes[1],
                personIdBytes[2],
                personIdBytes[3],
                personIdBytes[4],
                personIdBytes[5],
                personIdBytes[6],
                personIdBytes[7],
                personIdBytes[8],
                personIdBytes[9],
                personIdBytes[10],
                personIdBytes[11],
                personIdBytes[12],
                personIdBytes[13],
                personIdBytes[14],
                personIdBytes[15],
                0,
                0
            )
            val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 24)
            val shortToBytes = HexUtil.shortToBytes(calculateCrc)
            bytes[24] = shortToBytes[0]
            bytes[25] = shortToBytes[1]
            val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
            Log.e("SEND", "SEND==>$formatHexString")
            //showToast("SEND==>$formatHexString")
            val sndData = SndData(bytes.toByteArray(), bytes.size)
            loRaManager.sendContent(sndData)
        } catch (e: Exception) {
            LogUtils.e("MapActivity", "error is $e")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshLoc(e: RefreshLocEvent) {
        refreshData()
    }

}
