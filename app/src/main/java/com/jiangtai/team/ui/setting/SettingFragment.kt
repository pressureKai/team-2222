package com.jiangtai.team.ui.setting

import android.app.AlertDialog
import android.app.CPManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.amap.api.maps.offlinemap.OfflineMapActivity
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.dialog.ConfirmDialog
import com.jiangtai.team.dialog.SaveDialog
import com.jiangtai.team.event.ClearEvent
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.ui.beidoureceiver.BeiDouReceiverHistoryActivity
import com.jiangtai.team.ui.beidoureceiver.BeiDouSendHistoryActivity
import com.jiangtai.team.ui.data.DataActivity
import com.jiangtai.team.ui.data.EnvironmentDataActivity
import com.jiangtai.team.ui.data.InspectionDataActivity
import com.jiangtai.team.ui.receiver.UploadDataService
import com.jiangtai.team.util.DialogHelper
import com.jiangtai.team.util.Preference
import com.zyyoona7.wheel.WheelView
import kotlinx.android.synthetic.main.fragment_setting.*
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

/**
 * Created by heCunCun on 2021/3/10
 */
class SettingFragment : BaseFragment() {
    private var eleMode: Boolean by Preference(Constant.ELE_MODE, false)
    private var eleDistance: Int by Preference(Constant.ELE_DISTANCE, 0)
    private var eleTime: Int by Preference(Constant.ELE_TIME, 0)
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private var phoneIp: String by Preference(Constant.PHONE_IP, "")
    private var currentTaskId: String by Preference(Constant.CURRENT_TASK_ID, "")
    override fun attachLayoutRes(): Int = R.layout.fragment_setting
    private var cp: CPManager?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersionBar(dark = true)
    }
    override fun initView(view: View) {
        try {
            cp = context?.getSystemService("cpmanager") as CPManager
        }catch (e:java.lang.Exception){

        }

        iv_save.setOnClickListener {
          val pid = et_phone_id.text.toString().trim()
            if (pid.isNotEmpty() && pid.toInt()>=0 && pid.toInt()<=99) {
                phoneId = et_phone_id.text.toString().trim()
            }else{
                ToastUtils.showShort("ID 范围0-99,请重新设置")
                et_phone_id.setText(phoneId)
                return@setOnClickListener
            }
            val currentIP = et_serve_ip.text.toString().trim()
            if (currentIP != serverIp){
                serverIp = currentIP
                MyRetrofit.resetInstance()
            }

            phoneIp =(et_phone_ip.text.toString().trim())
            SaveDialog(requireContext()).show()
        }
        tv_del_task.setOnClickListener {
            //结束考核
            val finishDialog = ConfirmDialog(
                requireContext(), "清空所有任务",
                "即将清空所有任务列表及数据，如您已经不需要任何任务数据,请点击完成。", "完成"
            )
            finishDialog.show()
            finishDialog.setonConfirmListen(object : ConfirmDialog.OnConfirmListener {
                override fun onConfirm() {
                    LitePal.deleteDatabase("training")
                    currentTaskId=""
                    ToastUtils.showShort("清空成功")
                    EventBus.getDefault().post(ClearEvent())
                    LitePal.getDatabase()
                    finishDialog.dismiss()

                    eleMode = false
                    eleDistance = 0
                    eleTime = 0
                }

            })

        }

        history.setOnClickListener {
            requireActivity()?.let {
                val intent = Intent(it, BeiDouReceiverHistoryActivity::class.java)
                startActivity(intent)
            }
        }
        send_history.setOnClickListener {
            requireActivity()?.let {
                val intent = Intent(it, BeiDouSendHistoryActivity::class.java)
                startActivity(intent)
            }
        }

        offline_map_download.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity().applicationContext,
                    OfflineMapActivity::class.java
                )
            )
        }


        setting_1.setOnClickListener {
            //频点
            mContext?.let {
                initSignInSetDialog(it as FragmentActivity,object : DialogHelper.RemindDialogClickListener{
                    override fun onRemindDialogClickListener(positive: Boolean, message: String) {
                        try {
                            val toInt = message.toInt()
                            mContext?.let {
                                //    CommandUtils.setPingdian(it,toInt)
                                cp?.let {
                                    val byteArrayOf = byteArrayOf(
                                        0x00.toByte(),
                                        0x04.toByte(),
                                        0x10.toByte(),
                                        toInt.toByte()
                                    )
                                    val sendConfigData =
                                        it.sendConfigData(0x25.toByte(), byteArrayOf)
                                    sendConfigData
                                    //  ToastUtils.showShort("改变频点 :${sendConfigData}")
                                }
                            }
                        }catch (e:Exception){

                        }
                    }
                })
            }

        }


        setting_2.setOnClickListener {
            //功率
            mContext?.let {
                initSignInSetDialog(it as FragmentActivity,object : DialogHelper.RemindDialogClickListener{
                    override fun onRemindDialogClickListener(positive: Boolean, message: String) {
                        try {
                            val toInt = message.toInt()
                            mContext?.let {
                                //  CommandUtils.setPower(it,toInt)
                                cp?.let {
                                    val byteArrayOf = byteArrayOf(
                                        0x00.toByte(),
                                        0x04.toByte(),
                                        0x11.toByte(),
                                        toInt.toByte()
                                    )
                                    val sendConfigData =
                                        it.sendConfigData(0x25.toByte(), byteArrayOf)
                                    //  ToastUtils.showShort("改变功率 :${sendConfigData}")
                                }
                            }
                        }catch (e:Exception){

                        }
                    }
                },true)
            }
        }

        tv_data_upload.setOnClickListener {
            val intent = Intent(requireContext(), DataActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(),"数据上传",Toast.LENGTH_SHORT).show()
        }
        tv_environment_upload.setOnClickListener{
            val intent = Intent(requireContext(), EnvironmentDataActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(),"数据上传",Toast.LENGTH_SHORT).show()
        }
        tv_inspection_upload.setOnClickListener {
            val intent = Intent(requireContext(), InspectionDataActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(),"数据上传",Toast.LENGTH_SHORT).show()
        }
        tv_start.setOnClickListener {
            UploadDataService.stopUploadDataService()
            UploadDataService.startUploadDataService()
        }

        tv_stop.setOnClickListener {
            UploadDataService.stopUploadDataService()
        }
    }

    override fun lazyLoad() {
        et_phone_id.setText(phoneId)
        et_serve_ip.setText(serverIp)
        et_phone_ip.setText(phoneIp)
    }

    companion object {
        fun getInstance(): SettingFragment = SettingFragment()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        initImmersionBar(dark = false)
    }


    private fun initSignInSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: DialogHelper.RemindDialogClickListener,
        power:Boolean ?= false
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_config_set, null)
        val wheelView = inflate.findViewById<WheelView<Int>>(R.id.wheelview)
        val arrayList = ArrayList<Int>()
        if(!power!!){
            for (value in 0..10) {
                arrayList.add(value)
            }
        } else {
            for (value in 0..4) {
                arrayList.add(value)
            }
        }


        wheelView.data = arrayList



        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)

        val title = inflate.findViewById<TextView>(R.id.title)
        val des = inflate.findViewById<TextView>(R.id.des)

        if(power!!){
            title.text = "功率设置"
            des.text = "功率"
        } else {
            title.text = "频点设置"
            des.text = "频点"
        }


        cancelTextView.setOnClickListener {
            alertDialog.dismiss()
            remindDialogClickListener.onRemindDialogClickListener(false, "")
        }



        confirmTextView.setOnClickListener {
            val message = "${wheelView.selectedItemData}"
            alertDialog.dismiss()
            remindDialogClickListener.onRemindDialogClickListener(true, message)
        }

        alertDialog.show()
        alertDialog.setContentView(inflate)
        val window = alertDialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window?.setGravity(Gravity.CENTER)
        window?.let {
            val lp = window.attributes
            window.attributes = lp
        }

        return alertDialog
    }
}