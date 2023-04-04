package com.jiangtai.count.ui.data

import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.ToastUtils
import com.contrarywind.view.WheelView
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.util.CommonUtil
import com.jiangtai.count.util.Preference
import com.jiangtai.count.util.RegexUtil
import com.jiangtai.count.util.ToJsonUtil
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.activity_device.bt_commit
import kotlinx.android.synthetic.main.activity_device.et_notice
import kotlinx.android.synthetic.main.activity_device.et_number
import kotlinx.android.synthetic.main.activity_device.iv_back
import kotlinx.android.synthetic.main.activity_device.iv_delete
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DeviceActivity : BaseActivity() {
    private var smellMessage = "正常"
    private var deviceType = "DY库"
    private var isUpdate = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_device
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }

        bt_commit.setOnClickListener {
            if (checkResult()) {

                val sNumber = et_number.text.toString()
                val sTime = et_time.text.toString()
                val sTemperature = et_temperature.text.toString()
                val sNotice = et_notice.text.toString()

                var deviceFeelInfoBean : DeviceFeelInfoBean ?= null
                if(isUpdate){
                    val id = intent.getStringExtra("id")
                    if (id != null && id.isNotEmpty()) {
                        iv_delete.visibility = View.VISIBLE
                        val find = LitePal.where(
                            "recordID = ? and loginId = ? ",
                            id.toString(),
                            CommonUtil.getLoginUserId()
                        ).find(
                            DeviceFeelInfoBean::class.java
                        )

                        if (find.size == 0) {
                            ToastUtils.showShort("找不到该记录")
                            finish()
                        }
                        deviceFeelInfoBean = find.first()
                    }
                } else {
                    deviceFeelInfoBean = DeviceFeelInfoBean()
                }

                var toString = ""
                string2Date(sTime,"yyyy-MM-dd")?.let {
                    toString =  string2Date(sTime,"yyyy-MM-dd")!!.time.toString()
                }
                deviceFeelInfoBean?.let {

                    deviceFeelInfoBean.ssbh = sNumber
                    deviceFeelInfoBean.qwxx = smellMessage
                    deviceFeelInfoBean.sslb = deviceType
                    deviceFeelInfoBean.wd = sTemperature
                    deviceFeelInfoBean.sjsj = toString
                    deviceFeelInfoBean.bz = sNotice


                    deviceFeelInfoBean.save(isUpdate)
                    commitToServer(deviceFeelInfoBean)
                    if(isUpdate){
                        ToastUtils.showShort("更新成功")
                        finish()
                    } else {
                        ToastUtils.showShort("保存成功")
                        finish()
                    }
                }?:let {
                    ToastUtils.showShort("找不到该记录")
                    finish()
                }

            }
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
                    DeviceFeelInfoBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.DEVICE_FILL_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }


        tv_normal.setOnClickListener {
            smellMessage = "正常"

            tv_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            tv_un_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
        }

        tv_un_normal.setOnClickListener {
            smellMessage = "异常"
            tv_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
            tv_un_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
        }


        et_time.setOnClickListener {
            showDate(et_time)
        }
        reShowData()
    }
    private fun getScreenHeightReal(): Int {
        val dm = DisplayMetrics()
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.defaultDisplay.getRealMetrics(dm)
            dm.heightPixels
        } else {
            resources.displayMetrics.heightPixels
        }
    }

    private fun string2Date(format: String?, datess: String?): Date? {
        val sdr = SimpleDateFormat(format)
        var date: Date? = null
        try {
            date = sdr.parse(datess)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }
    /**
     * @des 显示时间选择器
     * @time 2021/10/8 2:05 下午
     */
    private fun showDate(editText: EditText) {
        var tempDate : Date =  Date(System.currentTimeMillis())
        val date: Date = Date(System.currentTimeMillis())
        val startCalendar = Calendar.getInstance()
        startCalendar.set(1922,0,0)

        val currentTimeMillis = System.currentTimeMillis()

        val maxDate = Date(currentTimeMillis)
        val endCalendar = Calendar.getInstance()
        endCalendar.time = maxDate

        val instance = Calendar.getInstance()
        instance.time = if(editText.text.toString().isEmpty()){
            date
        } else {
            string2Date("yyyy-MM-dd",editText.text.toString())
        }


        var build: TimePickerView? = null
        build = TimePickerBuilder(
            this
        ) { _, _ ->

        }.setLayoutRes(R.layout.dialog_time_picker) { v ->
            v?.let {
                val dialogLayout = v.findViewById<ConstraintLayout>(R.id.dialog_layout)
                val layoutParams = dialogLayout.layoutParams
                layoutParams.height = (getScreenHeightReal() / 5) * 2
                dialogLayout.layoutParams = layoutParams


                it.findViewById<TextView>(R.id.cancel).setOnClickListener {
                    build?.dismiss()
                }

                it.findViewById<TextView>(R.id.confirm).setOnClickListener {
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val format = simpleDateFormat.format(tempDate)
                    editText.setText(format)
                    build?.dismiss()
                }
            }
        }
            .setDate(instance)
            .setDividerType(WheelView.DividerType.WRAP)
            .setRangDate(startCalendar, endCalendar)
            .setTextColorCenter(resources.getColor(R.color.black))
            .setDividerColor(resources.getColor(R.color.black))
            .setTextColorOut(resources.getColor(R.color.black))
            .setBgColor(resources.getColor(android.R.color.transparent))
            .setLineSpacingMultiplier(3f)
            .setLabel(
                "", "", "",
                "", "", ""
            )
            .setOutSideCancelable(true)
            .setItemVisibleCount(3)
            .isAlphaGradient(true)
            .setTimeSelectChangeListener {
                tempDate = it
            }
            .build()
        build.show()
    }

    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sTemperature = et_temperature.text.toString()
        val sTime = et_time.text.toString()
        val isChecked =
            sNumber.isNotEmpty()  && sTemperature.isNotEmpty() && sTime.isNotEmpty()


        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }
            if (!checkAndToast(et_temperature)) {
                return false
            }
            if (!checkAndToast(et_time)) {
                return false
            }
        }

        return isChecked
    }

    override fun initListener() {

    }

    private fun reShowData() {
        val id = intent.getStringExtra("id")
        if (id != null && id.isNotEmpty()) {
            iv_delete.visibility = View.VISIBLE
            val find = LitePal.where(
                "recordID = ? and loginId = ? ",
                id.toString(),
                CommonUtil.getLoginUserId()
            ).find(
                DeviceFeelInfoBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }
            val deviceFeelInfoBean = find.first()
            et_number.setText(deviceFeelInfoBean.ssbh)
            et_time.setText(deviceFeelInfoBean.sjsj)
            et_temperature.setText(deviceFeelInfoBean.wd)
            et_notice.setText(deviceFeelInfoBean.bz)

            if(deviceFeelInfoBean.qwxx == "异常"){
                tv_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
                tv_un_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            } else {
                tv_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
                tv_un_normal.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
            }
            bt_commit.text = "更新"
            isUpdate = true
        } else {
            iv_delete.visibility = View.GONE
        }
    }

    private fun checkAndToast(et: EditText): Boolean {
        return if (et.text.toString().isEmpty()) {
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }


    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private fun commitToServer(bean: DeviceFeelInfoBean){
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }


        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(bean))
        val requestCallback = MyRetrofit.instance.api.sendFaceInfo(requestBody)

        requestCallback.compose(ThreadSwitchTransformer()).subscribe(object : CallbackListObserver<NewBaseBean<Any>>(){
            override fun onSucceed(t: NewBaseBean<Any>?) {
                ToastUtils.showShort("请求成功")
            }
            override fun onFailed() {

            }
        })
    }
}