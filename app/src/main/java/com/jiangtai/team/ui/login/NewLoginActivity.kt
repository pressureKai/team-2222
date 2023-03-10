package com.jiangtai.team.ui.login

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.base.BaseNoDataBean
import com.jiangtai.team.base.NewBaseBean
import com.jiangtai.team.bean.NewLoginBean
import com.jiangtai.team.bean.RequestLoginBean
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.ui.countMain.CountMainActivity
import com.jiangtai.team.util.Preference
import com.jiangtai.team.util.RegexUtil
import com.jiangtai.team.util.ToJsonUtil
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
        bt_commit.setOnClickListener {
            if(checkResult()){
                login()
            }
        }
    }

    override fun initListener() {

    }
    private fun login(){
        serverIp = "https://127.0.0.1:8080/"
        //请求接口
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
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

            }

            override fun onFailed() {
                loginUserName = ed_account.text.toString()
                loginUserId = ed_account.text.toString()
                loginUserPart = "研发部"
                loginUserType = "管理员"
                val intent = Intent(this@NewLoginActivity, CountMainActivity::class.java)
                startActivity(intent)

                finish()
            }
        })
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