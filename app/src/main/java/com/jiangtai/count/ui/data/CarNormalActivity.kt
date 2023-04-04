package com.jiangtai.count.ui.data

import android.view.View
import android.widget.EditText
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.util.*
import kotlinx.android.synthetic.main.activity_car_normal.*
import kotlinx.android.synthetic.main.activity_car_normal.bt_commit
import kotlinx.android.synthetic.main.activity_car_normal.et_notice
import kotlinx.android.synthetic.main.activity_car_normal.et_number
import kotlinx.android.synthetic.main.activity_car_normal.et_type
import kotlinx.android.synthetic.main.activity_car_normal.iv_back
import kotlinx.android.synthetic.main.activity_car_normal.iv_delete
import kotlinx.android.synthetic.main.activity_car_normal.ll_type
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

class CarNormalActivity : BaseActivity() {
    private var isStandBy = false
    private var isUpdate = false
    private var typeList = ArrayList<String>()
    override fun attachLayoutRes(): Int {
        return R.layout.activity_car_normal
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)

        //Y车、HC运输车、物资保障车
        typeList.add("Y车")
        typeList.add("HC运输车")
        typeList.add("物资保障车")
        typeList.add("其他")

        iv_back.setOnClickListener {
            finish()
        }

        bt_commit.setOnClickListener {
            commit()
        }
        tv_standby.setOnClickListener {
            isStandBy = true
            tv_standby.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            tv_execute.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
        }

        tv_execute.setOnClickListener {
            isStandBy = false
            tv_execute.setBackgroundResource(R.drawable.shape_white_round_stroke_blue)
            tv_standby.setBackgroundResource(R.drawable.shape_white_round_stroke_gray)
        }



        iv_delete.setOnClickListener {
            val id = intent.getStringExtra("id")
            if (id != null && id.isNotEmpty()) {
                LitePal.deleteAll(
                    CountRecordBean::class.java,
                    "recordID = ?",
                    id
                )
                LitePal.deleteAll(
                    DeviceInfoBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.DEVICE_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }

        ll_type.setOnClickListener{
            showPicker(typeList,"装备类别",et_type, callback = {

            })
        }

        et_type.setOnClickListener {
            showPicker(typeList,"装备类别",et_type, callback = {

            })
        }
        reshowData()
    }


    private fun commit() {
        if (checkResult()) {
            val sNumber = et_number.text.toString()
            val sType = et_type.text.toString()
            //剩余油量
            val sOil = et_oil.text.toString()
            //累计摩托小时
            val sHour = et_hour.text.toString()
            //备注
            val sNotice = et_notice.text.toString()

            var deviceInfoBean: DeviceInfoBean? = null
            if (isUpdate) {
                val id = intent.getStringExtra("id")
                if (id != null && id.isNotEmpty()) {
                    iv_delete.visibility = View.VISIBLE
                    val find = LitePal.where(
                        "recordID = ? and loginId = ? ",
                        id.toString(),
                        CommonUtil.getLoginUserId()
                    ).find(
                        DeviceInfoBean::class.java
                    )

                    if (find.size == 0) {
                        ToastUtils.showShort("找不到该记录")
                        finish()
                        return
                    }

                    deviceInfoBean = find.first()
                }
            } else {
                deviceInfoBean = DeviceInfoBean()
            }

            deviceInfoBean?.let {
                deviceInfoBean.vid = sNumber
                deviceInfoBean.zblb = sType
                deviceInfoBean.mtxs = sHour
                deviceInfoBean.yyl = sOil
                deviceInfoBean.bz = sNotice
                val find = LitePal.where(
                    "WXDX = ? and loginId = ? ",
                    sNumber,
                    CommonUtil.getLoginUserId()
                ).find(
                    CarFixBean::class.java
                )
                if (find.size == 0) {
                    deviceInfoBean.gzbj = "正常"
                } else {
                    deviceInfoBean.gzbj = "损坏"
                    GsonInstance.instance?.gson?.let {
                        val toJson = it.toJson(find.first())
                        deviceInfoBean.zbwxxx = toJson
                    }
                }

                deviceInfoBean.rwzt = if(isStandBy) "待命" else "在执行"
                deviceInfoBean.save(isUpdate)
                commitToServer(deviceInfoBean)
                if (isUpdate) {
                    ToastUtils.showShort("更新成功")
                    finish()
                } else {
                    ToastUtils.showShort("保存成功")
                    finish()
                }
            } ?: let {
                ToastUtils.showShort("找不到该记录")
                finish()
            }
        }
    }

    private fun checkResult(): Boolean {
        val sNumber = et_number.text.toString()
        val sType = et_type.text.toString()
        //剩余油量
        val sOil = et_oil.text.toString()
        //累计摩托小时
        val sHour = et_hour.text.toString()
        //备注
        val sNotice = et_notice.text.toString()

        val isChecked = sNumber.isNotEmpty()
                && sType.isNotEmpty() && sOil.isNotEmpty()
                && sHour.isNotEmpty() && sNotice.isNotEmpty()

        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }

            if (!checkAndToast(et_type)) {
                return false
            }

            if (!checkAndToast(et_oil)) {
                return false
            }

            if (!checkAndToast(et_hour)) {
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


    private fun reshowData() {
        val id = intent.getStringExtra("id")
        if (id != null && id.isNotEmpty()) {
            iv_delete.visibility = View.VISIBLE
            val find = LitePal.where(
                "recordID = ? and loginId = ? ",
                id.toString(),
                CommonUtil.getLoginUserId()
            ).find(
                DeviceInfoBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }

            val deviceInfoBean = find.first()
            et_number.setText(deviceInfoBean.vid)
            et_type.setText(deviceInfoBean.zblb)
            //剩余油量
            et_oil.setText(deviceInfoBean.yyl)
            //累计摩托小时
            et_hour.setText(deviceInfoBean.mtxs)
            //备注
            et_notice.setText(deviceInfoBean.bz)


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
    private fun commitToServer(bean: DeviceInfoBean) {
        if (serverIp.isEmpty() || !RegexUtil.isURL(serverIp)) {
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }

        val requestBody =
            RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(bean)
            )
        val requestCallback = MyRetrofit.instance.api.sendVehInfo(requestBody)

        requestCallback.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<Any>>() {
                override fun onSucceed(t: NewBaseBean<Any>?) {
                    ToastUtils.showShort("请求成功")
                }

                override fun onFailed() {

                }

            })
    }


    private fun showPicker(data:ArrayList<String>,title:String,editText: EditText,callback:(s:String)->Unit) {

        //显示选择框
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(this) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
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


}