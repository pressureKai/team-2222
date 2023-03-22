package com.jiangtai.count.ui.signIn

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.bean.Person
import com.jiangtai.count.bean.Project
import com.jiangtai.count.bean.TaskBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.ui.receiver.SignInEntity
import com.jiangtai.count.ui.receiver.SignInService
import com.jiangtai.count.util.DialogHelper
import com.jiangtai.count.util.Preference
import com.kongqw.radarscanviewlibrary.RadarScanView
import com.zyyoona7.wheel.WheelView
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal

class SignInFragment : BaseFragment() {
    private var scanRunning = false
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")

    private var signTime: Long by Preference(Constant.SIGN_TIME, 10000L)

    //0:全部1:分组2:任务
    private var signType: Int by Preference(Constant.SIGN_TYPE, 2)

    private val signInEntityList: ArrayList<SignInEntity> = ArrayList()
    private var objctlist: ArrayList<String> = ArrayList()


    private var taskTextView: TextView? = null
    private val personList = ArrayList<Person>()
    override fun useEventBus(): Boolean {
        return true
    }

    override fun attachLayoutRes(): Int {
        return R.layout.fragment_sign_in
    }

    private var is_url: String? = null
    private var selectdd: String? = null
    private var seletect: String? = null
    private var spname: String? = null


    override fun initView(view: View) {


        Handler(Looper.getMainLooper()).post {
            radarScanView.stopScan()
        }

        start.setOnClickListener {

            if (SignlnFragmentUtil.projectstringid == null) {
                ToastUtils.showShort("请点击设置按钮设置签到信息")
            } else {
                val sp = SignlnFragmentUtil.getsp(mContext)
                //  = sp.getString("is_url","")
                is_url = "http://192.168.1.13:5080/user/people_insert4"
                if (is_url.equals("")) {
                    ToastUtils.showShort("请点击设置按钮设置签到信息")
                } else {
//                selectdd =  sp.getString("selectdd","")
//                seletect =  sp.getString("seletect","")
//                spname =  sp.getString("spname","")
                    selectdd = "食堂"
                    seletect = "8"
                    spname = sp.getString("spname", "")
                    scanRunning = true
                    radarScanView.startScan()
                    mContext?.let {
                        SignInService.startCurrentThread(it)
                    }
                    start.visibility = View.GONE
                    signInEntityList.clear()
                    if (SignlnFragmentUtil.personList1 != null) {
                        SignlnFragmentUtil.personList1.clear()
                    }
                    if (personList != null) {
                        personList.clear()
                    }
                    if (SignlnFragmentUtil.ydkpersonList != null) {
                        SignlnFragmentUtil.ydkpersonList.clear()
                    } else {
                        SignlnFragmentUtil.oncreatArralist();

                    }
                    if (SignlnFragmentUtil.wdkpersonList != null) {
                        SignlnFragmentUtil.wdkpersonList.clear()
                    } else {
                        SignlnFragmentUtil.oncreatArralist();
                    }
                    objctlist.clear()

                    val find = LitePal.where("projectId = ?", SignlnFragmentUtil.projectstringid)
                        .find(Project::class.java)
                    if (find.isNotEmpty()) {
                        val last = find.last()
                        val peopleId = last.peopleId
                        if (peopleId.isNotEmpty()) {
                            val split = peopleId.split(",")
                            for (id in split) {
                                val find = LitePal.where("personId =?", id.toUpperCase())
                                    .find(Person::class.java)
                                if (find.size > 0) {
                                    personList.add(find.last())
                                }
                            }

                        }

                    }

                }
            }


        }
        radarScanView_layout.setOnClickListener {
            if (scanRunning) {
                scanRunning = false
                start.visibility = View.VISIBLE
                radarScanView.stopScan()
                SignInService.stopCurrentThread()
            }
        }
        xiangqing.setOnClickListener {
            if (SignlnFragmentUtil.projectstringid == null) {
                ToastUtils.showShort("请选择任务");
            } else {
//                  val personList = ArrayList<Person>()
//            val find =
//                LitePal.where("projectId = ?", SignlnFragmentUtil.projectstringid).find(Project::class.java)
//            if (find.isNotEmpty()) {
//                val last = find.last()
//                val peopleId = last.peopleId
//                if (peopleId.isNotEmpty()) {
//                    val split = peopleId.split(",")
//                    for (id in split) {
//                        val find = LitePal.where("personId =?", id.toUpperCase())
//                            .find(Person::class.java)
//                        if (find.size > 0) {
//                            personList.add(find.last())
//                        }
//                    }
                //  val  list = LitePal.where("personId=?", find.get(0).personId).find(Person::class.java)
                SignlnFragmentUtil.setDetails(mContext, personList)
                // }

                //     }
            }


        }
        iv_setting.setOnClickListener {
            initSignInSetDialog(mContext as FragmentActivity,
                object : DialogHelper.RemindDialogClickListener {
                    override fun onRemindDialogClickListener(positive: Boolean, message: String) {
                        try {
                            val split = message.split(",")
                            signTime = split[0].toInt() * 1000L
                            signType = split[1].toInt()
                        } catch (e: Exception) {

                        }
                    }

                })
        }
        settingsis.setOnClickListener {
//            val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
//
//                val  list = LitePal.where("taskId=?", find.get(0).taskId).find(Person::class.java)
//
            SignlnFragmentUtil.SignInIent(mContext)
        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!scanRunning) {
            start.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).post {
                radarScanView.stopScan()
            }
        } else {
            start.visibility = View.GONE
            Handler(Looper.getMainLooper()).post {
                radarScanView.startScan()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!scanRunning) {
            start.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).post {
                radarScanView.stopScan()
            }
        } else {
            start.visibility = View.GONE
            Handler(Looper.getMainLooper()).post {
                radarScanView.startScan()
            }
        }


