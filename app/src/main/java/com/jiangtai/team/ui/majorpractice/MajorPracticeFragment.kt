package com.jiangtai.team.ui.majorpractice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.bean.*
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.dialog.SelectWayDialog
import com.jiangtai.team.event.ClearEvent
import com.jiangtai.team.login.LoginActivity
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.net.toJsonBody
import com.jiangtai.team.response.TaskDetailModel
import com.jiangtai.team.ui.majorpracticedetail.MajorPracticeDetailActivity
import com.jiangtai.team.util.*
import dismissLoading
import kotlinx.android.synthetic.main.fragment_major_practice.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import showLoading
import java.io.File
import kotlin.collections.ArrayList

/**
 * @author xiezekai
 * @des  专项计划 - 碎片
 * @create time 2021/8/4 2:13 下午
 */
class MajorPracticeFragment : BaseFragment(), View.OnClickListener {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private var path: String? = null
    override fun attachLayoutRes(): Int {
        return R.layout.fragment_major_practice
    }

    override fun initView(view: View) {
        add.setOnClickListener(this)
        requireActivity()?.let {
            list.layoutManager = LinearLayoutManager(it)
            val majorPracticeAdapter =
                MajorPracticeAdapter(object : MajorPracticeAdapter.OnSlideClickListener {
                    override fun onClick(position: Int, type: Int) {
                        if (type == 0) {
                            val intent = Intent(activity, MajorPracticeDetailActivity::class.java)
                            try {
                                val taskId =
                                    ((list.adapter as MajorPracticeAdapter).data[position] as TaskBean).taskId
                                intent.putExtra("taskId", taskId)
                            } catch (e: Exception) {
                                LogUtils.e("MajorPracticeFragment get task id error is $e")
                            }
                            startActivity(intent)
                        } else {
                            try {
                                val taskId =
                                    ((list.adapter as MajorPracticeAdapter).data[position] as TaskBean).taskId
                                ((list.adapter as MajorPracticeAdapter).data[position] as TaskBean).delete()


                                val findAll = LitePal.findAll(Project::class.java)
                                for (project in findAll) {
                                    val projectId = project.projectId
                                    if (project.taskId == taskId) {
                                        project.delete()
                                    }


                                    try {
                                        val find =
                                            LitePal.where("projectId=?", projectId)
                                                .find(LocationReceiver::class.java)

                                        for (value in find) {
                                            value.delete()
                                        }
                                    } catch (e: Exception) {

                                    }

                                }

                                val scores = LitePal.findAll(Score::class.java)
                                for (score in scores) {
                                    if (score.taskId == taskId) {
                                        score.delete()
                                    }
                                }


                                val persons = LitePal.findAll(Person::class.java)
                                for (value in persons) {
                                    if (value.taskId == taskId) {
                                        value.delete()
                                    }

                                    val find =
                                        LitePal.where("personId =?", value.personId.toUpperCase())
                                            .find(SosBean::class.java)
                                    for (value in find) {
                                        value.delete()
                                    }
                                }


                                val realTaskId = if (taskId.contains(".0")) taskId.replace(".0", "")
                                    .trim() else taskId

                                val scoreList =
                                    LitePal.where("taskId=?", realTaskId)
                                        .find(UploadMajorScoreData::class.java)
                                for (value in scoreList) {
                                    value.delete()
                                }

                                val locationList = LitePal.findAll(LocationReceiver::class.java)
                                for (value in locationList) {
                                    if (value.projectId.isEmpty()) {
                                        value.delete()
                                    }
                                }
                            } catch (e: Exception) {
                                LogUtils.e("MajorPracticeFragment", "delete error is $e")
                            }

                            loadData()
                            ToastUtils.showShort("删除成功")
                        }
                    }
                })
            list.adapter = majorPracticeAdapter
            (list.adapter as MajorPracticeAdapter).setEmptyView(R.layout.empty_view, list)
        }
        loadData()
    }

    override fun lazyLoad() {

    }

