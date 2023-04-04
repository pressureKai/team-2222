package com.jiangtai.count.ui.data

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
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.bean.HelicopterOilInfoBean
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.util.CommonUtil
import com.jiangtai.count.util.ToJsonUtil
import kotlinx.android.synthetic.main.activity_helicopter_oil.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HelicopterOilActivity : BaseActivity() {
    private var isUpdate = false
    private var additive = false

    private var oilTypeList :ArrayList<String> = ArrayList()
    override fun attachLayoutRes(): Int {
        return R.layout.activity_helicopter_oil
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)

        oilTypeList.add("汽油")
        oilTypeList.add("柴油")
        oilTypeList.add("航空煤油")
        oilTypeList.add("重质燃料油")
        oilTypeList.add("汽油机润滑油")
        oilTypeList.add("齿轮油")
        oilTypeList.add("压缩机油")
        oilTypeList.add("仪表油")
        oilTypeList.add("军械防锈油")
        oilTypeList.add("军用汽车通用润滑脂")
        oilTypeList.add("2号坦克润滑脂")
        oilTypeList.add("03/H多效锂基润滑脂")
        oilTypeList.add("冷却液")
        oilTypeList.add("制动液")
        oilTypeList.add("防冰液")
        oilTypeList.add("其他")

        iv_back.setOnClickListener {
            finish()
        }

        bt_commit.setOnClickListener {
            commit()
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
                    HelicopterOilInfoBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.HELICOPTER_OIL_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }



        tv_none.setOnClickListener {
            additive = false
            tv_none.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            tv_have.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)

        }

        tv_have.setOnClickListener {
            additive = true
            tv_have.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            tv_none.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
        }
        et_type.setOnClickListener {
            showPicker(oilTypeList,"y料类型",et_type) {

            }
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

    private fun commit() {
        if (checkResult()) {
            val sNumber = et_number.text.toString()
            val sTime = et_time.text.toString()
            val sOilCover = et_oil_cover.text.toString()
            val sBrandNumber = et_brand_number.text.toString()
            val sNotice = et_notice.text.toString()
            val sOilCoverS = et_oil_cover_s.text.toString()

            var helicopterOilInfoBean : HelicopterOilInfoBean ?= null
            if(isUpdate){
                val id = intent.getStringExtra("id")
                if (id != null && id.isNotEmpty()) {
                    iv_delete.visibility = View.VISIBLE
                    val find = LitePal.where(
                        "recordID = ? and loginId = ? ",
                        id.toString(),
                        CommonUtil.getLoginUserId()
                    ).find(
                        HelicopterOilInfoBean::class.java
                    )

                    if (find.size == 0) {
                        ToastUtils.showShort("找不到该记录")
                        finish()
                        return
                    }
                     helicopterOilInfoBean = find.first()
                }
            }else{
                helicopterOilInfoBean = HelicopterOilInfoBean()
            }

            helicopterOilInfoBean?.let {
                helicopterOilInfoBean.ygch = sNumber
                helicopterOilInfoBean.jysj = sTime
                helicopterOilInfoBean.jyl = sOilCover
                helicopterOilInfoBean.syyl = sOilCoverS

                helicopterOilInfoBean.ypmc = sBrandNumber
                helicopterOilInfoBean.tjj = if(additive) "有" else "无"
                helicopterOilInfoBean.bz = sNotice
                helicopterOilInfoBean.save(isUpdate)



                if(isUpdate){
                    ToastUtils.showShort("更新成功")
                    finish()
                } else {
                    ToastUtils.showShort("保存成功")
                    finish()
                }


                sendDataToServer(helicopterOilInfoBean)

            }?:let {
                ToastUtils.showShort("找不到该记录")
                finish()
            }
        }
    }

    private fun sendDataToServer(helicopterOilInfoBean :HelicopterOilInfoBean){
        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(helicopterOilInfoBean))
        val requestCallback = MyRetrofit.instance.api.sendHelicopterData(requestBody)

        requestCallback.compose(ThreadSwitchTransformer()).subscribe(object : CallbackListObserver<NewBaseBean<Any>>(){
            override fun onSucceed(t: NewBaseBean<Any>?) {
                ToastUtils.showShort("请求成功")
            }

            override fun onFailed() {

            }

        })
    }

    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sTime = et_time.text.toString()
        val sOilCover = et_oil_cover.text.toString()
        val sBrandNumber = et_brand_number.text.toString()
        val sNotice = et_notice.text.toString()
        val sOilCoverS = et_oil_cover_s.text.toString()
        val isChecked = sNumber.isNotEmpty() && sTime.isNotEmpty()
                && sOilCover.isNotEmpty()
                && sBrandNumber.isNotEmpty()  && sNotice.isNotEmpty()  && sOilCoverS.isNotEmpty()
        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }

            if (!checkAndToast(et_time)) {
                return false
            }
            if (!checkAndToast(et_oil_cover_s)) {
                return false
            }
            if (!checkAndToast(et_oil_cover)) {
                return false
            }
            if (!checkAndToast(et_brand_number)) {
                return false
            }
            if (!checkAndToast(et_notice)) {
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
                HelicopterOilInfoBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }
            val helicopterOilInfoBean = find.first()

            et_number.setText(helicopterOilInfoBean.ygch)
            et_time.setText(helicopterOilInfoBean.jysj)
            et_oil_cover.setText(helicopterOilInfoBean.jyl)
            et_type.setText(helicopterOilInfoBean.jylx)
            et_brand_number.setText(helicopterOilInfoBean.ypmc)
            et_oil_purity.setText(helicopterOilInfoBean.ypcd)
            et_oil_water.setText(helicopterOilInfoBean.yphsl)
            et_notice.setText(helicopterOilInfoBean.bz)
            et_add_oil_person.setText(helicopterOilInfoBean.jyy)
            et_oil_captain.setText(helicopterOilInfoBean.jz)
            et_oil_other.setText(helicopterOilInfoBean.qtry)

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


    private fun showPicker(data:ArrayList<String>,title:String,editText: EditText,callback:(s:String)->Unit) {

        //显示选择框
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(this) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
            callback(data[options1])
            editText.setText(data[options1])
        }.build()


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
}