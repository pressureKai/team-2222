package com.jiangtai.count.ui.login

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.NewLoginBean
import com.jiangtai.count.bean.RequestLoginBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.ui.countMain.CountMainActivity
import com.jiangtai.count.ui.data.NewSettingActivity
import com.jiangtai.count.util.Preference
import com.jiangtai.count.util.RegexUtil
import com.jiangtai.count.util.ToJsonUtil
import kotlinx.android.synthetic.main.activity_new_login.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class NewLoginActivity :BaseActivity() {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private var loginUserName: String by Preference(Constant.LOGIN_USER_NAME, "")
    private var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")
    private var loginUserPart: String by Preference(Constant.LOGIN_USER_PART, "")
    private var loginUserType: String by Preference(Constant.LOGIN_USER_TYPE, "")

    override fun attachLayoutRes(): Int {
        return R.layout.activity_new_login
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        bt_commit.setOnClickListener {
            if(checkResult()){
                login()
            }
        }
        iv_set.setOnClickListener {
            val intent = Intent(this@NewLoginActivity, NewSettingActivity::class.java)
            startActivity(intent)
        }

        if(loginUserPart.isNotEmpty() && loginUserType.isNotEmpty()){
            val intent = Intent(this@NewLoginActivity, CountMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun initListener() {

    }
    private fun login(){
        //请求接口
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            val intent = Intent(this@NewLoginActivity, NewSettingActivity::class.java)
            startActivity(intent)
            return
        }
        val requestLoginBean = RequestLoginBean()
        requestLoginBean.userLoginId = ed_account.text.toString()
        requestLoginBean.password = ed_password.text.toString()
        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                ToJsonUtil.getInstance().toJson(requestLoginBean))
        val loginCallBack = MyRetrofit.instance.api.login(requestBody)
        loginCallBack.compose(ThreadSwitchTransformer()).subscribe(object :
            CallbackListObserver<NewBaseBean<NewLoginBean>>(){
            override fun onSucceed(t: NewBaseBean<NewLoginBean>?) {
                t?.let {
                    loginUserName = t.data.name
                    loginUserId =  t.data.userId
                    loginUserPart = t.data.ssbm
                    loginUserType = t.data.jxlx
                    ToastUtils.showShort("登陆成功")
                    val intent = Intent(this@NewLoginActivity, CountMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailed() {
                loginUserName = ed_account.text.toString()
                loginUserId = ed_account.text.toString()
                loginUserPart = "研发部"
                loginUserType = "管理员"
                ToastUtils.showShort("登录失败以管理员用户登录")
                val intent = Intent(this@NewLoginActivity, CountMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })


        iv_set.setOnClickListener {

        }
    }


    private fun checkResult():Boolean{
        var isChecked = false
        var errorMessage = ""

        val sAccount = ed_account.text.toString()
        val sPassword = ed_password.text.toString()
        isChecked = sAccount.isNotEmpty()
        if(isChecked){
            isChecked = sPassword.isNotEmpty()
            if(!isChecked){
                errorMessage = "请输入密码"
            }
        } else {
            errorMessage = "请输入账号"
        }
        if(!isChecked){
            ToastUtils.showShort(errorMessage)
        }
        return isChecked
    }
}