package com.jiangtai.count.ui.data

import android.view.View
import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.bean.HelicopterOilInfoBean
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.activity_helicopter_oil.*
import kotlinx.android.synthetic.main.activity_helicopter_oil.bt_commit
import kotlinx.android.synthetic.main.activity_helicopter_oil.et_notice
import kotlinx.android.synthetic.main.activity_helicopter_oil.et_number
import kotlinx.android.synthetic.main.activity_helicopter_oil.et_time
import kotlinx.android.synthetic.main.activity_helicopter_oil.iv_back
import kotlinx.android.synthetic.main.activity_helicopter_oil.iv_delete
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

class HelicopterOilActivity : BaseActivity() {
    private var isUpdate = false
    override fun attachLayoutRes(): Int {
        return R.layout.activity_helicopter_oil
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
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
        reShowData()
    }


    private fun commit() {
        if (checkResult()) {
            val sNumber = et_number.text.toString()
            val sTime = et_time.text.toString()
            val sOilCover = et_oil_cover.text.toString()
            val sType = et_type.text.toString()
            val sBrandNumber = et_brand_number.text.toString()
            val sPurity = et_oil_purity.text.toString()
            val sOilWater = et_oil_water.text.toString()
            val sNotice = et_notice.text.toString()
            val sOilPerson = et_add_oil_person.text.toString()
            val sOilCaptain = et_oil_captain.text.toString()
            val sOilOther = et_oil_other.text.toString()

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
                helicopterOilInfoBean.jylx = sType
                helicopterOilInfoBean.jyl = sOilCover
                helicopterOilInfoBean.yppph = sBrandNumber
                helicopterOilInfoBean.ypcd = sPurity
                helicopterOilInfoBean.yphsl = sOilWater
                helicopterOilInfoBean.tjj = "无"
                helicopterOilInfoBean.bz = sNotice
                helicopterOilInfoBean.jyy = sOilPerson
                helicopterOilInfoBean.jz = sOilCaptain
                helicopterOilInfoBean.qtry = sOilOther
                helicopterOilInfoBean.save(isUpdate)
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
        val sTime = et_time.text.toString()
        val sOilCover = et_oil_cover.text.toString()
        val sType = et_type.text.toString()
        val sBrandNumber = et_brand_number.text.toString()
        val sPurity = et_oil_purity.text.toString()
        val sOilWater = et_oil_water.text.toString()
        val sNotice = et_notice.text.toString()
        val sOilPerson = et_add_oil_person.text.toString()
        val sOilCaptain = et_oil_captain.text.toString()
        val sOilOther = et_oil_other.text.toString()


        val isChecked = sNumber.isNotEmpty() && sTime.isNotEmpty()
                && sOilCover.isNotEmpty() && sType.isNotEmpty()
                && sBrandNumber.isNotEmpty() && sPurity.isNotEmpty()
                && sOilWater.isNotEmpty() && sNotice.isNotEmpty()
                && sOilPerson.isNotEmpty() && sOilCaptain.isNotEmpty() && sOilOther.isNotEmpty()


        if (!isChecked) {
            if (!checkAndToast(et_number)) {
                return false
            }

            if (!checkAndToast(et_time)) {
                return false
            }
            if (!checkAndToast(et_oil_cover)) {
                return false
            }
            if (!checkAndToast(et_type)) {
                return false
            }
            if (!checkAndToast(et_brand_number)) {
                return false
            }
            if (!checkAndToast(et_oil_purity)) {
                return false
            }
            if (!checkAndToast(et_oil_water)) {
                return false
            }
            if (!checkAndToast(et_notice)) {
                return false
            }
            if (!checkAndToast(et_add_oil_person)) {
                return false
            }
            if (!checkAndToast(et_oil_captain)) {
                return false
            }
            if (!checkAndToast(et_oil_other)) {
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
            et_brand_number.setText(helicopterOilInfoBean.yppph)
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
}