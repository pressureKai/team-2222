import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jiangtai.team.dialog.LoadingDialog
import java.text.SimpleDateFormat
import java.util.*

/**
 * 扩展函数
 */

fun Fragment.showToast(content: String) {
    Toast.makeText(this.activity?.applicationContext, content, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

/**
 * @des
 * @time 2021/8/3 11:23 下午
 */
fun Context.showLoading() {
    android.os.Handler(Looper.getMainLooper()).post {
        LoadingDialog.show(this)
    }
}

fun Context.dismissLoading() {
    //change from xiezekai
    try {
        android.os.Handler(Looper.getMainLooper()).post {
            LoadingDialog.dismiss()
        }

    } catch (e: Exception) {
        Log.e("Ext", "dismissDialog error is $e")
    }
}

/**
 * 格式化当前日期
 */
fun formatCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date())
}

/**
 * String 转 Calendar
 */
fun String.stringToCalendar(): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date = sdf.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

fun Activity.getScreenWidth(): Int {
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

fun Activity.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * @des 测量View的宽高 IntArray[0] 宽，IntArray[1] 高。
 * @time 2021/8/4 8:43 上午
 */
fun View.measureView(): IntArray {
    var lp: ViewGroup.LayoutParams = layoutParams
    if (lp == null) {
        lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
    val lpHeight = lp.height
    val heightSpec: Int = if (lpHeight > 0) {
        View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
    } else {
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    }
    measure(widthSpec, heightSpec)
    return intArrayOf(measuredWidth, measuredHeight)
}