    companion object {
        fun getInstance(): MajorPracticeFragment = MajorPracticeFragment()
    }


    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.add -> {
                    val dialog = SelectWayDialog(requireContext())
                    dialog.show()
                    dialog.setonConfirmListen(object : SelectWayDialog.OnConfirmListener {
                        override fun onConfirm(way: Int) {
                            if (way == Constant.LOC) {
                                getTaskFromLocal()
                            } else {
                                if (serverIp.isEmpty() || !RegexUtil.isURL(serverIp)) {
                                    ToastUtils.showShort("请先设置正确的服务器IP")
                                } else {
                                    getTaskFromServer()
                                }
                            }
                            dialog.dismiss()
                        }
                    })
                }
            }

        }
    }


    private fun getTaskFromLocal() {
        if (checkPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        ) {
            openFile()
        } else {
            requestPermission(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 0x19
            )
        }
    }


    private fun getTaskFromServer() {
//        ToastUtils.showShort("LoginActivity.users_userid=" + LoginActivity.users_userid + "\n" + "LoginActivity.access_token=" + LoginActivity.access_token)
        requireActivity().showLoading()
        val networkAvailable = NetWork.isNetworkAvailable(mContext);
        if (networkAvailable) {

            val taskList = MyRetrofit.instance.api.getIsTaskList(
                LoginActivity.users_userid,
                LoginActivity.access_token
            )
            taskList.compose(ThreadSwitchTransformer())
                .subscribe(object : CallbackListObserver<IsTaskBean>() {
                    override fun onSucceed(isTaskBean: IsTaskBean) {
                        RunnaUtil.RunnaUtil(mContext, isTaskBean)
                        if (isTaskBean.data.size == 0) {
                            ToastUtils.showShort("当前暂无新数据")
                        } else {
                            val taskBean = TaskBean()

                            try {
//                                taskBean.startTime=CommonUtil.stringToLong(
//                                    isTaskBean.data.first().s_time,"yyyy-MM-dd HH:mm:ss")
//                                taskBean.endTime=CommonUtil.stringToLong(
////                                    isTaskBean.data.first().e_time,"yyyy-MM-dd HH:mm:ss")

                                taskBean.startTime = isTaskBean.data.first().create_time.toLong()
                            } catch (e: Exception) {
                                LogUtils.e(" error is $e")
                            }
                            val planList = ArrayList<ArrayList<IsTaskBean.PlanBean>>()
                            //不同的plan_code 列表
                            val planCodeList = ArrayList<String>()
                            if (isTaskBean.data.isNotEmpty()) {
                                //获取所有不同的plan_code
                                for (value in isTaskBean.data) {
                                    if (planCodeList.isEmpty()) {
                                        planCodeList.add(value.plan_code)
                                    } else {
                                        var codeIsExist = false
                                        for (code in planCodeList) {
                                            if (value.plan_code == code) {
                                                codeIsExist = true
                                                break
                                            }
                                        }
                                        if (!codeIsExist) {
                                            planCodeList.add(value.plan_code)
                                        }
                                    }
                                }

                                for (value in planCodeList) {
                                    val arrayList = ArrayList<IsTaskBean.PlanBean>()
                                    for (planBean in isTaskBean.data) {
                                        if (planBean.plan_code == value) {
                                            arrayList.add(planBean)
                                        }
                                    }
                                    planList.add(arrayList)
                                }
                            }


                            for (plans in planList) {
                                val taskBean = TaskBean()
                                taskBean.taskName = plans.first().plan_name
                                taskBean.taskId = plans.first().plan_code

                                val projectList = ArrayList<Project>()
                                val personList = ArrayList<Person>()
                                for (value in plans) {
                                    val project = Project()
                                    project.projectName = value.task_name
                                    project.projectId = value.task_code
                                    project.taskId = value.plan_code
                                    project.projectContent = value.plan_name
                                    project.peopleId = value.issue_userid
                                    project.startTime = CommonUtil.stringToLong(
                                        value.s_time, "yyyy-MM-dd HH:mm:ss"
                                    )
                                    project.endTime = CommonUtil.stringToLong(
                                        value.e_time, "yyyy-MM-dd HH:mm:ss"
                                    )

                                    projectList.add(project)
                                    project.save()
                                    val issureUser = value.issue_user
                                    val issureUserId = value.issue_userid

                                    if (issureUser.isNotEmpty()) {
                                        val users = issureUser.split(",")
                                        val ids = issureUserId.split(",")
                                        for ((index, user) in users.withIndex()) {


                                            val person = Person()
                                            person.name = user
                                            person.personId = ids[index]
                                            person.taskId = value.plan_code

                                            person.takeDevice = value.task_device
                                            person.deviceListLocal = value.device_code
                                            person.projectId = value.task_code
                                            personList.add(person)
                                            person.save()

                                        }
                                    }

                                }

                                taskBean.projects = projectList


                                taskBean.persons = personList


                                taskBean.save()


//                            taskBean.taskName=isTaskBean.data.first().plan_name
//                            taskBean.taskId=isTaskBean.data.first().plan_code
//
//                                val projectList=ArrayList<Project>()
//                                val personList=ArrayList<Person>()
//                                for (value in isTaskBean.data){
//                                    val project = Project()
//                                    project.projectName=value.task_name
//                                    project.projectId=value.task_code
//                                    project.taskId=value.plan_code
//                                    project.projectContent=value.remark+";任务计划："+value.plan_name
//                                    project.peopleId=value.issue_userid
//                                    project.startTime=CommonUtil.stringToLong(
//                                        value.s_time,"yyyy-MM-dd HH:mm:ss")
//                                    project.endTime=CommonUtil.stringToLong(
//                                        value.e_time,"yyyy-MM-dd HH:mm:ss")
//
//                                    projectList.add(project)
//                                    project.save()
//                                    val issureUser =  value.issue_user
//                                    val issureUserId =  value.issue_userid
//
//                                    if (issureUser.isNotEmpty()){
//                                        val users = issureUser.split(",")
//                                        val usersId = issureUserId.split(",")
//                                        for ((index,user) in users.withIndex()){
//                                            val person = Person()
//                                            person.name = user
//                                            person.personId=usersId[index]
//                                            person.taskId=value.plan_code
//
//                                            person.takeDevice=value.task_device
//                                            person.deviceListLocal=value.device_code
//                                            person.projectId=value.task_code
//                                            personList.add(person)
//                                            person.save()
//                                        }
//                                    }
//
//                                }
//
//                                taskBean.projects=projectList
//                                taskBean.persons=personList
//                                taskBean.save()
                                loadData()
                            }


                        }

                        requireActivity().dismissLoading()
                    }

                    override fun onFailed() {
//                        Log.e("chenjqqq", "=="+"LoginActivity.users_userid=" + LoginActivity.users_userid + "\n" + "LoginActivity.access_token=" + LoginActivity.access_token)

                    }
                })
        } else {
            ToastUtils.showShort("请检查网络")
        }

    }

    private fun getPersonWithTaskId(taskId: String) {
        val map = mapOf<String, Any>("taskId" to taskId)
        val taskDetailCall = MyRetrofit.instance.api.getTaskDetail(toJsonBody(map))
        taskDetailCall.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<TaskDetailModel>() {
                override fun onSucceed(taskDetailModel: TaskDetailModel) {
                    if (taskDetailModel.errorCode == Constant.SUCCESSED_CODE) {
                        val taskPersons = taskDetailModel.data.persons
                        for (taskPerson in taskPersons) {
                            val person = Person(
                                "",
                                "",
                                taskPerson.personNm,
                                taskPerson.personId,
                                "",
                                "",
                                0,
                                Constant.STATE_PRE,
                                taskId,
                                "",
                                ""
                            )
                            person.save()
                        }
                    }

                }

                override fun onFailed() {
                    requireActivity().dismissLoading()
                }
            })

    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            path = if ("file".equals(uri?.scheme, true)) {
                uri?.path
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    FileUtil.getPath(requireContext(), uri!!)
                } else {
                    FileUtil.getRealPathFromURI(uri, requireContext())
                }
            }
            //是表格则处理
            if (path!!.endsWith(".xls") || path!!.endsWith(".xlsx")) {
                requireContext().showLoading()
                Thread {
                    try {
                        decoderXlsFile(path!!)
                    } catch (e: Throwable) {
                        try {
                            requireContext().dismissLoading()
                            ToastUtils.showLong("解析失败,请确认表格格式")
                        } catch (e: java.lang.Exception) {

                        }

                    }
                }.start()

            } else {
                ToastUtils.showShort("新建任务失败,仅支持处理表格文件")
            }
        }
    }


    private fun decoderXlsFile(path: String) {
        val majorType = PoiImport.isMajorType(File(path))
        if (majorType) {
            val task = PoiImport.ExcelReader(File(path))
            val jsonStr = ToJsonUtil.getInstance().toJson(task)
            val taskBean = ToJsonUtil.fromJson(jsonStr, TaskBean::class.java)
            val list = LitePal.where("taskId=?", task.taskId).find(TaskBean::class.java)
            if (list.isNotEmpty()) {
                ToastUtils.showShort("不可重复导入相同任务")
                requireContext().dismissLoading()
                return
            } else {
                taskBean.isMajor = true
                taskBean?.let {
                    //保存任务项目
                    if (taskBean.projects != null) {
                        for (value in taskBean.projects!!) {
                            value.taskId = taskBean.taskId
                            value.save()
                        }
                    }

                    //保存任务人员列表
                    if (taskBean.persons != null) {
                        for (value in taskBean.persons!!) {
                            value.taskId = taskBean.taskId
                            value.isMajor = "1"
                            value.mineSave()
                        }
                    }
                }
                taskBean.save()
            }
            loadData()
        } else {
            requireContext().dismissLoading()
            ToastUtils.showShort("请选择专项任务表格")
        }
        requireContext().dismissLoading()

    }

    //加载数据
    private fun loadData() {
        //0:false,1:true
        //  val find = LitePal.where("isMajor=?", "1").find(TaskBean::class.java)
        val find = LitePal.findAll(TaskBean::class.java)
        var isstarttime: Long = 0
        var issendtime: Long = 0
        for (value in find) {
            val find1 =
                LitePal.where("taskId = ?", value.taskId).find(Project::class.java)
            for (value1 in find1) {
                if (isstarttime >= value1.startTime) {
                    isstarttime
                } else {
                    isstarttime = value1.startTime
                }
                if (issendtime >= value1.endTime) {
                    issendtime
                } else {
                    issendtime = value1.endTime
                }
            }
            value.state = getTaskState(isstarttime, issendtime)
        }
        requireActivity()?.let {
            it.runOnUiThread {
                if (find.size > 0) {
                    (list.adapter as MajorPracticeAdapter).setNewData(find)
                    (list.adapter as MajorPracticeAdapter).notifyDataSetChanged()
                } else {
                    (list.adapter as MajorPracticeAdapter).setNewData(find)
                    (list.adapter as MajorPracticeAdapter).setEmptyView(R.layout.empty_view, list)
                    (list.adapter as MajorPracticeAdapter).notifyDataSetChanged()
                }
            }
        }
    }


    private fun getTaskState(startTime: Long, endTime: Long): Int {
        var state = Constant.STATE_PRE

        val nowTime = System.currentTimeMillis()


        if (nowTime in (startTime + 1) until endTime) {
            // 现在时间处于开始时间以及结束时间之间
            state = Constant.STATE_RUNNING
        } else {
            if (nowTime < startTime) {
                state = Constant.STATE_PRE
            } else if (nowTime > endTime) {
                state = Constant.STATE_FINISHED
            }
        }

        return state
    }


    override fun useEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun clear(e: ClearEvent) {
        loadData()
    }

}