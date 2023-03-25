package com.jiangtai.count.ui.countMain

import android.content.Intent
import android.support.v4.widget.DrawerLayout.DrawerListener
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.ui.data.*
import com.jiangtai.count.ui.main.MainActivity
import com.jiangtai.count.util.Preference
import kotlinx.android.synthetic.main.activity_count_main.*
import kotlinx.android.synthetic.main.activity_count_main.drawerLayout
import kotlinx.android.synthetic.main.activity_count_main.iv_open
import kotlinx.android.synthetic.main.activity_count_main.ll_clear
import kotlinx.android.synthetic.main.activity_count_main.ll_data
import kotlinx.android.synthetic.main.activity_count_main.ll_home
import kotlinx.android.synthetic.main.activity_count_main.ll_quit
import kotlinx.android.synthetic.main.activity_count_main.tv_user_department
import kotlinx.android.synthetic.main.activity_count_main.tv_user_name


class CountMainActivity : BaseActivity() {
    private var loginUserName: String by Preference(Constant.LOGIN_USER_NAME, "")
    private var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")
    private var loginUserPart: String by Preference(Constant.LOGIN_USER_PART, "")
    private var loginUserType: String by Preference(Constant.LOGIN_USER_TYPE, "")

    override fun attachLayoutRes(): Int {
        return R.layout.activity_count_main
    }

    override fun initData() {

    }

    override fun initView() {
        tv_user_name.text = loginUserName
        tv_user_department.text = "$loginUserPart $loginUserType"
        //获取DrawerLayout布局
        drawerLayout.setDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }
            override fun onDrawerStateChanged(newState: Int) {}
        })


        ll_oil_normal.setOnClickListener {
            val intent = Intent(this@CountMainActivity, OilActivity::class.java)
            startActivity(intent)
        }

        ll_oil_helicopter.setOnClickListener {
            val intent = Intent(this@CountMainActivity, HelicopterOilActivity::class.java)
            startActivity(intent)
        }


        ll_car_normal.setOnClickListener {
            val intent = Intent(this@CountMainActivity, CarNormalActivity::class.java)
            startActivity(intent)
        }

        ll_car_fix.setOnClickListener {
            val intent = Intent(this@CountMainActivity, CarFixActivity::class.java)
            startActivity(intent)
        }


        ll_airdrop.setOnClickListener {
            val intent = Intent(this@CountMainActivity, CountMapActivity::class.java)
            startActivity(intent)
        }

        ll_device.setOnClickListener {
            val intent = Intent(this@CountMainActivity, DeviceActivity::class.java)
            startActivity(intent)
        }




        ll_weather.setOnClickListener {
            val intent = Intent(this@CountMainActivity, WeatherActivity::class.java)
            startActivity(intent)
        }


        iv_open.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        ll_quit.setOnClickListener {
            loginUserPart = ""
            loginUserType = ""
            finish()
        }


        ll_data.setOnClickListener {
            val intent = Intent(this@CountMainActivity, DataListActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawer(Gravity.START)
        }


        ll_clear.setOnClickListener {
            ToastUtils.showShort("清除成功")
        }


        ll_home.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.START)
        }


        ll_feel_layout.setOnClickListener {
           startActivity(Intent(this@CountMainActivity,FeelListActivity::class.java))
        }

        ll_test.setOnClickListener {
            startActivity(Intent(this@CountMainActivity,ManagerTestActivity::class.java))
        }
    }

    override fun initListener() {

    }

}