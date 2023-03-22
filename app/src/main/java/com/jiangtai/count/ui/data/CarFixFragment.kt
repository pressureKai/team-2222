package com.jiangtai.count.ui.data

import android.provider.FontsContract.FontRequestCallback
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.ToastUtils
import com.contrarywind.view.WheelView
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.bean.CarFixBean
import com.jiangtai.count.bean.DeviceInfoBean
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_car_fix.*
import org.litepal.LitePal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CarFixFragment: BaseFragment() {
    private var isUpdate = false
    private var typeList = ArrayList<String>()
    private var partFix = ArrayList<String>()
    override fun attachLayoutRes(): Int {
        return R.layout.fragment_car_fix
    }

    override fun initView(view: View) {
        //Y车、HC运输车、物资保障车
        typeList.add("Y车")
        typeList.add("HC运输车")
        typeList.add("物资保障车")
        typeList.add("其他")

        // 机体、发动机及部件、辅助动力装置及部件附件、着陆装置及载部附件
        partFix.add("机体")
        partFix.add("发动机及部件")
        partFix.add("辅助动力装置及部件附件")
        partFix.add("着陆装置及载部附件")
        partFix.add("其他")
        bt_commit.setOnClickListener {
            commit()
        }

        reshowData()
        ll_type.setOnClickListener{
            showPicker(typeList,"装备类别",et_type, callback = {

            })
        }

        et_type.setOnClickListener {
            showPicker(typeList,"装备类别",et_type, callback = {

            })
        }



        ll_part.setOnClickListener {
            showPicker(partFix,"故障部件",et_fix_part, callback = {

            })
        }

        et_fix_part.setOnClickListener {
            showPicker(partFix,"故障部件",et_fix_part, callback = {

            })
        }

        ll_time.setOnClickListener {
            showDate(et_fix_time)
        }

        et_fix_time.setOnClickListener {
            showDate(et_fix_time)
        }
    }

    override fun lazyLoad() {

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
            requireActivity()
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
    private fun commit() {
        if (checkResult()) {
            val sNumber = et_number.text.toString()
            val sType = et_type.text.toString()
            val sName = et_name.text.toString()
            val sFixPart = et_fix_part.text.toString()
            val sFixTime = et_fix_time.text.toString()
            val sPerson = et_person.text.toString()
            val sNotice = et_notice.text.toString()

            var carFixBean : CarFixBean?= null
            if(isUpdate){
                val id = requireActivity().intent.getStringExtra("id")
                if (id != null && id.isNotEmpty()) {
                    val find = LitePal.where(
                        "recordID = ? and loginId = ? ",
                        id.toString(),
                        CommonUtil.getLoginUserId()
                    ).find(
                        CarFixBean::class.java
                    )

                    if (find.size == 0) {
                        ToastUtils.showShort("找不到该记录")
                        requireActivity().finish()
                        return
                    }
                    carFixBean = find.first()
                }
            } else {
                carFixBean = CarFixBean()
            }

            carFixBean?.let {
                carFixBean.wxdx = sNumber
                carFixBean.wxsj = sFixTime
                carFixBean.zblb = sType
                carFixBean.wxpj = sFixPart
                carFixBean.wxry = sPerson
                carFixBean.bz = sNotice
                carFixBean.zbmc = sName

                val find = LitePal.where(
                    "VID = ? and loginId = ? ",
                    sNumber,
                    CommonUtil.getLoginUserId()
                ).find(
                    DeviceInfoBean::class.java
                )

                if (find.size == 0) {
                    ToastUtils.showShort("您还未录入该设备的日常信息")
                }
                //保存维修信息
                carFixBean.save(isUpdate)


                if(isUpdate){
                    ToastUtils.showShort("更新成功")
                    requireActivity().finish()
                } else {
                    ToastUtils.showShort("保存成功")
                    requireActivity().finish()
                }
            }?:let {
                ToastUtils.showShort("找不到该记录")
                requireActivity().finish()
            }
        }
    }

    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sType = et_type.text.toString()
        val sName = et_name.text.toString()
        val sFixType = et_fix_part.text.toString()
        val sFixTime = et_fix_time.text.toString()
        val sPerson = et_person.text.toString()
        val sNotice = et_notice.text.toString()

        val isChecked = sNumber.isNotEmpty() && sType.isNotEmpty()  && sName.isNotEmpty()
                && sFixType.isNotEmpty() && sFixTime.isNotEmpty() && sPerson.isNotEmpty() && sNotice.isNotEmpty()

        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }
            if (!checkAndToast(et_name)) {
                return false
            }
            if (!checkAndToast(et_type)) {
                return false
            }
            if (!checkAndToast(et_fix_part)) {
                return false
            }
            if (!checkAndToast(et_fix_time)) {
                return false
            }
            if (!checkAndToast(et_person)) {
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
        val id = requireActivity().intent.getStringExtra("id")
        if (id != null && id.isNotEmpty()) {
            val find = LitePal.where(
                "recordID = ? and loginId = ? ",
                id.toString(),
                CommonUtil.getLoginUserId()
            ).find(
                CarFixBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                requireActivity().finish()
                return
            }
            val carFixBean = find.first()
            et_number.setText(carFixBean.wxdx)
            et_type.setText(carFixBean.zblb)
            et_name.setText(carFixBean.zbmc)
            et_fix_part.setText(carFixBean.wxpj)
            et_fix_time.setText(carFixBean.wxsj)
            et_person.setText(carFixBean.wxry)
            et_notice.setText(carFixBean.bz)

            bt_commit.text = "更新"
            isUpdate = true
        }
    }


    private fun showPicker(data:ArrayList<String>,title:String,editText: EditText,callback:(s:String)->Unit) {

        //显示选择框
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(requireContext()) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
            callback(data[options1])
            editText.setText(data[options1])
        }.build<String>()


        //当前选中下标
        var currentIndex = 0

        val s = editText.text.toString()
        data.forEachIndexed { index, d ->
            if(d == s){
                currentIndex = index
            }
        }

        pvOptions.setSelectOptions(currentIndex)
        pvOptions.setPicker(data)
        pvOptions.setTitleText(title)
        pvOptions.show()
    }


    private fun getScreenHeightReal(): Int {
        val dm = DisplayMetrics()
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            requireActivity().windowManager.defaultDisplay.getRealMetrics(dm)
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


}