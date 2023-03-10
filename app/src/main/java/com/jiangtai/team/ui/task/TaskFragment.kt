package com.jiangtai.team.ui.task

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.bean.*
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.dialog.ConfirmDialog
import com.jiangtai.team.dialog.SelectWayDialog
import com.jiangtai.team.event.ClearEvent
import com.jiangtai.team.event.RefreshTaskState
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.net.toJsonBody
import com.jiangtai.team.response.TaskDetailModel
import com.jiangtai.team.response.TaskListModel
import com.jiangtai.team.ui.taskinfo.TaskInfoActivity
import com.jiangtai.team.util.FileUtil
import com.jiangtai.team.util.Preference
import com.jiangtai.team.util.RegexUtil
import com.jiangtai.team.util.ToJsonUtil
import com.jt.poi.PoiImport
import com.orhanobut.logger.Logger
import dismissLoading
import kotlinx.android.synthetic.main.activity_all_task.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import showLoading
import java.io.File

/**
 * Created by heCunCun on 2021/3/9
 */
class TaskFragment : BaseFragment() {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var currentTaskId: String by Preference(Constant.CURRENT_TASK_ID, "")
    override fun useEventBus(): Boolean = true

    var mRecyclerView :RecyclerView ?= null
    private var list = mutableListOf<TaskBean>()

    private val taskAdapter by lazy {
        TaskAllAdapter(object : TaskAllAdapter.OnSlideClickListener {
            override fun onClick(position: Int, type: Int) {
                var isContinue = true
                if (type == 0) {
                    if (phoneId.isEmpty()) {
                        ToastUtils.showShort("请先设置手持机ID")
                        isContinue = false
                        return
                    }
                    if (isContinue) {
                        if (currentTaskId.isEmpty()) {//无当前任务可进
                            val intent = Intent(requireContext(), TaskInfoActivity::class.java)
                            intent.putExtra("taskId", list[position].taskId)
                            startActivity(intent)
                        } else {
                            if (currentTaskId == list[position].taskId) {
                                //只当前任务可进
                                val intent = Intent(requireContext(), TaskInfoActivity::class.java)
                                intent.putExtra("taskId", list[position].taskId)
                                startActivity(intent)
                            } else {
                                ToastUtils.showShort("请先结束当前正在进行的任务")
                            }
                        }
                    }

                } else {
                    if(position < list.size){
                        val message = if(list[position].state == Constant.STATE_RUNNING){
                            "${list[position].taskName} 任务正在进行中，是否删除"
                        } else if(list[position].state == Constant.STATE_FINISHED){
                            "请上传${list[position].taskName} 任务考核成绩，如考核成绩已全部上传，点击删除"
                        } else {
                            "即将删除 ${list[position].taskName} 任务"
                        }
                        val finishDialog = ConfirmDialog(
                            requireContext(), "删除任务",
                            message, "删除"
                        )
                        finishDialog.show()
                        finishDialog.setonConfirmListen(object : ConfirmDialog.OnConfirmListener {
                            override fun onConfirm() {
                                try {
                                    val taskId = list[position].taskId
                                    list[position].delete()

                                    val findAll = LitePal.findAll(Project::class.java)
                                    for(project in findAll){
                                        if(project.taskId == taskId){
                                            project.delete()
                                        }
                                    }

                                    val scores = LitePal.findAll(Score::class.java)
                                    for(score in scores){
                                        if(score.taskId == taskId){
                                            score.delete()
                                        }
                                    }


                                    val persons = LitePal.findAll(Person::class.java)
                                    for(value in persons){
                                        if(value.taskId == taskId){
                                            value.delete()
                                        }

                                        val find = LitePal.where("personId =?", value.personId)
                                            .find(SosBean::class.java)
                                        for(value in find){
                                            value.delete()
                                        }
                                    }


                                    val find =
                                        LitePal.where("taskId=?", taskId)
                                            .find(LocationInfoBean::class.java)

                                    for(value in find){
                                        value.delete()
                                    }



                                    currentTaskId = ""
                                } catch (e: Exception) {
                                    LogUtils.e("TaskFragment", "delete error is $e")
                                }



                                Handler(Looper.getMainLooper()).post {
                                    finishDialog.dismiss()
                                    initData()
                                }


                            }

                        })
                    }
                }
            }

        })
    }

    override fun attachLayoutRes(): Int = R.layout.activity_all_task


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersionBar(dark = false)
    }
    override fun initView(view: View) {
        mRecyclerView = rv_task
        rv_task.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }



