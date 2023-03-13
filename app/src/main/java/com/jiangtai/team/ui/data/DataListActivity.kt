package com.jiangtai.team.ui.data

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.Gravity
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.ui.countMain.CountMainActivity
import com.jiangtai.team.util.Preference
import kotlinx.android.synthetic.main.activity_data_list.*
import kotlinx.android.synthetic.main.activity_data_list.drawerLayout
import kotlinx.android.synthetic.main.activity_data_list.iv_open
import kotlinx.android.synthetic.main.activity_data_list.ll_clear
import kotlinx.android.synthetic.main.activity_data_list.ll_data
import kotlinx.android.synthetic.main.activity_data_list.ll_home
import kotlinx.android.synthetic.main.activity_data_list.ll_quit
import kotlinx.android.synthetic.main.activity_data_list.tv_user_department
import kotlinx.android.synthetic.main.activity_data_list.tv_user_name
import kotlin.system.exitProcess

class DataListActivity :BaseActivity(){
    private var loginUserName: String by Preference(Constant.LOGIN_USER_NAME, "")
    private var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")
    private var loginUserPart: String by Preference(Constant.LOGIN_USER_PART, "")
    private var loginUserType: String by Preference(Constant.LOGIN_USER_TYPE, "")
    private val fragments = ArrayList<Fragment>()

    val nameList = arrayOf(
        "车辆日常",
        "车辆维修",
        "设施监控",
        "YL日清",
        "气象信息",
        "直升机加油",
        "空投物质采集"
    )


    val typeList = arrayOf(
        CountRecordBean.DEVICE_TYPE,
        CountRecordBean.CAR_FIX_TYPE,
        CountRecordBean.DEVICE_FILL_TYPE,
        CountRecordBean.OIL_TYPE,
        CountRecordBean.WEATHER_TYPE,
        CountRecordBean.HELICOPTER_OIL_TYPE,
        CountRecordBean.AIR_TYPE
    )


    override fun attachLayoutRes(): Int {
        return R.layout.activity_data_list
    }

    override fun initData() {

    }

    override fun initView() {
        tv_user_name.text = loginUserName
        tv_user_department.text = "$loginUserPart $loginUserType"
        iv_open.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        ll_quit.setOnClickListener {
            finish()
            exitProcess(0)
        }


        ll_data.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.START)
        }


        ll_clear.setOnClickListener {
            ToastUtils.showShort("清除成功")
        }


        ll_home.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.START)
            val intent = Intent(this@DataListActivity, CountMainActivity::class.java)
            startActivity(intent)
        }



        typeList.forEach {
            fragments.add(DataFragment(it))
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




    }

    override fun initListener() {



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
}