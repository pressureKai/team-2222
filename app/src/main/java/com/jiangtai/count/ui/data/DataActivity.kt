package com.jiangtai.count.ui.data

import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.ToastUtils
import com.contrarywind.view.WheelView
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import kotlinx.android.synthetic.main.activity_data.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DataActivity: BaseActivity() {

    override fun attachLayoutRes(): Int {
        return R.layout.activity_data
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

        ll_date_layout.setOnClickListener {
            showDate()
        }
        et_date.setOnClickListener {
            showDate()
        }

        ll_oil.setOnClickListener {
            showOilPicker()
        }
        et_oil_variety.setOnClickListener {
            showOilPicker()
        }
        bt_commit.setOnClickListener {
            commit()
        }
    }
    private fun commit(){
        val sDate = et_date.text.toString()
        val sNumber = et_voucher_number.text.toString()
        val sUnit = et_unit.text.toString()
        val sOilUnit = et_oil_unit.text.toString()
        val sOilVariety = et_oil_variety.text.toString()
        val sOilMan = et_oilman.text.toString()
        val sAdopter = et_adopter.text.toString()
        val sSpecialOdor = et_special_odor.text.toString()
        val sDeviceCondition = et_device_condition.text.toString()
        val sOther = et_other.text.toString()

        var message = ""
        var isComplete = sDate.isNotEmpty()
        if(isComplete){
            isComplete = sNumber.isNotEmpty()
            if(isComplete){
                isComplete = sUnit.isNotEmpty()
                if(isComplete){
                    isComplete = sOilUnit.isNotEmpty()
                    if(isComplete){
                        isComplete = sOilVariety.isNotEmpty()
                        if(isComplete){
                            isComplete = sOilMan.isNotEmpty()
                            if(isComplete){
                                isComplete = sAdopter.isNotEmpty()
                                if(isComplete){
                                    isComplete = sSpecialOdor.isNotEmpty()
                                    if(isComplete){
                                        isComplete = sDeviceCondition.isNotEmpty()
                                        if(!isComplete){
                                            message = et_device_condition.hint.toString()
                                        }
                                    } else {
                                        message = et_special_odor.hint.toString()
                                    }
                                } else {
                                    message = et_adopter.hint.toString()
                                }
                            } else {
                                message = et_oilman.hint.toString()
                            }
                        } else {
                            message = et_oil_variety.hint.toString()
                        }
                    } else {
                        message = et_oil_unit.hint.toString()
                    }
                } else {
                    message = et_unit.hint.toString()
                }
            } else {
                message = et_voucher_number.hint.toString()
            }
        } else {
            message = et_date.hint.toString()
        }


        if(isComplete){

        } else {
            ToastUtils.showShort(message)
        }
    }

    override fun initListener() {

    }

    private fun clear(){
        et_date.setText("")
        et_voucher_number.setText("")
        et_unit.setText("")
        et_oil_unit.setText("")
        et_oil_variety.setText("")
        et_oilman.setText("")
        et_special_odor.setText("")
        et_adopter.setText("")
        et_device_condition.setText("")
        et_other.setText("")
    }


    /**
     * @des 显示时间选择器
     * @time 2021/10/8 2:05 下午
     */
    private fun showDate() {
        var tempDate :Date =  Date(System.currentTimeMillis())
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
            this@DataActivity
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


    private fun showOilPicker() {
        //初始化数据
        val firstList = ArrayList<String>()


        firstList.add("品种一")
        firstList.add("品种二")
        firstList.add("品种三")
        firstList.add("品种四")
        firstList.add("品种五")
        firstList.add("品种六")
        firstList.add("品种七")
        //显示选择框
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(this) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
            et_oil_variety.setText(firstList[options1])
        }.build<String>()


        var currentIndex = 0
        if(et_oil_variety.text.toString().isNotEmpty()){
            firstList.forEachIndexed { index, s ->
                if( s == et_oil_variety.text.toString()){
                    currentIndex = index
                }
            }

        }



        pvOptions.setSelectOptions(currentIndex)
        pvOptions.setPicker(firstList)
        pvOptions.setTitleText("油料品种")
        pvOptions.show()
    }
}