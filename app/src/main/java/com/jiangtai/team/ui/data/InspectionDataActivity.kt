package com.jiangtai.team.ui.data

import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.widget.TextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.ToastUtils
import com.contrarywind.view.WheelView
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import kotlinx.android.synthetic.main.activity_inspection_data.*
import kotlinx.android.synthetic.main.activity_inspection_data.bt_commit
import kotlinx.android.synthetic.main.activity_inspection_data.et_date
import kotlinx.android.synthetic.main.activity_inspection_data.et_device_condition
import kotlinx.android.synthetic.main.activity_inspection_data.et_other
import kotlinx.android.synthetic.main.activity_inspection_data.iv_back
import kotlinx.android.synthetic.main.activity_inspection_data.ll_date_layout
import kotlinx.android.synthetic.main.activity_inspection_data.tv_clear
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InspectionDataActivity: BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_inspection_data
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }
        tv_clear.setOnClickListener {
            clear()
        }
        bt_commit.setOnClickListener {
            commit()
        }

        ll_date_layout.setOnClickListener {
            showDate()
        }
        et_date.setOnClickListener {
            showDate()
        }
    }

    override fun initListener() {

    }

    private fun clear(){
        et_odor.setText("")
        et_device_condition.setText("")
        et_inspector.setText("")
        et_date.setText("")
        et_other.setText("")
    }

    private fun commit(){
        val sOdor = et_odor.text.toString()
        val sDeviceCondition = et_device_condition.text.toString()
        val sInspector = et_inspector.text.toString()
        val sDate = et_date.text.toString()
        val sOther = et_other.text.toString()



        var message = ""
        var isComplete = sOdor.isNotEmpty()

        if(isComplete){
            isComplete = sDeviceCondition.isNotEmpty()
            if(isComplete){
                isComplete = sInspector.isNotEmpty()
                if(isComplete){
                    isComplete = sDate.isNotEmpty()
                    if(!isComplete){
                        message = et_date.hint.toString()
                    }
                } else {
                    message = et_inspector.hint.toString()
                }
            } else {
                message = et_device_condition.hint.toString()
            }
        } else {
            message = et_odor.hint.toString()
        }


        if(isComplete){

        } else {
            ToastUtils.showShort(message)
        }
    }


    /**
     * @des 显示时间选择器
     * @time 2021/10/8 2:05 下午
     */
    private fun showDate() {
        var tempDate : Date =  Date(System.currentTimeMillis())
        val date: Date = Date(System.currentTimeMillis())
        val startCalendar = Calendar.getInstance()
        startCalendar.set(1922,0,0)

        val currentTimeMillis = System.currentTimeMillis()

        val maxDate = Date(currentTimeMillis)
        val endCalendar = Calendar.getInstance()
        endCalendar.time = maxDate

        val instance = Calendar.getInstance()
        instance.time = if(et_date.text.toString().isEmpty()){
            date
        } else {
            string2Date("yyyy-MM-dd",et_date.text.toString())
        }


        var build: TimePickerView? = null
        build = TimePickerBuilder(
            this@InspectionDataActivity
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
                    et_date.setText(format)
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

    private fun getScreenHeightReal(): Int {
        val dm = DisplayMetrics()
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.defaultDisplay.getRealMetrics(dm)
            dm.heightPixels
        } else {
            resources.displayMetrics.heightPixels
        }
    }
}