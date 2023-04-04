package com.jiangtai.count.ui.data

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.CarFixBean
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.activity_car_fix.*
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal


class CarFixActivity : BaseActivity() {
    private var isUpdate = false
    private val fragments = ArrayList<Fragment>()
    val nameList = arrayOf(
        "信息录入",
        "信息读取"
    )
    override fun attachLayoutRes(): Int {
        return R.layout.activity_car_fix
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }



        nameList.forEach {
            if(it == "信息录入"){
                fragments.add(CarFixFragment())
            } else {
                fragments.add(CarReadFragment())
            }

        }
        vp.adapter = MyPagerAdapter(supportFragmentManager)
        vp.offscreenPageLimit = fragments.size
        vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {



            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        tabLayout.setViewPager(vp,nameList,this,fragments)


        iv_delete.setOnClickListener {
            val id = intent.getStringExtra("id")
            if(id != null && id.isNotEmpty()){
                LitePal.deleteAll(
                    CountRecordBean::class.java,
                    "recordID = ?",
                    id
                )
                LitePal.deleteAll(
                    CarFixBean::class.java,
                    "recordID = ?",
                    id
                )
                ToastUtils.showShort("删除成功")
                val deleteBean = DeleteBean()
                deleteBean.beDeleteID = id
                deleteBean.beDeleteType = CountRecordBean.CAR_FIX_TYPE
                EventBus.getDefault().postSticky(deleteBean)
                finish()
            } else {
                ToastUtils.showShort("找不到该记录!")
            }
        }
        reshowData()
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
                CarFixBean::class.java
            )

            if (find.size == 0) {
                ToastUtils.showShort("找不到该记录")
                finish()
                return
            }
            isUpdate = true
        } else {
            iv_delete.visibility = View.GONE
        }
    }



    inner class MyPagerAdapter(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

    }

    private fun showPicker(data:ArrayList<String>,title:String) {

        //显示选择框
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(this) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置

        }.build<String>()


        //当前选中下标
        var currentIndex = 0


        pvOptions.setSelectOptions(currentIndex)
        pvOptions.setPicker(data)
        pvOptions.setTitleText(title)
        pvOptions.show()
    }



}