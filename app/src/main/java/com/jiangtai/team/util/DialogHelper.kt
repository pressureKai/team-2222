package com.jiangtai.team.util

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.application.App
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.SosBean
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.ui.adapter.DialogPeopleListAdapter
import com.zyyoona7.wheel.WheelView
import org.litepal.LitePal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author xiezekai
 * @des 弹框工具类
 * @create time 2021/8/3 6:52 下午
 */
class DialogHelper {
    private var mOrderListDialog: AlertDialog? = null
    private var mRemindDialog: AlertDialog? = null
    private var mCircleReportTimeSetDialog: AlertDialog? = null
    private var mEleReportSetDialog: AlertDialog? = null
    private var mCancelEleDialog: AlertDialog? = null
    private var mPeopleListDialog: AlertDialog? = null
    private var mSOSDialog: AlertDialog? = null
    private var mChannelAlertDialog: AlertDialog? = null
    private var mClearRemindDialog: AlertDialog? = null
    private var beiDouChannel: Boolean by Preference(Constant.BEI_DOU_CHANNEL, false)

    companion object {
        var instance: DialogHelper? = null
            get() {
                synchronized(DialogHelper::class.java) {
                    if (field == null) {
                        field = DialogHelper()
                    }
                }
                return field
            }
    }


    /**
     * @des 清除弹框提示
     * @time 2021/8/21 1:41 上午
     */
     fun showClearRemindDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        if (mClearRemindDialog != null) {
            val showing = mClearRemindDialog!!.isShowing
            if (!showing) {
                try {
                    mClearRemindDialog?.show()
                } catch (e: Exception) {

                }

            }
        } else {
            mClearRemindDialog =
                initClearDialog(activity, remindDialogClickListener)
            mClearRemindDialog?.show()
        }
    }



    private fun initClearDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_clear, null)


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)




        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "")
            alertDialog.dismiss()
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


    /**
     * @des 隐藏清除弹框
     * @time 2021/8/4 10:16 上午
     */
    fun hintClearDialog() {
        mClearRemindDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mClearRemindDialog = null
                }, 10)
            }
        }
    }


    /**
     * @des 展示通信渠道弹窗
     * @time 2021/8/21 12:36 上午
     */
    fun showChannelDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        if (mChannelAlertDialog != null) {
            val showing = mChannelAlertDialog!!.isShowing
            if (!showing) {
                mChannelAlertDialog?.show()
            }
        } else {
            mChannelAlertDialog =
                initChannelDialog(activity, remindDialogClickListener)
            mChannelAlertDialog?.show()
        }
    }


    private fun initChannelDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_channel, null)


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)


        val beiDou = inflate.findViewById<CheckBox>(R.id.beidou)
        val net = inflate.findViewById<CheckBox>(R.id.net)


        beiDou.isChecked = beiDouChannel
        net.isChecked = !beiDouChannel


        var channel = beiDouChannel

        beiDou.setOnCheckedChangeListener { compoundButton, b ->
            beiDouChannel = b
            beiDou.isChecked = beiDouChannel
            net.isChecked = !beiDouChannel
        }


        net.setOnCheckedChangeListener { compoundButton, b ->
            beiDouChannel = !b
            beiDou.isChecked = beiDouChannel
            net.isChecked = !beiDouChannel
        }


        cancelTextView.setOnClickListener {
            beiDouChannel = channel
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "")
            alertDialog.dismiss()
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

    /**
     * @des 隐藏ChannelDialog
     * @time 2021/8/4 10:16 上午
     */
    fun hintChannelDialog() {
        mChannelAlertDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mChannelAlertDialog = null
                }, 10)
            }
        }
    }

    /**
     * @des 显示SOS
     * @time 2021/8/19 11:31 上午
     */
    fun showSOSDialog(
        activity: FragmentActivity,
        sosBean: SosBean,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        if (mSOSDialog != null) {
            val showing = mSOSDialog!!.isShowing
            if (!showing) {
                mSOSDialog?.show()
            }
        } else {
            mSOSDialog =
                initSOSDialog(activity, sosBean, remindDialogClickListener)
            mSOSDialog?.show()
        }
    }


    /**
     * @des 隐藏SOSDialog
     * @time 2021/8/4 10:16 上午
     */
    fun hintSOSDialog() {
        mSOSDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mSOSDialog = null
                }, 10)
            }
        }
    }

    /**
     * @des 显示取消周期上报弹窗
     * @time 2021/8/4 10:15 上午
     */
    fun showCancelEleReportDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        mCancelEleDialog =
            initCancelEleReportSetDialog(activity, remindDialogClickListener)
        mCancelEleDialog?.show()
    }


    /**
     * @des 隐藏取消周期上报弹窗
     * @time 2021/8/4 10:16 上午
     */
    fun hintCancelEleReportDialog() {
        mCancelEleDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mCancelEleDialog = null
                }, 10)
            }
        }
    }

    /**
     * @des 显示人员列表弹窗
     * @time 2021/8/16 3:55 下午
     */
    fun showPeopleListDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        if (mPeopleListDialog != null) {
            val showing = mPeopleListDialog!!.isShowing
            if (!showing) {
                mPeopleListDialog?.show()
            }
        } else {
            mPeopleListDialog =
                initPeopleListDialog(activity, remindDialogClickListener)
            mPeopleListDialog?.show()
        }
    }


    /**
     * @des 隐藏人员列表弹窗
     * @time 2021/8/4 10:16 上午
     */
    fun hintPeopleListDialog() {
        mPeopleListDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mPeopleListDialog = null
                }, 10)
            }
        }
    }

    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")

    private fun initPeopleListDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_people_list, null)


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val list = inflate.findViewById<RecyclerView>(R.id.list)

        list.layoutManager = LinearLayoutManager(activity)
        val dialogPeopleListAdapter = DialogPeopleListAdapter()
        dialogPeopleListAdapter.setOnItemClickListener { adapter, _, position ->
            try {
                val person = adapter.data[position] as Person
                remindDialogClickListener.onRemindDialogClickListener(true, person.personId)
                alertDialog.dismiss()
            } catch (e: Exception) {

            }
        }
        list.adapter = dialogPeopleListAdapter
        (list.adapter as DialogPeopleListAdapter).setEmptyView(R.layout.empty_view, list)
        if (mCurrentMajorProject.isNotEmpty()) {
            val find = LitePal.where("projectId = ?", mCurrentMajorProject)
                .find(Project::class.java)
            val arrayList = ArrayList<Person>()
            if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
                val peopleId = find.first().peopleId
                val split = peopleId.split(",")
                for (value in split) {
                    val find1 =
                        LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                    arrayList.addAll(find1)
                }
                list.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                list.layoutParams.height = CommonUtil.dp2px(activity, 157f) * find.size
                (list.adapter as DialogPeopleListAdapter).setNewData(arrayList)
            } else {
                (list.adapter as DialogPeopleListAdapter).setNewData(arrayList)
                (list.adapter as DialogPeopleListAdapter).setEmptyView(R.layout.empty_view, list)
            }
            (list.adapter as DialogPeopleListAdapter).notifyDataSetChanged()
        }


        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
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

    /**
     * @des 显示电子围栏半径设置弹窗
     * @time 2021/8/4 10:15 上午
     */
    fun showEleReportSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        mEleReportSetDialog =
            initEleReportSetDialog(activity, remindDialogClickListener)
        mEleReportSetDialog?.show()
    }


    /**
     * @des 隐藏电子围栏半径设置
     * @time 2021/8/4 10:16 上午
     */
    fun hintEleReportSetDialog() {
        mEleReportSetDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mEleReportSetDialog = null
                }, 10)
            }
        }
    }


    /**
     * @des 显示命令弹窗
     * @time 2021/8/4 10:15 上午
     */
    fun showOrderListDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {

        if(mOrderListDialog == null){
            mOrderListDialog =
                initOrderListDialog(activity, remindDialogClickListener)
            mOrderListDialog?.show()
        } else {
            val showing = mOrderListDialog!!.isShowing
            if (!showing) {
                mOrderListDialog?.show()
            }
        }
    }


    /**
     * @des 隐藏命令弹窗
     * @time 2021/8/4 10:16 上午
     */
    fun hintOrderListDialog() {
        mOrderListDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mOrderListDialog = null
                }, 10)
            }
        }
    }


    /**
     * @des 显示周期上报时间设置弹窗
     * @time 2021/8/4 10:16 上午
     */
    fun showCircleReportTimeSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ) {
        if (!circleReportTimeDialogIsShow()) {
            mCircleReportTimeSetDialog =
                initCircleReportTimeSetDialog(activity, remindDialogClickListener)
            mCircleReportTimeSetDialog?.show()
        }
    }

    private fun circleReportTimeDialogIsShow(): Boolean {
        var isShow = false
        mCircleReportTimeSetDialog?.let {
            isShow = it.isShowing
        }
        return isShow
    }


    /**
     * @des 隐藏周期上报时间设置弹窗
     * @time 2021/8/4 10:17 上午
     */
    fun hintCircleReportTimeSetDialog() {
        mCircleReportTimeSetDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mCircleReportTimeSetDialog = null
                }, 10)
            }
        }
    }

    /**
     * @des 显示自定义命令弹窗
     * @time 2021/8/4 10:18 上午
     */
    fun showOrderInputDialog(
        activity: FragmentActivity,
        cancel: String = "",
        confirm: String? = "",
        remindDialogClickListener: RemindDialogClickListener
    ) {

        var cancelString = cancel
        if (cancelString!!.isEmpty()) {
            cancelString = App.getMineContext()!!.resources.getString(R.string.cancel)
        }
        var confirmString = confirm
        if (confirmString!!.isEmpty()) {
            confirmString = App.getMineContext()!!.resources.getString(R.string.confirm)
        }

        mRemindDialog =
            initOrderInputDialog(activity, confirmString!!, cancelString, remindDialogClickListener)
        mRemindDialog?.show()
    }

    /**
     * @des 隐藏自定义命令弹窗
     * @time 2021/8/4 10:18 上午
     */
    fun hintOrderInputDialog() {
        mRemindDialog?.let {
            if (it.isShowing) {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.hide()
                    mRemindDialog = null
                }, 10)
            }
        }
    }


    /**
     * @des 初始化周期上报时间设置
     * @time 2021/8/3 8:52 下午
     */
    private fun initCircleReportTimeSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_circle_report_time, null)


        val wheelView = inflate.findViewById<WheelView<Int>>(R.id.wheelview)


        val arrayList = ArrayList<Int>()
        for (value in 1..30) {
            arrayList.add(value)
        }
        wheelView.data = arrayList
        wheelView.selectedItemPosition = 4


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)
        val checkbox = inflate.findViewById<CheckBox>(R.id.checkbox)
        val inputEdit = inflate.findViewById<EditText>(R.id.input_edit)
        checkbox.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (inputEdit.text.isEmpty()) {
                    checkbox.isChecked = false
                    ToastUtils.showShort("请设置半径")
                }
            }
        }
        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        confirmTextView.setOnClickListener {
            var message = "${wheelView.selectedItemData}"
            if (checkbox.isChecked && inputEdit.text.isNotEmpty()) {
                message += "," + inputEdit.text.toString()
            }
            remindDialogClickListener.onRemindDialogClickListener(true, message)
            alertDialog.dismiss()
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

    /**
     * @des  初始化命令列表弹窗
     * @time 2021/8/3 7:06 下午
     */
    private fun initOrderListDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_order_list, null)
        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val customOrder = inflate.findViewById<TextView>(R.id.custom_order)
        val eatOrder = inflate.findViewById<TextView>(R.id.eat_order)
        val gatherOrder = inflate.findViewById<TextView>(R.id.gather_order)
        val restOrder = inflate.findViewById<TextView>(R.id.rest_order)
        val reportLocation = inflate.findViewById<TextView>(R.id.report_location)


        customOrder.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "0")
            alertDialog.dismiss()
        }


        eatOrder.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "1")
            alertDialog.dismiss()
        }


        gatherOrder.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "2")
            alertDialog.dismiss()
        }


        restOrder.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "3")
            alertDialog.dismiss()
        }

        reportLocation.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "4")
            alertDialog.dismiss()
        }

        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        alertDialog.show()
        alertDialog.setContentView(inflate)
        val window = alertDialog.window

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
        window?.let {
            val lp = window.attributes
            window.attributes = lp
        }

        return alertDialog
    }


    /**
     * @des 初始化电子围栏设置弹窗
     * @time 2021/8/4 10:22 上午
     */
    private fun initEleReportSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_ele_report_set, null)
        val content = inflate.findViewById<EditText>(R.id.input_edit)
        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)


        content.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                KeyBoardUtils.showInput(App.getMineContext()!!, content)
            }
        }

        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, content.text.toString())
        }

        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, content.text.toString())
        }

        alertDialog.show()
        alertDialog.setContentView(inflate)
        val window = alertDialog.window
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //加上下面这一行弹出对话框时软键盘随之弹出
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        window?.setGravity(Gravity.CENTER)
        window?.let {
            val lp = window.attributes
            window.attributes = lp
        }

        return alertDialog
    }


    private fun initSOSDialog(
        activity: FragmentActivity,
        sosBean: SosBean,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_sos, null)


        val latLng = inflate.findViewById<TextView>(R.id.latLng)
        val latLng2 = inflate.findViewById<TextView>(R.id.latLng2)
        val time = inflate.findViewById<TextView>(R.id.time)
        val info = inflate.findViewById<TextView>(R.id.info)



        try {
            val decimalFormat = DecimalFormat("#.000000")

            val latitude = sosBean.lat.toDouble()
            val longitude = sosBean.lng.toDouble()


            var lat = "纬度:"
            var lng = "经度:"
            try {
                lat += decimalFormat.format(latitude)
                lng += decimalFormat.format(longitude)
            } catch (e: Exception) {
                lat = "纬度: 0.00"
                lng = "经度: 0.00"
            }



            latLng.text = lat
            latLng2.text = lng
            sosBean?.let {
                val personInfo = getPersonInfo(sosBean.userId)
                personInfo?.let { person ->
                    info.text = "姓名: ${person.name} 心率: ${it.heartRate}"
                }
            }

        } catch (e: Exception) {
            LogUtils.e("MapFragment format error is $e")
        }

        try {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val date = Date()
            time.text = "时间:" + sdf.format(date)
        } catch (e: Exception) {
            LogUtils.e("MapFragment parse error is $e")
        }


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)

        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "")
            alertDialog.dismiss()
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

    /**
     * @des 取消电子围栏设置
     * @time 2021/8/4 10:22 上午
     */
    private fun initCancelEleReportSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_cancle_circle_report_time, null)


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)

        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, "")
            alertDialog.dismiss()
        }



        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, "")
            alertDialog.dismiss()
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

    /**
     * @des 初始化自定义命令弹框
     * @time 2021/8/3 6:54 下午
     */
    private fun initOrderInputDialog(
        activity: FragmentActivity,
        confirm: String,
        cancel: String,
        remindDialogClickListener: RemindDialogClickListener
    ): AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_input_emergency_message, null)
        val content = inflate.findViewById<EditText>(R.id.input_edit)
        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)


        content.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                KeyBoardUtils.showInput(App.getMineContext()!!, content)
            }
        }

        cancelTextView.text = cancel
        confirmTextView.text = confirm

        cancelTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(false, content.text.toString())
            alertDialog.dismiss()
        }

        confirmTextView.setOnClickListener {
            remindDialogClickListener.onRemindDialogClickListener(true, content.text.toString())
            alertDialog.dismiss()
        }

        alertDialog.show()
        alertDialog.setContentView(inflate)
        val window = alertDialog.window
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //加上下面这一行弹出对话框时软键盘随之弹出
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        window?.setGravity(Gravity.CENTER)
        window?.let {
            val lp = window.attributes
            window.attributes = lp
        }

        return alertDialog
    }


    /**
     * @des 获取个人信息
     * @time 2021/8/19 10:49 下午
     */
    private fun getPersonInfo(id: String): Person? {
        val find = LitePal.where("personId = ?", id).find(Person::class.java)
        return if (find.isNotEmpty()) {
            find.last()
        } else {
            null
        }
    }

    public interface RemindDialogClickListener {
        fun onRemindDialogClickListener(positive: Boolean, message: String)
    }
}