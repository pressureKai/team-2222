package com.jiangtai.team.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jiangtai.team.R
import com.jiangtai.team.application.App
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.bean.*
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MajorRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.ui.signIn.SignlnFragmentUtil
import com.jiangtai.team.util.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.math.abs


/**
 * @author xiezekai
 * @des  地图碎片 （make it work, make it right, make it fast）
 * @create time 2021/8/3 8:46 上午
 */
class MapFragment : BaseFragment(), View.OnClickListener , AMapLocationListener {
    private var eleMode: Boolean by Preference(Constant.ELE_MODE, false)
    private var eleDistance: Int by Preference(Constant.ELE_DISTANCE, 0)
    private var eleTime: Int by Preference(Constant.ELE_TIME, 0)
    private var eleStartTime: Long by Preference(Constant.ELE_START_TIME, 0L)
    private var lastCommitTime: Long by Preference(Constant.LAST_COMMIT_TIME, 0L)


    private var latCenter: Double by Preference(Constant.LAT_CENTER, 0.toDouble())
    private var lngCenter: Double by Preference(Constant.LNG_CENTER, 0.toDouble())

    //默认湖州中心点 （手持机自身位置）
    private var center: LatLng? = null
    private var cldMap: AMap? = null

    //电子围栏(圆圈)列表
    private val eleList: ArrayList<Circle> = ArrayList()
    private val sosMap: ConcurrentHashMap<String, Marker> = ConcurrentHashMap()
    private val uploadMap: ConcurrentHashMap<String, Marker> = ConcurrentHashMap()
    private val infoWindowMap: ConcurrentHashMap<String, Marker> = ConcurrentHashMap()
    //    private var locationManager: CldLocationClient? = null
    private var isPause: Boolean = false

    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")
    private var mapPageMajorProject = ""

    //是否是第一次请求定位
    private var isFirstRequestLocation = true
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    override fun attachLayoutRes(): Int {
        return R.layout.fragment_map
    }


    override fun useEventBus(): Boolean {
        return true
    }

    override fun initView(view: View) {
        initImmersionBar(dark = false)
        initMap()
        initListener()
        initData()
       mCurrentMajorProject= SignlnFragmentUtil.sharSignlnfragment(mContext)
       if (!mCurrentMajorProject.isNotEmpty()){
           SignlnFragmentUtil.popwindow(mContext)
           mCurrentMajorProject= SignlnFragmentUtil.sharSignlnfragment(mContext)
         }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView_fragment.onCreate(savedInstanceState);
    }


    /**
     * @des 初始化数据
     * @time 2021/8/16 12:07 上午
     */
    private fun initData() {
        LogUtils.e("MapFragment initData")
        requireActivity()?.let {
            it.runOnUiThread {
                checkTaskEnable()
                 val message = if (mCurrentMajorProject.isNotEmpty()) {
                    val checkProjectExist = checkProjectExist()
                    if (checkProjectExist.isNotEmpty()) {
                        checkProjectExist
                    } else {
                        "请选择计划任务"
                    }
                } else {
                    "请选择计划任务"
                }



                if(mCurrentMajorProject.isNotEmpty() && message == "请选择计划任务"){
                    mCurrentMajorProject = ""
                }
               // tv_title.text = message
            }
        }
    }


    /**
     * @des 确认是否可以点击到选择任务页面
     * @time 2021/8/16 12:13 上午
     */
    private fun checkTaskEnable() {
        val find = LitePal.where("isMajor=?", "1").find(TaskBean::class.java)
//        if (find.size > 0) {
//            task_icon.visibility = View.VISIBLE
//        } else {
//            task_icon.visibility = View.INVISIBLE
//        }
    }


    /**
     * @des 确认ProjectID所对应的Project 是否存在
     * @time 2021/8/16 3:23 下午
     */
    private fun checkProjectExist(): String {
        var taskName = ""
     val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)

