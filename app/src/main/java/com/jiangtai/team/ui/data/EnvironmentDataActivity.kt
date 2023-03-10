package com.jiangtai.team.ui.data

import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import kotlinx.android.synthetic.main.activity_environment_data.*

class EnvironmentDataActivity: BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_environment_data
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(dark = true)
        iv_back.setOnClickListener {
            finish()
        }
        tv_clear.setOnClickListener {
            clear()
        }
        bt_commit.setOnClickListener {
            commit()
        }
    }

    private fun clear(){
        et_odor.setText("")
        et_voice.setText("")
        et_vibration.setText("")
        et_electromagnetic_interference.setText("")
        et_ray.setText("")
        et_temperature.setText("")
        et_other.setText("")
    }

    private fun commit(){
        val sOdor = et_odor.text.toString()
        val sVoice = et_voice.text.toString()
        val sVibration = et_vibration.text.toString()
        val interference = et_electromagnetic_interference.text.toString()
        val sRay = et_ray.text.toString()
        val sTemperature = et_temperature.text.toString()
        val sOther = et_other.text.toString()

        var message = ""
        var isComplete = sOdor.isNotEmpty()
        if(isComplete){
            isComplete = sVoice.isNotEmpty()
            if(isComplete){
                isComplete = sVibration.isNotEmpty()
                if(isComplete){
                    isComplete = interference.isNotEmpty()
                    if(isComplete){
                        isComplete = sRay.isNotEmpty()
                        if(isComplete){
                            isComplete = sTemperature.isNotEmpty()
                            if(!isComplete){
                                message = et_temperature.hint.toString()
                            }
                        } else {
                            message = et_ray.hint.toString()
                        }
                    } else {
                        message = et_electromagnetic_interference.hint.toString()
                    }
                } else {
                    message = et_vibration.hint.toString()
                }
            } else {
                message = et_voice.hint.toString()
            }
        } else {
            message = et_odor.hint.toString()
        }


        if(isComplete){

        } else {
            ToastUtils.showShort(message)
        }

    }


    override fun initListener() {

    }
}