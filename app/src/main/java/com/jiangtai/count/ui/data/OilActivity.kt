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
import kotlinx.android.synthetic.main.activity_oil.*
import kotlinx.android.synthetic.main.activity_oil.bt_commit
import kotlinx.android.synthetic.main.activity_oil.et_date
import kotlinx.android.synthetic.main.activity_oil.et_notice
import kotlinx.android.synthetic.main.activity_oil.et_number
import kotlinx.android.synthetic.main.activity_oil.iv_back
import kotlinx.android.synthetic.main.activity_oil.iv_delete
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OilActivity : BaseActivity() {
    private var isUpdate = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_oil
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)

        reshowData()

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
                    OilInfoBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.OIL_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }

        ll_date.setOnClickListener {
            showDate(et_date)
        }
        et_date.setOnClickListener {
            showDate(et_date)
        }
    }

    private fun commit(){
        if(checkResult()){
            val sNumber = et_number.text.toString()
            val sName = et_name.text.toString()
            val sDate = et_date.text.toString()
            val sVolumeNumber = et_volume_number.text.toString()
            val sApparentDensity = et_apparent_density.text.toString()
            val sSurplusHeight = et_surplus_height.text.toString()
            val sOilTemperature = et_oil_temperature.text.toString()
            val sNotice = et_notice.text.toString()

            var oilInfoBean : OilInfoBean ?= null
            if(isUpdate){
                val id = intent.getStringExtra("id")
                if(id != null && id.isNotEmpty()){
                    val find = LitePal.where("recordID = ? and loginId = ? ", id.toString(), CommonUtil.getLoginUserId()).find(
                        OilInfoBean::class.java
                    )

                    if (find.size == 0) {
                        ToastUtils.showShort("找不到该记录")
                        finish()
                        return
                    }
                    oilInfoBean =  find.first()
                }
            } else {
                oilInfoBean = OilInfoBean()
            }

            oilInfoBean?.let {
                oilInfoBean.gh = sNumber
                oilInfoBean.ypmc = sName
                oilInfoBean.sjsj = sDate
                oilInfoBean.rjbh = sVolumeNumber
                oilInfoBean.smd = sApparentDensity
                oilInfoBean.yg = sSurplusHeight
                oilInfoBean.yw = sOilTemperature
                oilInfoBean.bz = sNotice
                oilInfoBean.save(isUpdate)
                commitToServer(oilInfoBean)
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


    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sName = et_name.text.toString()
        val sDate = et_date.text.toString()
        val sVolumeNumber = et_volume_number.text.toString()
        val sApparentDensity = et_apparent_density.text.toString()
        val sSurplusHeight = et_surplus_height.text.toString()
        val sOilTemperature = et_oil_temperature.text.toString()
        val sNotice = et_notice.text.toString()



        val isChecked = sNumber.isNotEmpty() && sName.isNotEmpty()
                && sDate.isNotEmpty() && sVolumeNumber.isNotEmpty()
                && sApparentDensity.isNotEmpty() && sSurplusHeight.isNotEmpty()
                && sOilTemperature.isNotEmpty() && sNotice.isNotEmpty()


        if (!isChecked) {
             if(!checkAndToast(et_number)){
                 return false
             }
             if(!checkAndToast(et_name)){
                 return false
             }
             if(!checkAndToast(et_date)){
                 return false
             }
             if(!checkAndToast(et_volume_number)){
                 return false
             }
             if(!checkAndToast(et_apparent_density)){
                 return false
             }
             if(!checkAndToast(et_surplus_height)){
                 return false
             }
             if(!checkAndToast(et_oil_temperature)){
                 return false
             }
            if(!checkAndToast(et_notice)){
                 return false
             }
        }
        return isChecked
    }

    override fun initListener() {

    }



    private fun checkAndToast(et:EditText):Boolean{
        return if(et.text.toString().isEmpty()){
            ToastUtils.showShort(et.hint)
            false
        } else {
            true
        }
    }


    private fun reshowData(){
        val id = intent.getStringExtra("id")
        if(id != null && id.isNotEmpty()){
            iv_delete.visibility = View.VISIBLE
            val find = LitePal.where("recordID = ? and loginId = ? ", id.toString(), CommonUtil.getLoginUserId()).find(
                OilInfoBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }

            val first = find.first()
            et_number.setText(first.gh)
            et_name.setText(first.ypmc)
            et_date.setText(first.sjsj)
            et_volume_number.setText(first.rjbh)
            et_apparent_density.setText(first.smd)
            et_surplus_height.setText(first.yg)
            et_oil_temperature.setText(first.yw)
            et_notice.setText(first.bz)

            bt_commit.text = "更新"
            isUpdate = true
        } else {
            iv_delete.visibility = View.GONE
        }

    }

    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private fun commitToServer(oilInfoBean: OilInfoBean){
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }


        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(oilInfoBean))
        val requestCallback = MyRetrofit.instance.api.sendOilDepot(requestBody)

        requestCallback.compose(ThreadSwitchTransformer()).subscribe(object : CallbackListObserver<NewBaseBean<Any>>(){
            override fun onSucceed(t: NewBaseBean<Any>?) {
                ToastUtils.showShort("请求成功")
            }

            override fun onFailed() {

            }

        })
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
            this@OilActivity
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

}