        if (find.isNotEmpty()) {
            taskName = find.first().projectName
        }
        return taskName
    }


    private fun checkLocation() {
        cldMap?.let {
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
                    mLocationClient = AMapLocationClient(requireActivity())
                    //初始化定位参数
                    mLocationOption = AMapLocationClientOption()
                    //设置定位回调监听
                    mLocationClient!!.setLocationListener(this)
                    //设置为高精度定位模式
                    mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
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
    /**
     * @des 初始化地图
     * @time 2021/8/6 8:41 上午
     */
    private fun initMap() {
        cldMap = mapView_fragment.map
        mapView_fragment.isNestedScrollingEnabled = false

        if (center == null) {
            //  center = LatLng(39.7375821, 116.2489938) 116.379743,39.995899
            center = LatLng(39.995173, 116.390986)

        }


        Handler(Looper.getMainLooper()).postDelayed({
            //高德地图监听位置变化
            checkLocation()
            cldMap?.moveCamera(
                CameraUpdateFactory.newLatLng(
                    center
                )
            )
            setCenterDes(center!!)
            initEle()

            cldMap?.isMyLocationEnabled = true
            val myLocationStyle = MyLocationStyle()
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_location_blue))
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER)
            myLocationStyle.showMyLocation(true)
            cldMap?.myLocationStyle = myLocationStyle
        }, 10)

        cldMap?.isTrafficEnabled = false

        cldMap?.setOnMarkerClickListener { p0 ->
            try {
                onMarkerClick(p0)
            } catch (e: Exception) {
                LogUtils.e("onMapClick error is $e")
            }
            true
        }



        Handler(Looper.getMainLooper()).postDelayed(
            {
                initEle()
                reShowLocation()
            }, 300
        )

        cldMap?.uiSettings!!.setLogoBottomMargin(-100)
    }

    /**
     * @des 初始化周期上报
     * @time 2021/8/11 6:05 下午
     */
    private fun initEle(moveCamera: Boolean? = false) {
        try {
            var realMoveCamera = moveCamera
            if (eleMode) {
                if (firstShow) {
                    realMoveCamera = firstShow
                }
                Glide.with(circle_report).load(R.mipmap.icon_circled).into(circle_report)
                circle_des.setTextColor(requireActivity().resources.getColor(R.color.color_blue_2979FF))
                drawCircle(eleDistance, center!!, moveCamera = realMoveCamera)
                firstShow = false

                Handler(Looper.getMainLooper()).postDelayed({
                    eleCheckPositionDistance()
                }, 200)
            } else {
                Glide.with(circle_report).load(R.mipmap.circle_report_icon).into(circle_report)
                circle_des.setTextColor(requireActivity().resources.getColor(R.color.black))
            }
        } catch (e: Exception) {
            LogUtils.e("initEle error is $e")
        }

    }

    private var phoneId: String by Preference(Constant.PHONE_ID, "")

    /**
     * @des 命令列表弹窗
     * @time 2021/8/3 7:56 下午
     */
    private fun showOrderListDialog() {
        DialogHelper.instance?.showOrderListDialog(activity = requireActivity(),
            object : DialogHelper.RemindDialogClickListener {
                override fun onRemindDialogClickListener(
                    positive: Boolean,
                    message: String
                ) {
                    if (positive) {
                        when (message) {
                            "0" -> {
                                //点击自定义命令
                                showOrderInputDialog()
                            }
                            "1" -> {
                                //点击开饭命令
                                ToastUtils.showShort("点击开饭命令")
                                requireActivity()?.let {
                                    if (phoneId.isNotEmpty()) {

                                      if (mCurrentMajorProject.isNotEmpty()) {

                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,

                                                "开饭"
                                            )
                                        } else {
                                            ToastUtils.showShort("请选择计划任务")
                                        }
                                    } else {
                                        ToastUtils.showShort("请设置手持机ID")
                                    }
                                }
                            }
                            "2" -> {
                                //点击集合命令
                                ToastUtils.showShort("点击集合命令")
                                requireActivity()?.let {
                                    if (phoneId.isNotEmpty()) {
                                       if (mCurrentMajorProject.isNotEmpty()) {

                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                 mCurrentMajorProject,

                                                "集合"
                                            )
                                        } else {
                                            ToastUtils.showShort("请选择计划任务")
                                        }
                                    } else {
                                        ToastUtils.showShort("请设置手持机ID")
                                    }
                                }

                            }
                            "3" -> {
                                //点击休息命令
                                ToastUtils.showShort("点击休息命令")
                                requireActivity()?.let {
                                    if (phoneId.isNotEmpty()) {
                                         if (mCurrentMajorProject.isNotEmpty()) {

                                            CommandUtils.actionCommand(
                                                it,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                 mCurrentMajorProject,

                                                "休息"
                                            )
                                        } else {
                                            ToastUtils.showShort("请选择计划任务")
                                        }
                                    } else {
                                        ToastUtils.showShort("请设置手持机ID")
                                    }
                                }
                            }
                        }
                    }
                    DialogHelper.instance?.hintOrderListDialog()
                }
            })
    }

    /**
     * @des 自定义命令输入框
     * @time 2021/8/3 7:00 下午
     */
    private fun showOrderInputDialog() {
        DialogHelper.instance?.showOrderInputDialog(activity = requireActivity(),
            "",
            "发送", object : DialogHelper.RemindDialogClickListener {
                override fun onRemindDialogClickListener(
                    positive: Boolean,
                    message: String
                ) {
                    if (positive) {
                        if (message.isNotEmpty()) {
                            //发送自定义命令
                            ToastUtils.showShort("发送 $message")
                            requireActivity()?.let {
                                if (phoneId.isNotEmpty()) {
                                  if (mCurrentMajorProject.isNotEmpty()) {

                                        CommandUtils.actionCommand(
                                            it,
                                            phoneId.toInt(),
                                            getCurrentTaskId(),
                                            mCurrentMajorProject,

                                            message
                                        )
                                    } else {
                                        ToastUtils.showShort("请选择计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                        }
                    }
                    DialogHelper.instance?.hintOrderInputDialog()
                }
            })
    }

    /**
     * @des 绘制电子围栏
     * @time 2021/8/3 1:47 下午
     * @params 半径 radius m 为单位， center LatLng 中心点
     */
    private fun drawCircle(radius: Int, center: LatLng, change: Boolean? = false,moveCamera:Boolean?= false) {
        if(moveCamera!!){
            cldMap?.moveCamera(
                CameraUpdateFactory.newLatLng(
                    center
                )
            )
        }

        if (change!!) {
            eleDistance = radius
        }


        eleCheckPositionDistance(false)
        if (eleDistance != 0) {
            cldMap?.let {
                for (value in eleList) {
                    value.remove()
                }
                //中心点标记 待完成
                val fillColor = CircleOptions()
                    .center(center)
                    .radius(eleDistance.toDouble())
                    .zIndex(-999f)
                    .fillColor(resources.getColor(R.color.emergency_color))
                    .strokeColor(resources.getColor(R.color.emergency_color))

                LogUtils.e("EleDistance $eleDistance")
                eleList.add(it.addCircle(fillColor))

            }
        }
    }



    /**
     * @des 周期上报时间设置
     * @time 2021/8/3 8:41 下午
     */
    private fun circleReportTimeSet() {
        DialogHelper.instance?.showCircleReportTimeSetDialog(activity = requireActivity(),
            object : DialogHelper.RemindDialogClickListener {
                override fun onRemindDialogClickListener(
                    positive: Boolean,
                    message: String
                ) {
                    if (positive) {


                        var circleTime = 0
                        var circleRadius = 0
                        eleMode = true
                        try {
                            if (message.contains(",")) {
                                val split = message.split(",")
                                circleTime = split[0].toInt()
                                circleRadius = split[1].toInt()
                                //绘制电子围栏
                                drawCircle(circleRadius, center!!,true,moveCamera = true)
                            } else {
                                circleTime = message.toInt()
                            }
                        } catch (e: java.lang.Exception) {

                        }
                        eleTime = circleTime
                        eleStartTime = System.currentTimeMillis()


                    }
                    initEle()
                    DialogHelper.instance?.hintCircleReportTimeSetDialog()

                    if (positive) {
//                        //展示通信渠道弹框
                        requireActivity()?.let {
                            DialogHelper.instance?.showChannelDialog(it,
                                object : DialogHelper.RemindDialogClickListener {
                                    override fun onRemindDialogClickListener(
                                        positive: Boolean,
                                        message: String
                                    ) {
                                        DialogHelper.instance?.hintChannelDialog()
                                    }
                                })
                        }
                    }
                }
            })
    }

    override fun lazyLoad() {

    }

    override fun onPause() {
        super.onPause()
        mapView_fragment?.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            isPause = true
        } catch (e: Exception) {
            LogUtils.e("stop thread error is $e")
        }
        mapView_fragment?.onDestroy()
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {
        super.onResume()
        mapView_fragment?.onResume()
        initData()
        Handler(Looper.getMainLooper()).postDelayed({
            restartSOSAnimation()
            if (mapPageMajorProject.isEmpty()) {
                mapPageMajorProject = mCurrentMajorProject
             } else {
               val b = mCurrentMajorProject.isNotEmpty() && mCurrentMajorProject != mapPageMajorProject
                 if (b) {
                    clear(false)
                     mapPageMajorProject = mCurrentMajorProject
                     reShowLocation()
                }
            }
        }, 100)
    }


    private fun reShowLocation() {
        val personList = getPersonList()
        val arrayList = ArrayList<LocationReceiver>()
        for (value in personList) {
            val find = LitePal.where("userId = ?", value.personId.toUpperCase())
                .find(LocationReceiver::class.java)
            if (find.isNotEmpty()) {
                arrayList.add(find.last())
            }
        }


        for (value in arrayList) {
            loadLocationReceiver(value)
        }

    }

    var firstShow = true
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isPause = false
            initImmersionBar(dark = false)
            initData()
            initEle()
        } else {
            isPause = true
        }
    }


    companion object {
        fun getInstance(): MapFragment = MapFragment()
    }

    /**
     * @des 初始化控件监听
     * @time 2021/8/3 7:01 下午
     */
    private fun initListener() {
        order_layout.setOnClickListener(this)
        search_people_layout.setOnClickListener(this)
        circle_report_layout.setOnClickListener(this)
        location_report_layout.setOnClickListener(this)
        electronic_report_layout.setOnClickListener(this)
        trash_layout.setOnClickListener(this)
        location_my_shelf.setOnClickListener(this)
        tv_title.setOnClickListener(this)
        task_icon.setOnClickListener(this)

    }


    /**
     * @des 取消周期上报
     * @time 2021/8/11 5:46 下午
     */
    private fun cancelCircleReport() {
        DialogHelper.instance?.showCancelEleReportDialog(activity = requireActivity(),
            object : DialogHelper.RemindDialogClickListener {
                override fun onRemindDialogClickListener(positive: Boolean, message: String) {
                    if (positive) {
                        eleDistance = 0
                        eleMode = false
                        for (value in eleList) {
                            value.remove()
                        }
                        initEle()
                        eleCheckPositionDistance(false)
                    }
                    DialogHelper.instance?.hintCancelEleReportDialog()
                }
            })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.order_layout -> {
                    //命令列表弹窗
                    showOrderListDialog()
                }
                R.id.circle_report_layout -> {
                    //周期上报时间设置
                    if (!eleMode) {
                        if (phoneId.isNotEmpty()) {
                            if (mCurrentMajorProject.isNotEmpty()) {
                                 val personList = getPersonList()
                                if (personList.isNotEmpty()) {
                                    circleReportTimeSet()
                                } else {
                                    ToastUtils.showShort("当前任务暂无人员信息")
                                }
                            } else {
                                ToastUtils.showShort("请选择计划任务")
                            }
                        } else {
                            ToastUtils.showShort("请设置手持机ID")
                        }
                    } else {
                        cancelCircleReport()
                    }
                }
                R.id.search_people_layout -> {
                    //搜索某一个人员
                    showPeopleListDialog()
                }
                R.id.location_report_layout -> {
                    //点击位置上报命令
                    ToastUtils.showShort("查询位置信息")
                    requireActivity()?.let {
                        if (phoneId.isNotEmpty()) {
                            if (mCurrentMajorProject.isNotEmpty()) {
                                 val personList = getPersonList()
                                if (personList.isNotEmpty()) {
                                    Thread {
                                        Handler(Looper.getMainLooper()).post {
                                            CommandUtils.inquiryDeviceCommand2(
                                                it,
                                                phoneId.toInt(),
                                             mCurrentMajorProject.toInt()

                                            )
                                        }
                                    }.start()
                                } else {
                                    ToastUtils.showShort("当前任务暂无人员信息")
                                }

                            } else {
                                ToastUtils.showShort("请选择计划任务")
                            }
                        } else {
                            ToastUtils.showShort("请设置手持机ID")
                        }
                    }
                }
                R.id.electronic_report_layout -> {
                    //电子围栏
                    showEleReportSetDialog()
                }


                R.id.trash_layout -> {
                    //一键清除
                    requireActivity()?.let {
                        DialogHelper.instance?.showClearRemindDialog(it,
                            object : DialogHelper.RemindDialogClickListener {
                                override fun onRemindDialogClickListener(
                                    positive: Boolean,
                                    message: String
                                ) {
                                    if (positive) {
                                        clear()
                                    }
                                    DialogHelper?.instance?.hintClearDialog()
                                }
                            })
                    }
                }
                R.id.location_my_shelf -> {
                    //手持机位置
                    center?.let {
                        cldMap?.moveCamera(CameraUpdateFactory.newLatLng(center))
                    }
                }
                R.id.tv_title,R.id.task_icon -> {
                    //选择Task页面
//                    requireActivity()?.let {
//                        val intent = Intent(it, MajorTaskActivity::class.java)
//                        it.startActivity(intent)
//                    }
                    SignlnFragmentUtil.popwindow(mContext)
                    mCurrentMajorProject= SignlnFragmentUtil.sharSignlnfragment(mContext)
                }
                else -> {

                }
            }
        }
    }


    /**
     * @des 显示人员列表界面
     * @time 2021/8/16 3:53 下午
     */
    private fun showPeopleListDialog() {
        val find = LitePal.where("projectId = ?", mCurrentMajorProject)
            .find(Project::class.java)


        val arrayList = ArrayList<Person>()
        if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
            val peopleId = find.first().peopleId
            val split = peopleId.split(",")
            for (value in split) {
                val find1 =
                    LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                arrayList.addAll(find1)
            }
        }
        if (arrayList.size > 0) {
            DialogHelper.instance?.showPeopleListDialog(requireActivity(),
                object : DialogHelper.RemindDialogClickListener {
                    override fun onRemindDialogClickListener(positive: Boolean, message: String) {
                        if (positive) {
                            requireActivity()?.let {
                                if (phoneId.isNotEmpty()) {
                                   if (mCurrentMajorProject.isNotEmpty()) {

                                        CommandUtils.searchCommand(
                                            it,
                                            phoneId.toInt(),
                                            message
                                        )
                                        ToastUtils.showShort("发送搜救指令成功")
                                    } else {
                                        ToastUtils.showShort("请选择专项计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                        }
                        DialogHelper.instance?.hintPeopleListDialog()
                    }
                })
        } else {
            ToastUtils.showShort("当前计划任务暂无人员列表")
        }
    }

    /**
     * @des 显示电子围栏设置弹窗
     * @time 2021/8/4 10:30 上午
     */
    private fun showEleReportSetDialog() {
        DialogHelper.instance?.showEleReportSetDialog(activity = requireActivity(),
            object : DialogHelper.RemindDialogClickListener {
                override fun onRemindDialogClickListener(
                    positive: Boolean,
                    message: String
                ) {
                    if (positive) {
                        if (message.isNotEmpty()) {
                            //半径设置
                            try {
                                drawCircle(message.toInt(), center!!)
                                // cldMap?.setMapCenter(center)
                                // 缩放到合适比例
                            } catch (e: Exception) {

                            }
                        }
                    }
                    DialogHelper.instance?.hintEleReportSetDialog()
                }
            })
    }

    override fun onStop() {
        super.onStop()
        mapView_fragment.onPause()
    }

    /**
     * @des  设置实时经纬度描述
     * @time 2021/8/4 11:45 上午
     */
    @SuppressLint("SetTextI18n")
    private fun setCenterDes(center: LatLng) {8
        try {
            latLng_des.text = CommonUtil.dblToLocation(center.latitude, false) +
                    " , ${CommonUtil.dblToLocation(center.longitude, true)}"
        } catch (e: Exception) {
            LogUtils.e("error is $e")
        }
    }


    /**
     * @des 添加sos标记点
     * @time 2021/8/6 10:45 上午
     */
    private fun addSOSMarker(center: LatLng, personId: String) {
        if (personId.isEmpty()) {
            return
        }
        requireActivity()?.let {
            val uploadMarkerOptions =
                MarkerOptions().position(center)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_big))
            val addOverlay = cldMap?.addMarker(uploadMarkerOptions)
            val s =
                center.latitude.toString() + ":" + center.longitude.toString() + ":" + personId
            removePersonIdOnMap(sosMap, personId)
            //sosMap.remove(center)
            if (addOverlay != null) {
                sosMap[s] = addOverlay
            }
        }
    }


    /**
     * @des 添加普通位置标记点
     * @time 2021/8/6 2:25 下午
     */
    private fun addUploadLocationMarker(
        position: LatLng,
        personId: String,
        isDistance: Boolean? = false
    ) {
        if (personId.isEmpty()) {
            return
        }
        var bg = R.mipmap.upload_location_icon
        if (isDistance!!) {
            bg = R.mipmap.location_big
        }
        requireActivity()?.let {
            val uploadMarkerOptions =
                MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(bg))
            val addOverlay = cldMap?.addMarker(uploadMarkerOptions)
            val key =
                position.latitude.toString() + ":" + position.longitude.toString() + ":" + personId
            removePersonIdOnMap(uploadMap, personId)
            if (addOverlay != null) {
                uploadMap[key] = addOverlay
            }
        }
    }

    /**
     * @des 移除Map
     * @time 2021/8/20 2:09 上午
     */
    private fun removePersonIdOnMap(map: ConcurrentHashMap<String, Marker>, personId: String) {
        try {
            val beRemoveKeys = ArrayList<String>()
            val iterator = map.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.key.isNotEmpty()) {
                    val split = next.key.split(":")
                    if (split.last() == personId) {
                        beRemoveKeys.add(next.key)
                    }
                }
            }

            for (key in beRemoveKeys) {
                val overlay = map[key]
                overlay?.remove()
                map.remove(key)
            }
        } catch (e: Exception) {

        }

    }

    /**
     * @des 开启SOS动画
     * @time 2021/8/6 10:46 上午
     */
    //单线程 -> currentIndex （多线程同时改变该值）
    private var currentIndex = 0

    @SuppressLint("CheckResult")
    private fun startSOSAnimation() {
        Observable.create<Int> {
            isPause = false
            while (true) {
                if (!isPause) {
                    try {
                        addCurrentThreadName(Thread.currentThread().name + Thread.currentThread().id.toString())
                        if (threadNameList.size == 0
                            || (threadNameList.size > 0
                                    && (Thread.currentThread().name + Thread.currentThread().id.toString()) == getLastThreadName())
                        ) {
                            //防止多线程同时改变同一个值
                            var index = 0
                            index = if (currentIndex == 0) {
                                1
                            } else {
                                0
                            }
                            currentIndex = index
//                            LogUtils.e("send $currentIndex thread name is ${getLastThreadName()}")
                            changeSOS()
                            Thread.sleep(1800)
                        } else {
                            try {
                                Thread.sleep(1200000)
                                Thread.interrupted()
                            }catch (e:Exception){

                            }
                        }
                    } catch (e: java.lang.Exception) {
                        LogUtils.e("MapFragment error is $e")
                        Handler(Looper.getMainLooper()).postDelayed({
                            restartSOSAnimation()
                        },200)
                    }

                }
            }
        }.subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe {}
    }

    /**
     * @des 重新启动SOS Animation
     * @time 2021/8/12 9:07 上午
     */
    private fun restartSOSAnimation() {
        try {
            startSOSAnimation()
        } catch (e: Exception) {
            LogUtils.e("MapFragment restart sos animation error is $e")
        }
    }

    /**
     * @des 改变SOS背景
     * @time 2021/8/11 6:35 下午
     */
    private fun changeSOS() {
        try {
            if(!isPause){
                val iterator = sosMap.iterator()
                val mip = if (currentIndex == 0) {
                    R.mipmap.location_big
                } else {
                    R.mipmap.location_big_blue
                }

                val keys = ArrayList<String>()
                keys.clear()

                val overlays = ArrayList<Marker>()
                overlays.clear()


                val beRemoverOverlays = ArrayList<Marker>()
                beRemoverOverlays.clear()

                while (iterator.hasNext()) {
                    val next = iterator.next()
                    val key = next.key

                    val split = key.split(":")
                    val latLng = LatLng(split[0].toDouble(), split[1].toDouble())

                    val uploadMarkerOptions =
                        MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(mip))
                    beRemoverOverlays.add(next.value)
                    val addOverlay = cldMap?.addMarker(uploadMarkerOptions)
                    if (addOverlay != null) {
                        keys.add(key)
                        overlays.add(addOverlay)
                    }
                }



                for (value in beRemoverOverlays) {
                    value.remove()
                }

                for ((index, value) in keys.withIndex()) {
                    sosMap[value] = overlays[index]
                }
            }
        } catch (e: Exception) {
            LogUtils.e("MapFragment changeSOS error is $e")
        }

    }

    /**
     * @des 添加SOS信息窗口
     * @time 2021/8/5 4:10 下午
     */
    private fun addSOSInfoWindow(position: LatLng, locationReceiver: LocationReceiver? = null) {
        locationReceiver?.let { receiver ->
            if (receiver.userId.isNotEmpty()) {
                requireActivity()?.let {
                    //vibrator()


                    Handler(Looper.getMainLooper()).postDelayed({
                        val inflate = View.inflate(it, R.layout.map_info_window, null)
                        val latLng = inflate.findViewById<TextView>(R.id.latLng)
                        val latLng2 = inflate.findViewById<TextView>(R.id.latLng2)
                        val time = inflate.findViewById<TextView>(R.id.time)
                        val info = inflate.findViewById<TextView>(R.id.info)
                        try {
                            val decimalFormat = DecimalFormat("#.000000")

                            val latitude = position.latitude
                            val longitude = position.longitude

                            latLng.text = "纬度:${0.000000}"
                            latLng2.text = "经度:${0.000000}"

                            latLng.text = "纬度:${decimalFormat.format(locationReceiver.lat.toDouble())}"
                            latLng2.text = "经度:${decimalFormat.format(locationReceiver.lng.toDouble())}"


                            locationReceiver?.let {
                                val personInfo = getPersonInfo(locationReceiver.userId)
                                var infoText = "姓名: - -"
                                personInfo?.let { person ->
                                    infoText = "姓名: ${person.name}"
                                }
                                info.text  = infoText + "心率: ${it.heartRate}"

                            }

                        } catch (e: Exception) {
                            LogUtils.e("MapFragment format error is $e")
                        }

                        try {
                            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                            val date = Date()
                            time.text = "时间:" + sdf.format(date)
                        } catch (e: Exception) {
                            LogUtils.e("MapFragment parse error is $e")
                        }
                        val marker = MarkerOptions().position(position).icon(
                            BitmapDescriptorFactory.fromView(inflate)
                        )
                        val infoOverlay = cldMap?.addMarker(marker)
                        infoOverlay?.let {
                            infoOverlay.isVisible =  !infoOverlay.isVisible
                        }
                        if (infoOverlay != null) {
                            if (position.latitude != 0.toDouble() && position.longitude != 0.toDouble()) {

                                val s =
                                    position.latitude.toString() + ":" + position.longitude.toString() + ":" + receiver.userId

                                removePersonIdOnMap(infoWindowMap, receiver.userId)
                                infoWindowMap[s] = infoOverlay

                            }
                        }
                    },200)


                }
            }

        }

    }
    /**
     * @des 添加SOS信息窗口
     * @time 2021/8/5 4:10 下午
     */
    private fun addInfoWindow(position: LatLng, locationReceiver: LocationReceiver? = null) {
        locationReceiver?.let { receiver ->
            if (receiver.userId.isNotEmpty()) {
                requireActivity()?.let {
                    //vibrator()


                    Handler(Looper.getMainLooper()).postDelayed({
                        val inflate = View.inflate(it, R.layout.map_window, null)

                        val latLng = inflate.findViewById<TextView>(R.id.islatLng)
                        val latLng2 = inflate.findViewById<TextView>(R.id.islatLng2)
                        val time = inflate.findViewById<TextView>(R.id.istime)
                        val info = inflate.findViewById<TextView>(R.id.isinfo)
                        try {
                            val decimalFormat = DecimalFormat("#.000000")
                            val latitude = position.latitude
                            val longitude = position.longitude

                            latLng.text = "纬度:${0.000000}"
                            latLng2.text = "经度:${0.000000}"

                            latLng.text = "纬度:${decimalFormat.format(locationReceiver.lat.toDouble())}"
                            latLng2.text = "经度:${decimalFormat.format(locationReceiver.lng.toDouble())}"


                            locationReceiver?.let {
                                val personInfo = getPersonInfo(locationReceiver.userId)
                                var infoText = "姓名: - -"
                                personInfo?.let { person ->
                                    infoText = "姓名: ${person.name}"
                                }
                                info.text  = infoText + "心率: ${it.heartRate}"

                            }

                        } catch (e: Exception) {
                            LogUtils.e("MapFragment format error is $e")
                        }

                        try {
                            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                            val date = Date()
                            time.text = "时间:" + sdf.format(date)
                        } catch (e: Exception) {
                            LogUtils.e("MapFragment parse error is $e")
                        }
                        val marker = MarkerOptions().position(position).icon(
                            BitmapDescriptorFactory.fromView(inflate)
                        )
                        val infoOverlay = cldMap?.addMarker(marker)
                        infoOverlay?.let {
                            infoOverlay.isVisible =  !infoOverlay.isVisible
                        }
                        if (infoOverlay != null) {
                            if (position.latitude != 0.toDouble() && position.longitude != 0.toDouble()) {

                                val s =
                                    position.latitude.toString() + ":" + position.longitude.toString() + ":" + receiver.userId

                                removePersonIdOnMap(infoWindowMap, receiver.userId)
                                infoWindowMap[s] = infoOverlay

                            }
                        }
                    },200)


                }
            }

        }

    }

    /**
     * @des 获取个人信息
     * @time 2021/8/19 10:49 下午
     */
    private fun getPersonInfo(id: String): Person? {
        val find = LitePal.where("personId = ?", id).find(Person::class.java)
        return if (find.isNotEmpty()) {
            find.last()
        } else {
            null
        }
    }


    /**
     * @des maker被点击SOSInfoWindow的显示控制
     * @time 2021/8/6 2:08 下午
     */
    private fun onMarkerClick(p0: Marker?) {
        p0?.let {
            //隐藏与显示 InfoWindow
//            val sosIterator = sosMap.iterator()
            val infoIterator = infoWindowMap.iterator()
            val targetPositionX = it.position.latitude
            val targetPositionY = it.position.longitude


//            LogUtils.e("on Mark click targetPositionX ${targetPositionX} targetPositionY ${targetPositionY}")
//            var isClickSOSMarker = false
//            while (sosIterator.hasNext()) {
//                val next = sosIterator.next()
//                val positionXDifference = abs(next.value.position.latitude - targetPositionX)
//                val positionYDifference = abs(next.value.position.longitude - targetPositionY)
//
////                LogUtils.e(
////                    "targetPositionX $targetPositionX " + "next positionX ${next.value.position.x}" +
////                            "\ntargetPositionY $targetPositionY next positionY is ${next.value.position.y} \n infoWindowMap size is ${infoWindowMap.size}"
////                )
//
//                try {
//                    if (positionXDifference < 0.000001
//                        && positionYDifference < 0.000001
//                    ) {
//                        isClickSOSMarker = true
//                        val overlay = infoWindowMap[next.key]
//                        if (overlay != null) {
//                            overlay.isVisible = !overlay.isVisible
//                        }
//                    }
//                } catch (e: Exception) {
//                    LogUtils.e("onMarkClick error is $e")
//                }
//            }



//            if (!isClickSOSMarker) {
            while (infoIterator.hasNext()) {
                val next = infoIterator.next()
                val positionXDifference = abs(next.value.position.latitude - targetPositionX)
                val positionYDifference = abs(next.value.position.longitude - targetPositionY)

                LogUtils.e(
                    "info targetPositionX $targetPositionX " + " next info positionX ${next.value.position.latitude}" +
                            "\n info targetPositionY $targetPositionY next info positionY is ${next.value.position.longitude} \n infoWindowMap size is ${infoWindowMap.size}"
                )

                if (positionXDifference < 0.000001
                    && positionYDifference < 0.000001
                ) {
                    val overlay = next.value
                    overlay.isVisible = !overlay.isVisible
                }
            }
//            }
        }
    }


    /**
     * @des 一键清除
     * @time 2021/8/6 2:34 下午
     */
    private fun clear(isClearSOS: Boolean? = true) {
        val iterator = uploadMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            next.value.remove()
        }
        uploadMap.clear()
        uploadMap.clear()
        uploadMap.clear()

        if (isClearSOS!!) {
            val iteratorSOS = sosMap.iterator()
            while (iteratorSOS.hasNext()) {
                val next = iteratorSOS.next()
                next.value.remove()
            }
            sosMap.clear()

            val iteratorWindow = infoWindowMap.iterator()
            while (iteratorWindow.hasNext()) {
                val next = iteratorWindow.next()
                next.value.remove()
            }
            infoWindowMap.clear()

        }

        if(isClearSOS){
            ToastUtils.showShort("清除成功")
        }
    }


    /**
     * @des 电子围栏模式 --> 确认人员信息 （震动）
     * @time 2021/8/6 3:16 下午
     */
    private fun eleCheckPositionDistance(fromNewAddPosition: Boolean? = false) {
        if (eleMode) {
            if (eleDistance != 0) {
                //默认点
                val position = LatLng(39.995899, 116.379743)

                center?.let {
                    if ((center!!.latitude != 0.toDouble() && center!!.longitude != 0.toDouble())
                        && (center!!.latitude != position.latitude && center!!.longitude != position!!.longitude)
                    ) {

                        var vibrator = false
                        val iterator = uploadMap.iterator()



                        if(uploadMap.size > 0){
                            //需要被改变的位置图标
                            val outDistance = ArrayList<String>()
                            val inDistance = ArrayList<String>()

                            while (iterator.hasNext()) {
//                                val next = iterator.next()
//                                val latLng = next.key
//                                val distance = DistanceUtil.getDistance(getKeyLatLng(latLng), center)
//                                if (distance > eleDistance) {
//                                    vibrator = true
//                                    outDistance.add(latLng)
//                                } else {
//                                    inDistance.add(latLng)
//                                }

                                try {
                                    requireActivity()?.let {
                                        if (!it.isDestroyed) {
                                            val next = iterator.next()

                                            val keyLatLng = getKeyLatLng(next.key)
                                            val distance = CommonUtil.distance(
                                                center!!.longitude,
                                                center!!.latitude,
                                                keyLatLng.longitude,
                                                keyLatLng.latitude
                                            )

                                            LogUtils.e("MapFragment distance $distance eleDistance $eleDistance")
                                            if (uploadMap.size > 0) {
                                                if (distance > eleDistance && eleDistance != 0) {
                                                    vibrator = true
                                                    outDistance.add(next.key)
                                                } else {
                                                    inDistance.add(next.key)
                                                }
                                            }

                                        }
                                    }
                                }catch (e:Exception){
                                    try {
                                        Thread.sleep(3000000000)
                                        Thread.interrupted()
                                    }catch (e:Exception){

                                    }
                                }
                            }


                            for (value in outDistance) {
                                fromKeyToMaker(value, true)
                            }
                            for (value in inDistance) {
                                fromKeyToMaker(value, false)
                            }
                            if (fromNewAddPosition!!) {
                                if (vibrator) {
                                    vibrator()
                                }
                            }
                        }





                    }
                }
            } else {
                greenPoint()
            }
        }else {
            greenPoint()
        }
    }


    private fun greenPoint(){
        val iterator = uploadMap.iterator()
        //需要被改变的位置图标
        val inDistance = ArrayList<String>()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val latLng = next.key
            inDistance.add(latLng)
        }
        for (value in inDistance) {
            fromKeyToMaker(value, false)
        }
    }



    private fun fromKeyToMaker(key: String, outDistance: Boolean) {
        try {
            val split = key.split(":")
            val lat = split[0]
            val lon = split[1]
            val personId = split[2]

            addUploadLocationMarker(
                LatLng(lat.toDouble(), lon.toDouble()),
                personId.toString(),
                outDistance
            )
        } catch (e: java.lang.Exception) {

        }

    }

    private fun getKeyLatLng(key: String): LatLng {
        var lat = 0.toDouble()
        var lng = 0.toDouble()
        try {
            val split = key.split(":")
            lat = split[0].toDouble()
            lng = split[1].toDouble()
        } catch (e: java.lang.Exception) {

        }
        return LatLng(lat, lng)
    }


    /**
     * @des 震动
     * @time 2021/8/11 5:29 下午
     */
    private fun vibrator() {
        requireActivity()?.let {
            val vibrator =
                it.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
            val patter =
                longArrayOf(1000, 1000, 50, 1000)
            vibrator.vibrate(patter, -1)
        }
    }


    /**
     * @des 内部初始化一个线程名称数组
     * @time 2021/8/10 2:52 下午
     */
    private var threadNameList: ConcurrentHashMap<Long, String> = ConcurrentHashMap()
    private fun addCurrentThreadName(name: String) {
        var repeat = false
        val iterator = threadNameList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.value == name) {
                repeat = true
                break
            }
        }
        if (!repeat) {
            threadNameList[System.currentTimeMillis()] = name
        }
    }


    private fun getLastThreadName(): String {
        var threadName = ""
        var lastTime = 0L
        val iterator = threadNameList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.key > lastTime) {
                lastTime = next.key
            }
        }

        val s = threadNameList[lastTime]

        if (s != null) {
            threadName = s
        }
        return threadName
    }

    private var sosID: Int by Preference(Constant.SOS_ID, 0)
    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun refreshTaskState(location: LocationReceiver) {
        EventBus.getDefault().removeStickyEvent(location)
        try {
            if (location.lat.toDouble() > 0 && location.lng.toDouble() > 0) {
                loadLocationReceiver(location)
                if (location.isSOS) {
                    vibrator()
                }
            } else {
                if (location.isSOS) {
                    App.getMineContext()?.let {
                        //  ToastUtils.showShort("MapFragment 经纬度 为0")
                        Handler(Looper.getMainLooper()).post {
                            NotificationHelper(it).showNotification(
                                sosID,
                                getUserNameById(userId = location.userId),
                                location.lat.toString(),
                                location.lng.toString()
                            )
                            sosID++
                        }
                        vibrator()
                    }
                }
            }
        } catch (e: Exception) {
            try {
                App.getMineContext()?.let {
                    if (location.isSOS) {
                        //      ToastUtils.showShort("MapFragment 经纬度 为0")
                        Handler(Looper.getMainLooper()).post {
                            NotificationHelper(it).showNotification(
                                sosID,
                                getUserNameById(userId = location.userId),
                                location.lat.toString(),
                                location.lng.toString()
                            )
                            sosID++
                        }
                        vibrator()
                    }
                }
            } catch (e: Exception) {
                if (location.isSOS) {
                    //    ToastUtils.showShort("收到经纬度为零发生错误 $e")
                }
            }
        }
    }


    private fun getUserNameById(userId: String): String {
        var userName = ""
        val find =
            LitePal.where("personId = ?", userId.toUpperCase()).find(Person::class.java)
        if (find.isNotEmpty()) {
            userName = find.last().name
        }
        return userName
    }

    private fun loadLocationReceiver(e: LocationReceiver,moveCamera: Boolean? = true) {
        LogUtils.e("uploadLocation ${e.userId}")
        if (e.userId.isEmpty()) {
            return
        }
        try {
            if (e.isSOS) {
                if (e.lat.toDouble() > 0 && e.lng.toDouble() > 0) {
                    val wgs84ToCLDEx =
                        LatLng(e.lat.toDouble(), e.lng.toDouble())
                    LogUtils.e("e.lat ${e.lat} e.lng ${e.lng} wgs84ToCLDEx ${wgs84ToCLDEx.latitude} wgs84ToCLDEx ${wgs84ToCLDEx.longitude}")
                    addSOSMarker(wgs84ToCLDEx, e.userId)
                    addSOSInfoWindow(wgs84ToCLDEx, e)
                    uploadLocation(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if(moveCamera!!){
                            cldMap?.moveCamera(CameraUpdateFactory.newLatLng(wgs84ToCLDEx))
                        }
                    }, 1200)
                }

            } else {
                if (e.lat.toDouble() > 0 && e.lng.toDouble() > 0) {
                    val wgs84ToCLDEx2 =
                        LatLng(e.lat.toDouble(), e.lng.toDouble())
                    addUploadLocationMarker(wgs84ToCLDEx2, e.userId)
                    addInfoWindow(wgs84ToCLDEx2, e)
                    eleCheckPositionDistance(true)
                    uploadLocation(false)
                }
            }
        } catch (e: Exception) {
            LogUtils.e("error is $e")
        }
    }

    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private fun uploadLocation(isSOS: Boolean) {
        val personList = getPersonList()
        val arrayList = ArrayList<LocationReceiver>()

        val sos = if (isSOS) "1" else "0"
        for (value in personList) {
            val find = LitePal.where(
                "userId = ? and isSOS = ?",
                value.personId.toUpperCase(), sos
            ).find(LocationReceiver::class.java)
            if (find.size > 0) {
                arrayList.add(find.last())
            }
        }


        var cmd = "personlocationindication"
        if (isSOS) {
            cmd = "personsosindication"
        }

        if (arrayList.size > 0 && serverIp.isNotEmpty()) {
            val dipperBig = DipperBig()
            dipperBig.handsetnumber = CommonUtil.getSNCode().toString()
            dipperBig.cmd = cmd
            dipperBig.lat = latCenter.toString()
            dipperBig.lng = lngCenter.toString()
            dipperBig.time = CommonUtil.formatTime(System.currentTimeMillis().toString())


            val taskPersonLoclist = if (!isSOS) {
                dipperBig.taskpersonloclist
            } else {
                dipperBig.soslist
            }

            for (value in arrayList) {
                val bigDipperPersonVi = BigDipperPersonVi()
                bigDipperPersonVi.sostype = value.sosType
                bigDipperPersonVi.taskid = value.taskId
                bigDipperPersonVi.shortid = "1"
                bigDipperPersonVi.status = "normal"
                bigDipperPersonVi.heartrate = value.heartRate
                bigDipperPersonVi.time =
                    CommonUtil.formatTime(System.currentTimeMillis().toString())
                bigDipperPersonVi.lat = value.lat
                bigDipperPersonVi.lng = value.lng
                bigDipperPersonVi.deviceid = value.userId
                taskPersonLoclist.add(bigDipperPersonVi)
            }


            val toJson = ToJsonUtil.getInstance().toJson(dipperBig)
            val requestBody =
                RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    toJson
                )

            val uploadMajorScore = MajorRetrofit.instance.api.uploadMajorLocation(requestBody)
            uploadMajorScore.compose(ThreadSwitchTransformer()).subscribe(object :
                CallbackListObserver<ResponseData>() {
                override fun onSucceed(t: ResponseData?) {
                    ToastUtils.showShort("上报成功")
                }

                override fun onFailed() {

                }
            })
        }
    }



    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun refreshTaskState(e: MqttBean) {
        EventBus.getDefault().removeStickyEvent(e)
        requireActivity()?.let {
            it.runOnUiThread {
                initEle()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun refreshEle(e: EleBean) {
        EventBus.getDefault().removeStickyEvent(e)
        requireActivity()?.let {
            it.runOnUiThread {
                initEle()
            }
        }
    }


    private fun getPersonList(): ArrayList<Person> {
        val find = LitePal.where("projectId = ?", mCurrentMajorProject)
            .find(Project::class.java)


        val arrayList = ArrayList<Person>()
        if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
            val peopleId = find.first().peopleId
            val split = peopleId.split(",")
            for (value in split) {
                LogUtils.e("value is ${value.toUpperCase()}")
                val find1 =
                    LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                arrayList.addAll(find1)
            }
        }
        return arrayList
    }


    private fun getCurrentTaskId(): String {
        var taskId = ""
         val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)

        if (find.size > 0) {
            taskId = find.first().taskId
        }
        return taskId
    }

    private var isFirstGetLocation = false
    override fun onLocationChanged(location: AMapLocation?) {
        if (null != location) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (abs(location.latitude) < 200) {
                    if (location.latitude != 0.toDouble() && location.longitude != 0.toDouble()) {
                        center = LatLng(
                            location.latitude, location
                                .longitude
                        )
                        latCenter = location.latitude
                        lngCenter = location.longitude
                        Log.e("chahcasa",lngCenter.toString())
                        setCenterDes(center!!)
                        initEle(!isFirstGetLocation)
                        if (!isFirstGetLocation) {
                            isFirstGetLocation = true
                        }
                    }
                } else {
                    if (!isFirstRequestLocation) {
                        isFirstRequestLocation = false
                        ToastUtils.showShort("无法定位，请检查网络或前往空旷位置")
                    }
                }
            },2000)
        }
    }


}