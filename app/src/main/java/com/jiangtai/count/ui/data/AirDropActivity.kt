package com.jiangtai.count.ui.data

import android.view.View
import com.blankj.utilcode.util.ToastUtils
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
import kotlinx.android.synthetic.main.activity_air_drop.bt_commit
import kotlinx.android.synthetic.main.activity_air_drop.et_name
import kotlinx.android.synthetic.main.activity_air_drop.et_notice
import kotlinx.android.synthetic.main.activity_air_drop.et_number
import kotlinx.android.synthetic.main.activity_air_drop.iv_back
import kotlinx.android.synthetic.main.activity_air_drop.iv_delete
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

class AirDropActivity : BaseActivity() {
    private var isUpdate = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_air_drop
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
                val sEtNumber = et_number.text.toString()
                val sEtName = et_name.text.toString()
                val sEtNotice = et_notice.text.toString()
                var airDropInfo: AirDropInfo? = null
                if (isUpdate) {
                    val id = intent.getStringExtra("id")
                    if (id != null && id.isNotEmpty()) {
                        iv_delete.visibility = View.VISIBLE
                        val find = LitePal.where(
                            "recordID = ? and loginId = ? ",
                            id.toString(),
                            CommonUtil.getLoginUserId()
                        ).find(
                            AirDropInfo::class.java
                        )

                        if (find.size == 0) {
                            ToastUtils.showShort("找不到该记录")
                            finish()
                        }
                        airDropInfo = find.first()

                    }
                } else {
                    airDropInfo = AirDropInfo()
                }


                airDropInfo?.let {
                    airDropInfo.ryid = CommonUtil.getLoginUserId()
                    airDropInfo.wzbh = sEtNumber
                    airDropInfo.wzjszt = "已接收"
                    airDropInfo.sjsj = airDropInfo.recordTime
                    airDropInfo.wzmc = sEtName
                    airDropInfo.wzbz = sEtNotice
                    airDropInfo.save(isUpdate)
                    commitToServer(airDropInfo)
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
            if (id != null && id.isNotEmpty()) {
                LitePal.deleteAll(
                    CountRecordBean::class.java,
                    "recordID = ?",
                    id
                )
                LitePal.deleteAll(
                    AirDropInfo::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.AIR_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }

        reshowData()
    }

    private fun checkResult(): Boolean {
        val sEtNumber = et_number.text.toString()
        val sEtName = et_name.text.toString()
        val sEtNotice = et_notice.text.toString()

        val isChecked = sEtNumber.isNotEmpty() && sEtName.isNotEmpty() && sEtNotice.isNotEmpty()
        if (!isChecked) {
            if (sEtNumber.isEmpty()) {
                ToastUtils.showShort("请输入编号")
                return false
            }

            if (sEtName.isEmpty()) {
                ToastUtils.showShort("请输入名称")
                return false
            }

            if (sEtName.isEmpty()) {
                ToastUtils.showShort("请输入名称")
                return false
            }
        } else {
            return true
        }

        return false

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
                AirDropInfo::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }
            val airDropInfo = find.first()
            et_number.setText(airDropInfo.wzbh)
            et_name.setText(airDropInfo.wzmc)
            et_notice.setText(airDropInfo.wzbz)

            bt_commit.text = "更新"
            isUpdate = true
        } else {
            iv_delete.visibility = View.GONE
        }
    }

    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private fun commitToServer(bean: AirDropInfo){
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }

        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(bean))
        val requestCallback = MyRetrofit.instance.api.sendMatRecv(requestBody)

        requestCallback.compose(ThreadSwitchTransformer()).subscribe(object : CallbackListObserver<NewBaseBean<Any>>(){
            override fun onSucceed(t: NewBaseBean<Any>?) {
                ToastUtils.showShort("请求成功")
            }

            override fun onFailed() {

            }

        })
    }
}