        Handler(Looper.getMainLooper()).post {
            try {
                taskTextView?.text = getCurrentTaskName()
            } catch (e: Exception) {

            }
        }
    }


    override fun lazyLoad() {

    }

    companion object {
        fun getInstance(): SignInFragment = SignInFragment()
    }


    override fun onDestroy() {
        SignInService.stopCurrentThread()
        try {
            radarScanView.stopScan()
        } catch (e: Exception) {

        }

        super.onDestroy()
    }


    /**
     * @des 初始化考勤打卡设置
     * @time 2021/8/3 8:52 下午
     */
    private fun initSignInSetDialog(
        activity: FragmentActivity,
        remindDialogClickListener: DialogHelper.RemindDialogClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(activity, R.layout.dialog_sign_in, null)


        val wheelView = inflate.findViewById<WheelView<Int>>(R.id.wheelview)


        val arrayList = ArrayList<Int>()
        for (value in 1..60) {
            arrayList.add(value)
        }


        wheelView.data = arrayList

        if (signTime != 0L) {
            val l = signTime / 1000
            wheelView.selectedItemPosition = (l - 1).toInt()
        } else {
            wheelView.selectedItemPosition = 9
        }


        val cancelTextView = inflate.findViewById<TextView>(R.id.cancel)
        val confirmTextView = inflate.findViewById<TextView>(R.id.confirm)
        val xzjh = inflate.findViewById<Spinner>(R.id.xzjh)
        val find = LitePal.findAll(TaskBean::class.java)
        if (find.size > 0) {
            SignlnFragmentUtil.SpinnerUtil(context, find, xzjh)
        } else {

        }


//        val taskName = inflate.findViewById<TextView>(R.id.task_name)
//        taskName.text = getCurrentTaskName()
//
//        taskName.setOnClickListener {
//            mContext?.let {
//                val intent = Intent(it, MajorTaskActivity::class.java)
//                mContext?.startActivity(intent)
//            }
//        }
//
//        taskTextView = taskName

//        when (signType) {
//            0 -> {
//                checkboxAll.isChecked = true
//            }
//            1 -> {
//                checkboxGroup.isChecked = true
//            }
//            2 -> {
//                checkboxTask.isChecked = true
//            }
//        }
//
//
//        checkBoxAllLayout.setOnClickListener {
//            checkboxAll.isChecked = !checkboxAll.isChecked
//            if (checkboxAll.isChecked) {
//                checkboxGroup.isChecked = !checkboxAll.isChecked
//                checkboxTask.isChecked = !checkboxAll.isChecked
//            }
//        }
//
//        checkBoxGroupLayout.setOnClickListener {
//            checkboxGroup.isChecked = !checkboxGroup.isChecked
//            if (checkboxGroup.isChecked) {
//                checkboxAll.isChecked = !checkboxGroup.isChecked
//                checkboxTask.isChecked = !checkboxGroup.isChecked
//            }
//        }
//
//        checkBoxTaskLayout.setOnClickListener {
//            checkboxTask.isChecked = !checkboxTask.isChecked
//            if (checkboxTask.isChecked) {
//                checkboxGroup.isChecked = !checkboxTask.isChecked
//                checkboxAll.isChecked = !checkboxTask.isChecked
//            }
//        }

        cancelTextView.setOnClickListener {
            alertDialog.dismiss()
            remindDialogClickListener.onRemindDialogClickListener(false, "")
        }



        confirmTextView.setOnClickListener {
            if (SignlnFragmentUtil.projectstringid.isNotEmpty()) {
                SignlnFragmentUtil.projectstringid

            }
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

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun refreshTaskState(e: SignInEntity) {
        EventBus.getDefault().removeStickyEvent(e)

        if (signInEntityList.isEmpty()) {
            signInEntityList.add(e)
        } else {
            var isRepeat = false
            for (value in signInEntityList) {
                if (value.personId == e.personId) {
                    isRepeat = true
                }
            }

            if (!isRepeat) {
                signInEntityList.add(e)
            }
        }




        signInEntityList.get(signInEntityList.size - 1).personId
        Log.e("chenjqttttttt", signInEntityList.get(signInEntityList.size - 1).personId)
        signInEntityList.get(signInEntityList.size - 1).taskId





        if (objctlist.size == 0) {
            Handler(Looper.getMainLooper()).post {
                count.text = personList.size.toString()
                count1.text = signInEntityList.size.toString()
                var a = personList.size - signInEntityList.size;
                if (a <= 0) {
                    count2.text = "0"
                } else {
                    count2.text = a.toString()
                }
                RadarScanView.addpeople(true)

            }

            SignlnFragmentUtil.people_insert(
                signInEntityList.get(signInEntityList.size - 1).personId,
                is_url,
                selectdd,
                seletect,
                personList
            )
            objctlist.add(signInEntityList.get(signInEntityList.size - 1).personId)
        } else {
            if (signInEntityList.size != objctlist.size) {
                for (i in objctlist) {
                    if (!signInEntityList.get(signInEntityList.size - 1).personId.equals(objctlist)) {
                        Handler(Looper.getMainLooper()).post {
                            count.text = personList.size.toString()
                            count1.text = signInEntityList.size.toString()
                            var a = personList.size - signInEntityList.size;
                            count2.text = a.toString()
                            RadarScanView.addpeople(true)
                        }
                        SignlnFragmentUtil.people_insert(
                            signInEntityList.get(signInEntityList.size - 1).personId,
                            is_url,
                            selectdd,
                            seletect,
                            personList
                        )
                        objctlist.add(signInEntityList.get(signInEntityList.size - 1).personId)
                    }
                }
            }
        }

    }


    private fun getCurrentTaskName(): String {
        var taskId = ""
        val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
        if (find.size > 0) {
            taskId = find.first().projectName
        }
        return taskId
    }

    override fun onPause() {
        super.onPause()
        start.visibility = View.VISIBLE

        try {
            Handler(Looper.getMainLooper()).post {
                radarScanView.stopScan()
            }
        } catch (e: Exception) {

        }
    }

}