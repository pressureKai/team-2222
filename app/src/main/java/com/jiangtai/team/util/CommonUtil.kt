package com.jiangtai.team.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.blankj.utilcode.util.LogUtils
import com.jiangtai.team.application.App
import com.jiangtai.team.constant.Constant
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*


/**
 *
 */
object CommonUtil {
    val pi = 3.1415926535897932384626
    val a = 6378245.0
    val ee = 0.00669342162296594323


    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue,
            context.resources.displayMetrics
        ).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取虚拟功能键高度
     *
     * @param context
     *
     * @return
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        if (!isNavigationBarShow(activity)) {
            return 0
        }
        val resources = activity.resources
        val resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen", "android"
        )
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 全面屏（是否开启全面屏开关 0 关闭  1 开启）
     *
     * @param context
     * @return
     */
    fun isNavigationBarShow(context: Context): Boolean {
        val value = Settings.Global.getInt(context.contentResolver, getDeviceInfo(), 0)
        return value != 0
    }

    fun setMaxAspect(mContext: Context) {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = mContext.packageManager.getApplicationInfo(
                mContext.packageName,
                PackageManager.GET_META_DATA
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (applicationInfo == null) {
            throw IllegalArgumentException(" get application info = null ")
        }
        applicationInfo.metaData.putString("android.max_aspect", "2.1")
    }

    /**
     * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo都可以）
     *
     * @return
     */
    fun getDeviceInfo(): String {
        val brand = Build.BRAND
        if (TextUtils.isEmpty(brand)) return "navigationbar_is_min"
        return if (brand.equals("HUAWEI", ignoreCase = true)) {
            "navigationbar_is_min"
        } else if (brand.equals("XIAOMI", ignoreCase = true)) {
            "force_fsg_nav_bar"
        } else if (brand.equals("VIVO", ignoreCase = true)) {
            "navigation_gesture_on"
        } else if (brand.equals("OPPO", ignoreCase = true)) {
            "navigation_gesture_on"
        } else {
            "navigationbar_is_min"
        }
    }

    /**
     * 获取随机rgb颜色值
     */
    fun randomColor(): Int {
        val random = Random()
        //0-190, 如果颜色值过大,就越接近白色,就看不清了,所以需要限定范围
        var red = random.nextInt(190)
        var green = random.nextInt(190)
        var blue = random.nextInt(190)
        //使用rgb混合生成一种新的颜色,Color.rgb生成的是一个int数
        return Color.rgb(red, green, blue)
    }

    /**
     * 解决InputMethodManager引起的内存泄漏
     * 在Activity的onDestroy方法里调用
     */
    fun fixInputMethodManagerLeak(context: Context) {

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val arr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
        var field: Field? = null
        var objGet: Any? = null
        for (i in arr.indices) {
            val param = arr[i]
            try {
                field = imm.javaClass.getDeclaredField(param)
                if (field.isAccessible === false) {
                    field.isAccessible = true
                }
                objGet = field.get(imm)
                if (objGet != null && objGet is View) {
                    val view = objGet
                    if (view.context === context) {
                        // 被InputMethodManager持有引用的context是想要目标销毁的
                        field.set(imm, null) // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }

        }

    }

    /**
     * 获取当前进程名
     */
    fun getProcessName(pid: Int): String {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader!!.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim({ it <= ' ' })
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader!!.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return ""
    }

    fun getScreenHeight(activity: Activity): Int {
        return activity.windowManager.defaultDisplay.width
    }


    /**
     * 获取唯一标识
     */
    public fun getSerialNumber(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    App.getMineContext()!!,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return "";
            }

            try {
                return Build.getSerial();
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
        return Build.SERIAL;
    }


    //将Double类型的经纬度坐标转换为度分秒格式  longitude 经度  东
    fun dblToLocation(d: Double, isLongitude: Boolean): String {
        var retS = ""
        try {
            val du1 = d.toInt()
            val tp: Double = (d - du1.toDouble()) * 60
            val fen = tp.toInt()
            val miao = String.format("%.2f", abs((tp - fen) * 60))
            retS = du1.toString() + "°" + abs(fen) + "'" + miao

            val split = retS.split(".")
            retS = split[0]
            retS += if (isLongitude) {
                if (d < 0.toDouble()) {
                    "W"
                } else {
                    "E"
                }
            } else {
                if (d < 0.toDouble()) {
                    "S"
                } else {
                    "N"
                }
            }
        } catch (e: Exception) {
            LogUtils.e("dblToLocation error is $e")
        }


        return retS
    }


    // strTime 要转换的String类型的时间
    // formatType 时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    @Throws(ParseException::class)
    fun stringToLong(strTime: String?, formatType: String?): Long {
        val date: Date? = stringToDate(strTime, formatType) // String类型转成date类型
        return date?.let { dateToLong(it) } ?: 0
    }

    // strTime 要转换的string类型的时间，
    // formatType 要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    @Throws(ParseException::class)
    fun stringToDate(strTime: String?, formatType: String?): Date? {
        val formatter = SimpleDateFormat(formatType)
        var date: Date? = null
        date = formatter.parse(strTime)
        return date
    }


    // date 要转换的date类型的时间
    fun dateToLong(date: Date): Long {
        return date.time
    }


    private var deviceSn: String by Preference(Constant.DEVICE_SN, "")


    fun getSNCode(): String? {
        if (deviceSn.isEmpty()) {
            deviceSn = ShellUtils.execCommand(
                "getprop ro.serialno",
                false
            ).successMsg
        }
        return deviceSn
    }


    fun getTimeT016(time: Long): ArrayList<Byte> {
        val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        val str = format.format(Date(time))

        val bytes = ArrayList<Byte>()

        val split = str.split("-")

        for ((index, value) in split.withIndex()) {
            if (index == 0) {
                bytes.add((value.toInt() - 2000).toByte())
            } else {
                bytes.add(value.toInt().toByte())
            }
        }


        return bytes

    }


    /**
     * 1、 获取ActivityThread中保存的所有的ActivityRecord
     * 2、从ActivityRecord中获取状态不是pause的Activity并返回，这个Activity就是当前处于活动状态的Activity
     *
     * @return
     */
    fun getTopActivityInstance(): Activity? {
        var activityThreadClass: Class<*>? = null
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            val activities = activitiesField[activityThread] as Map<*, *>
            for (activityRecord in activities.values) {
                val activityRecordClass: Class<*> = activityRecord!!.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField =
                        activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    return activityField[activityRecord] as Activity
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return null
    }


    fun getTimeByCharArray(charArray: List<Byte>): Long {
        var time = 0L
        try {
            var timeStr = ""
            for ((index, value) in charArray.withIndex()) {
                //      LogUtils.e("char $value")
                if (index == 0) {
                    val i = (value.toInt() + 2000).toString()
                    timeStr += i
                } else {
                    var item = ""
                    item = if (value.toInt().toString().length == 1) {
                        "0" + value.toInt().toString()
                    } else {
                        value.toInt().toString()
                    }
                    timeStr += ":$item"
                }
            }

            LogUtils.e("获取的时间为$timeStr")

            val date: Date? = stringToDate(timeStr, "yyyy:MM:dd:HH:mm") // String类型转成date类型

            time = date?.let { dateToLong(it) } ?: 0
        } catch (e: Exception) {
            LogUtils.e("error is $e")
        }

        return time
    }


    fun formatTime(time: String): String {
        var dateString = ""
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
        dateString = try {
            val date = Date(time.toLong())
            sdf.format(date)
        } catch (e: Exception) {
            sdf.format(Date())

        }
        return dateString
    }


    fun getLoginUserId(): String {
        var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")
        return loginUserId
    }

    fun getLoginUserName():String{
        var loginUserName: String by Preference(Constant.LOGIN_USER_NAME, "")
        return loginUserName
    }

    fun getLoginUserDepartment():String{
        var loginUserPart: String by Preference(Constant.LOGIN_USER_PART, "")
        return loginUserPart
    }

    fun getLoginUserType():String{
        var loginUserType: String by Preference(Constant.LOGIN_USER_TYPE, "")
        return loginUserType
    }



    fun formatTime(time: Long): String {
        var dateString = ""
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
        dateString = try {
            val date = Date(time.toLong())
            sdf.format(date)
        } catch (e: Exception) {
            sdf.format(Date())

        }
        return dateString
    }


    private const val EARTH_RADIUS = 6378.137

    private fun radian(d: Double): Double {
        return d * Math.PI / 180.0
    }

    fun distance(
        firsLongitude: Double, firstLatitude: Double,
        secondLongitude: Double, secondLatitude: Double
    ): Double {
        val firstRadianLongitude = radian(firsLongitude)
        val firstRadianLatitude = radian(firstLatitude)
        val secondRadianLongitude = radian(secondLongitude)
        val secondRadianLatitude = radian(secondLatitude)
        val a = firstRadianLatitude - secondRadianLatitude
        val b = firstRadianLongitude - secondRadianLongitude
        var cal = 2 * asin(
            sqrt(
                sin(a / 2).pow(2.0)
                        + (cos(firstRadianLatitude) * cos(secondRadianLatitude)
                        * sin(b / 2).pow(2.0))
            )
        )
        cal *= EARTH_RADIUS
        return (cal * 10000.0).roundToInt() / 10000.0
    }


    fun distance(firstPoint: String, secondPoint: String): Double {
        val firstArray = firstPoint.split(",").toTypedArray()
        val secondArray = secondPoint.split(",").toTypedArray()
        val firstLatitude = java.lang.Double.valueOf(firstArray[0].trim { it <= ' ' })
        val firstLongitude = java.lang.Double.valueOf(firstArray[1].trim { it <= ' ' })
        val secondLatitude = java.lang.Double.valueOf(secondArray[0].trim { it <= ' ' })
        val secondLongitude = java.lang.Double.valueOf(secondArray[1].trim { it <= ' ' })
        return distance(firstLatitude, firstLongitude, secondLatitude, secondLongitude)
    }

    fun wgs84_To_Gcj02(lat: Double, lon: Double): LocateInfo? {
        val info = LocateInfo()
        if (outOfChina(lat, lon)) {
            info.isChina = false
            info.latitude = lat
            info.longitude = lon
        } else {
            var dLat = transformLat(lon - 105.0, lat - 35.0)
            var dLon = transformLon(lon - 105.0, lat - 35.0)
            val radLat: Double = lat / 180.0 * pi
            var magic = Math.sin(radLat)
            magic = 1 - ee * magic * magic
            val sqrtMagic = Math.sqrt(magic)
            dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
            dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
            val mgLat = lat + dLat
            val mgLon = lon + dLon
            info.isChina = true
            info.latitude = mgLat
            info.longitude = mgLon
        }
        return info
    }

    private fun outOfChina(lat: Double, lon: Double): Boolean {
        if (lon < 72.004 || lon > 137.8347) return true
        return if (lat < 0.8293 || lat > 55.8271) true else false
    }

    private fun transformLat(x: Double, y: Double): Double {
        var ret =
            -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + (0.1
                * Math.sqrt(Math.abs(x)))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0
        return ret
    }


    class LocateInfo {
        var longitude = 0.0
        var latitude = 0.0
        var isChina = false

        constructor() {}
        constructor(longitude: Double, latitude: Double) {
            this.longitude = longitude
            this.latitude = latitude
        }
    }

}