//        taskAdapter.setOnItemClickListener { _, _, position ->
//            if (phoneId.isEmpty()) {
//                ToastUtils.showShort("请先设置手持机ID")
//                return@setOnItemClickListener
//            }
//            if (currentTaskId.isEmpty()){//无当前任务可进
//                val intent = Intent(requireContext(), TaskInfoActivity::class.java)
//                intent.putExtra("taskId", list[position].taskId)
//                startActivity(intent)
//            }else{
//                if (currentTaskId ==list[position].taskId){//只当前任务可进
//                    val intent = Intent(requireContext(), TaskInfoActivity::class.java)
//                    intent.putExtra("taskId", list[position].taskId)
//                    startActivity(intent)
//                }else{
//                    ToastUtils.showShort("请先结束当前正在进行的任务")
//                }
//            }
//        }

        tv_add_task.setOnClickListener {
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
                ), 0x09
            )
        }
    }


    private fun getTaskFromServer() {
        requireActivity().showLoading()
        val taskList = MyRetrofit.instance.api.getTaskList("1", "1000")
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<TaskListModel>() {
                override fun onSucceed(taskListModel: TaskListModel) {
                    Log.e("TaskFragment", "从服务器上拉取任务状态是 ${taskListModel.errorCode}")
                    if (taskListModel.errorCode == Constant.SUCCESSED_CODE) {
                        val tasks = taskListModel.data
                        for (task in tasks) {
                            //任务入库
                            val findAll = LitePal.findAll(TaskBean::class.java)
                            var isSame = false
                            for(value in findAll){
                                isSame = value.taskId == task.taskId
                                break
                            }
                            if(!isSame){
                                val taskBean = TaskBean(null, null, task.taskId, task.taskName)
                                taskBean.save()
                                val projectList = task.subjects
                                for (projectBean in projectList) {
                                    //科目入库
                                    val project = Project(
                                        projectBean.subjectId,
                                        projectBean.subjectNm,
                                        Constant.STATE_PRE,
                                        task.taskId,
                                        0,
                                        0
                                    )
                                    project.save()
                                }
                                //任务人员入库
                                getPersonWithTaskId(task.taskId)
                            }
                        }
                        //刷新数据
                        initData()
                    }
                    requireActivity().dismissLoading()
                }

                override fun onFailed() {
                    //change from xiezekai
                    Log.e("TaskFragment", "从服务器上拉取任务状态是 connect server fail")
                    requireActivity().dismissLoading()
                }
            })
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

    override fun lazyLoad() {
        initData()
    }

    private fun initData() {
        //0:false 1:true
        list = LitePal.where("isMajor=?", "0").find(TaskBean::class.java)
        if (list.isNotEmpty()) {
            Handler(Looper.getMainLooper()).post {
                taskAdapter.setNewData(list)
                taskAdapter.notifyDataSetChanged()
            }
        } else {
            list.clear()
            taskAdapter.setNewData(list)
            taskAdapter.notifyDataSetChanged()
            taskAdapter.setEmptyView(R.layout.empty_view, rv_task)
        }
    }

    private var path: String? = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            //uri = content://com.android.external/storage.documents/document/911E-577F%3A%E6%89%8B%E6%8C%81%E6%9C%BA%E5%AF%BC%E5%85%A5%E5%90%8D%E5%8D%95.xls
            //uri = content://media/external/file/2104
            //uri = content://media/external/file/2798
            Log.e("HCC", "uri =$uri")
            if ("file".equals(uri?.scheme, true)) {
                path = uri?.path
                Log.e("HCC", "PATH 1=$path")
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    path = FileUtil.getPath(requireContext(), uri!!)
                    Log.e("HCC", "PATH2 =$path")
                } else {
                    path = FileUtil.getRealPathFromURI(uri, requireContext())
                    Log.e("HCC", "PATH3 =$path")
                }
            }
            //是表格则处理
            if (path!!.endsWith(".xls") || path!!.endsWith(".xlsx")) {
                requireContext().showLoading()
                Thread {
                    try {
                        decoderXlsFile(path!!)
                        Thread.sleep(100000000)
                    } catch (e: Throwable) {
                        requireContext().dismissLoading()
                        ToastUtils.showLong("解析失败,请确认表格格式")
                    }

                }.start()

            } else {
                ToastUtils.showShort("新建任务失败,仅支持处理表格文件")
            }
        }
    }

    private fun decoderXlsFile(path: String) {
        val majorType = com.jiangtai.team.ui.majorpractice.PoiImport.isMajorType(File(path))
        if(!majorType){
            val task = PoiImport.ExcelReader(File(path))
            val jsonStr = ToJsonUtil.getInstance().toJson(task)
            Log.e("TaskFragment", jsonStr)
            val taskBean = ToJsonUtil.fromJson(jsonStr, TaskBean::class.java)
            val list = LitePal.where("taskId=?", task.taskId).find(TaskBean::class.java)
            if (list.isNotEmpty()) {
                ToastUtils.showShort("不可重复导入相同任务")
                requireContext().dismissLoading()
                return
            }

            //插入数据库
            val save = taskBean.save()
            Logger.e("save=$save")
            val projectList = taskBean.projects
            val personList = taskBean.persons
            if (projectList != null) {
                for (project in projectList) {
                    project.taskId = taskBean.taskId
                    project.save()
                }
            }
            if (personList != null) {
                for (person in personList) {
                    person.taskId = taskBean.taskId
                    person.state = Constant.STATE_PRE
                    person.save()
                }
            }
            //插入完成  刷新表格
            requireActivity().runOnUiThread {
                requireContext().dismissLoading()
                initData()
            }
        } else {
            requireContext().dismissLoading()
            ToastUtils.showShort("请选择考核任务表格")
        }
    }


    companion object {
        fun getInstance(): TaskFragment = TaskFragment()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTaskState(e: RefreshTaskState) {
        initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTaskState(e: ClearEvent) {
        list.clear()
        taskAdapter.setNewData(list)
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.e("TaskFragment init immersionBar $hidden")
        initImmersionBar(dark = false)
    }


}