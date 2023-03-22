package com.jiangtai.count.ui.majorpracticedetail

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.login.LoginActivity
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MajorRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.ui.majorpracticedaydetail.MajorPracticeDayDetailActivity
import com.jiangtai.count.ui.majorpracticepeople.MajorPracticePeopleActivity
import com.jiangtai.count.util.CommandUtils
import com.jiangtai.count.util.CommonUtil
import com.jiangtai.count.util.Preference
import com.jiangtai.count.util.ToJsonUtil
import kotlinx.android.synthetic.main.activity_major_practice_detail.*
import kotlinx.android.synthetic.main.activity_major_practice_detail.iv_back
import kotlinx.android.synthetic.main.activity_major_practice_detail.kq_return_clear
import kotlinx.android.synthetic.main.activity_major_practice_detail.list
import kotlinx.android.synthetic.main.activity_major_practice_detail.practice_name
import kotlinx.android.synthetic.main.activity_major_practice_detail.toolbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * @author xiezekai
 * @des 专项计划详情页面
 * @create time 2021/8/4 5:03 下午
 */
class MajorPracticeDetailActivity : BaseActivity(), View.OnClickListener {
    private val dataList: ArrayList<Project> = ArrayList()
    private var taskId = ""

    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var isTaskName: String by Preference("isTaskName", "")
    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_practice_detail
    }

    override fun initData() {
        //专项计划名称，id从上个页面传递至此页面
        val taskId = intent.getStringExtra("taskId")
        if (taskId != null && taskId.isNotEmpty()) {
            this.taskId = taskId
        }

        list.layoutManager = LinearLayoutManager(this)
        val majorPracticeDetailAdapterClickListener =
            object : MajorPracticeDetailAdapter.MajorPracticeDetailAdapterClickListener {
                override fun onMajorPracticeAdapterClick(
                    item: Project,
                    isStartProject: Boolean
                ) {
                    if (isStartProject) {
                        val find =
                            LitePal.where("taskId = ?", item.taskId).find(TaskBean::class.java)
                        if (find.isNotEmpty()) {
                            if (phoneId.isNotEmpty()) {
                                LogUtils.e("send projectId is ${item.projectId}")
                                CommandUtils.majorTraining2(
                                    phoneId.toInt(),
                                    parseToInt(item.taskId),
                                    find.first().taskName,
                                    item.projectId,
                                    item.projectName,
                                    item.projectContent,
                                    (item.startTime).toString(),
                                    (item.endTime).toString(),
                                    true,
                                    this@MajorPracticeDetailActivity
                                )
                            } else {
                                ToastUtils.showShort("请设置手持机ID")
                            }
                        }

                    } else {
                        val intent = Intent(
                            this@MajorPracticeDetailActivity,
                            MajorPracticeDayDetailActivity::class.java
                        )
                        intent.putExtra("taskId", item.taskId)
                        intent.putExtra("projectId", item.projectId)
                        startActivity(intent)
                    }
                }

            }
        list.adapter = MajorPracticeDetailAdapter(dataList, majorPracticeDetailAdapterClickListener)
        (list.adapter as MajorPracticeDetailAdapter).setEmptyView(R.layout.empty_view, list)
        open.setOnClickListener {
            val intent =
                Intent(this@MajorPracticeDetailActivity, MajorPracticePeopleActivity::class.java)
            intent.putExtra("taskId", taskId)
            val taskName = practice_name.text.toString()
            intent.putExtra("taskName", taskName)
            startActivity(intent)
        }
        loadData()
    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false, fitSystem = false)
        iv_back.setOnClickListener {
            finish()
        }
        kq_return_clear.setOnClickListener {
            finish()
        }
    }

    override fun initListener() {
        upload.setOnClickListener(this)
//        upload_sos.setOnClickListener(this)
//        upload_normal.setOnClickListener(this)
    }


    /**
     * @des 加载数据根据TaskId
     * @time 2021/8/9 4:04 下午
     */
    private fun loadData() {
        if (taskId.isEmpty()) {
            return
        }
        val taskList = LitePal.where("taskId=?", taskId).find(TaskBean::class.java)
        if (taskList != null && taskList.size > 0) {
            val last = taskList.last()
            practice_name.text =  "我的任务"
            isTaskName = last.taskName
        }
        val find = LitePal.where("taskId=?", taskId).find(Project::class.java)
        //0337005
        for (value in find) {
            value.state = getTaskState(value.startTime, value.endTime,value.projectId)
        }
        if (find.size > 0) {
            (list.adapter as MajorPracticeDetailAdapter).setNewData(dealGrouping(find as ArrayList<Project>))
            (list.adapter as MajorPracticeDetailAdapter).notifyDataSetChanged()
        } else {
            (list.adapter as MajorPracticeDetailAdapter).setEmptyView(R.layout.empty_view, list)
        }
    }


    private fun getTaskState(startTime: Long, endTime: Long,projectId: String): Int {
        var state = Constant.STATE_PRE
        val nowTime = System.currentTimeMillis()
        if (nowTime in (startTime + 1) until endTime) {
            // 现在时间处于开始时间以及结束时间之间
            state = Constant.STATE_RUNNING

//            val projectAllPersonIsFinish = getProjectAllPersonIsFinish(projectId)
//            if(projectAllPersonIsFinish){
//                state = Constant.STATE_FINISHED
//            }
        } else {
            if (nowTime < startTime) {
                state = Constant.STATE_PRE
            } else if (nowTime > endTime) {
                state = Constant.STATE_FINISHED
            }
        }

        return state
    }


    /**
     * @des 项目中所有人员是否已完成
     * @time 2021/9/24 10:56 上午
     */
    private fun getProjectAllPersonIsFinish(projectId: String): Boolean {
        var allPersonIsFinish = true
        val personList = getPersonList(projectId)
        for (value in personList) {
            val times = getTimes(value.personId, projectId, taskId)
            if (!(times[0] != "-- --" && times[1] != "-- --")) {
                allPersonIsFinish = false
                break
            }
        }
        return allPersonIsFinish
    }


    private fun getTimes(
        personId: String,
        projectId: String,
        taskId: String
    ): java.util.ArrayList<String> {
        val arrayList = java.util.ArrayList<String>()

        try {
            val find = LitePal.where(
                "personId = ? and taskId = ? and projectId = ?",
                personId,
                taskId.replace(".0", "").trim(),
                projectId
            ).find(UploadMajorScoreData::class.java)
            if (find.isNotEmpty()) {
                arrayList.add(find.last().startTime)
                arrayList.add(find.last().endTime)
            } else {
                arrayList.clear()
                arrayList.add("-- --")
                arrayList.add("-- --")
            }
        } catch (e: java.lang.Exception) {
            arrayList.add("-- --")
            arrayList.add("-- --")
        }

        return arrayList
    }

    /**
     * @des 将已完成与未开始进行分组
     * @time 2021/8/5 9:00 上午
     */
    private fun dealGrouping(originList: ArrayList<Project>): ArrayList<Project> {
        val finishList: ArrayList<Project> = ArrayList()
        val unFinishList: ArrayList<Project> = ArrayList()
        val runningList: ArrayList<Project> = ArrayList()
        val backArrayList: ArrayList<Project> = ArrayList()

        // const val STATE_PRE = 0
        // const val STATE_RUNNING = 1
        // const val STATE_FINISHED = 2
        // 0:未开始; 1:进行中；2:已完成；

        // 添加 第一个 进行中描述实体
        val projectRunning = Project()
        projectRunning.isDesItem = true
        projectRunning.state = 1
        runningList.add(projectRunning)

        // 添加 第一个 已完成描述实体
        val projectFinish = Project()
        projectFinish.isDesItem = true
        projectFinish.state = 2
        finishList.add(projectFinish)


        // 添加 第一个 未开始描述实体
        val projectUnFinish = Project()
        projectUnFinish.isDesItem = true
        projectUnFinish.state = 0
        unFinishList.add(projectUnFinish)

        for (value in originList) {
            when (value.state) {
                0 -> {
                    //未开始
                    unFinishList.add(value)
                }
                1 -> {
                    //进行中
                    runningList.add(value)
                }
                2 -> {
                    //已完成
                    finishList.add(value)
                }
            }
        }
        backArrayList.clear()
        backArrayList.addAll(runningList)
        backArrayList.addAll(finishList)
        backArrayList.addAll(unFinishList)
        return backArrayList
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.upload -> {
                    uploadMajorScore()
                }
//                R.id.upload_sos -> {
//                    monitorUploadSOS(true)
//                }
//                R.id.upload_normal -> {
//                    monitorUploadSOS(false)
//                }
            }
        }
    }

    /**
     * @des 模拟上报位置（sos 或 普通位置） 通过 业务平台（内网）
     * @time 2021/8/13 11:00 上午
     */
    fun monitorUploadSOS(isSOS: Boolean? = false) {
        val dipperBig = DipperBig()
        if (isSOS!!) {
            dipperBig.cmd = "personsosindication"
        } else {
            dipperBig.cmd = "personlocationindication"
        }
        val taskpersonloclist = dipperBig.taskpersonloclist
        //初始化normal
        for (value in 0..3) {
            val bigDipperPersonVi = BigDipperPersonVi()
            bigDipperPersonVi.deviceid = "0000${value + 1}"
            bigDipperPersonVi.taskid = "${value + 1}"
            bigDipperPersonVi.deviceid = "${value + 1}"
            taskpersonloclist.add(bigDipperPersonVi)
        }

        val sosList = dipperBig.soslist
        for (index in 3..6) {
            val bigDipperPersonVi = BigDipperPersonVi()
            bigDipperPersonVi.sostype = "falldown"
            bigDipperPersonVi.shortid = "00${index + 1}"
            bigDipperPersonVi.deviceid = "0000${index + 1}"
            bigDipperPersonVi.taskid = "${index + 1}"
            bigDipperPersonVi.deviceid = "${index + 1}"
            bigDipperPersonVi.status = "sosing"
            bigDipperPersonVi.heartrate = "100"
            sosList.add(bigDipperPersonVi)
        }


        val toJson = ToJsonUtil.getInstance().toJson(dipperBig)
        val requestBody =
            RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                toJson
            )


        LogUtils.e("MajorPracticeDetailActivity uploadMajorLocation $toJson")
        val uploadMajorScore = MajorRetrofit.instance.api.uploadMajorLocation(requestBody)
        uploadMajorScore.compose(ThreadSwitchTransformer()).subscribe(object :
            CallbackListObserver<ResponseData>() {
            override fun onSucceed(t: ResponseData?) {
                ToastUtils.showShort("上报成功")
            }

            override fun onFailed() {

            }
        })
    }


    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    private var isonSucceed=false
    private var StartHr=""
    private var EndHr=""
    private var StartLong=""
    private var EndLong=""
    private var StartLat=""
    private var EndLat=""


    /**
     * @des 上传计划成绩 （同过业务平台 --> 内网） 按科目上传
     * @time 2021/8/13 10:13 上午
     */
    private fun uploadMajorScore() {

        if(serverIp.isNotEmpty()){

            val find = LitePal.where("taskId = ?", taskId).find(TaskBean::class.java)
            if (find.isNotEmpty()) {
                val first = find.first()
                val projects = LitePal.where("taskId = ?", taskId).find(Project::class.java)
                if (projects.isNotEmpty()) {
                    for (value in projects) {
                         val returnBean = ReturnBean()
                        val peopleid=value.peopleId
                        if (peopleid.isNotEmpty()){
                            val split=peopleid.split(",")

                            for (id in split){
                                val find=LitePal.where("personId =?", id.toUpperCase())
                                    .find(Person::class.java)
                                if (find.isNotEmpty()){
                                    val times = getTimes(id,value.projectId,taskId)
                                    if (times[0] !="-- --"&& times[1]!="-- --"){
                                        returnBean.personId = id
                                        val  scoce=(times[1].toLong() - times[0].toLong())/60000
                                        returnBean.score=scoce.toString()
                                        returnBean.startTm=CommonUtil.formatTime(times[0])
                                        returnBean.finishTm=CommonUtil.formatTime(times[1])
                                        returnBean.taskId = value.projectId
                                        returnBean.taskStarTm = CommonUtil.formatTime(value.startTime.toString())
                                        returnBean.taskEndTm =CommonUtil.formatTime(value.endTime.toString())
                                        returnBean.subjectId = value.projectId
                                        returnBean.subjectStartTm = CommonUtil.formatTime(times[0])
                                        returnBean.subjectEndTm = CommonUtil.formatTime(times[1])
                                        val locationList =  LitePal.findAll(LocationReceiver::class.java)
                                        var isone=0
                                        for ((lacat,e) in locationList.withIndex()){
                                            if (id.equals(e.userId)){
                                                if (isone.equals(0)){
                                                    StartHr=e.heartRate
                                                    StartLong=e.lng
                                                    StartLat=e.lat
                                                    isone++
                                                }else{
                                                    EndHr=e.heartRate
                                                    EndLong=e.lng
                                                    EndLat=e.lat
                                                }
                                            }
                                        }

                                        if (returnBean.score.isNotEmpty()){
                                            if (StartHr.equals("")){
                                                StartHr="0"
                                            }
                                            if (EndHr.equals("")){
                                                EndHr="0"
                                            }
                                            if (StartLong.equals("")){
                                                StartLong="0"
                                            }
                                            if (EndLong.equals("")){
                                                EndLong="0"
                                            }
                                            if (StartLat.equals("")){
                                                StartLat="0"
                                            }
                                            if (EndLat.equals("")){
                                                EndLat="0"
                                            }
                                            val uploadMajorScore = MajorRetrofit.instance.api.uploadRetuenList(returnBean.taskId, returnBean.taskStarTm,
                                                returnBean.taskEndTm,returnBean.subjectId,returnBean.subjectStartTm,returnBean.subjectStartTm,
                                                returnBean.personId, returnBean.score,returnBean.startTm, returnBean.finishTm,
                                                StartHr,EndHr, StartLong, EndLong, StartLat, EndLat,
                                                LoginActivity.access_token)
                                            uploadMajorScore.compose(ThreadSwitchTransformer()).subscribe(object :
                                                CallbackListObserver<ReturnIsBean>() {
                                                override fun onSucceed(returnIsBean: ReturnIsBean) {
                                                    ToastUtils.showShort(returnIsBean.info)
                                                    isonSucceed=true
                                                }

                                                override fun onFailed() {

                                                }
                                            })
                                        }
                                    }
                                }else{
                                    ToastUtils.showShort("查无此人员")
                                }

                            }

                        }else{
                            ToastUtils.showShort("该科目暂时无人")
                        }
//                        returnBean.taskId = realTaskId
//                        returnBean.taskStarTm = CommonUtil.formatTime(first.startTime.toString())
//                        returnBean.taskEndTm =CommonUtil.formatTime(first.endTime.toString())
//                        returnBean.subjectId = value.taskId
//                        returnBean.subjectStartTm = value.startTime.toString()
//                        returnBean.subjectEndTm = value.endTime.toString()




                    }
                    if (false){
                        ToastUtils.showShort("暂无数据上传")
                        isonSucceed=false
                    }

                } else {
                    ToastUtils.showShort("暂无计划项目")
                }

            } else {
                ToastUtils.showShort("暂无计划")
            }
        } else {
            ToastUtils.showShort("请设置服务器IP")
        }



    }


    private fun getPersonList(projectId: String): ArrayList<Person> {
        val personList = ArrayList<Person>()
        val projectList =
            LitePal.where("taskId =? and projectId =?", taskId, projectId).find(Project::class.java)
        if (projectList.isNotEmpty()) {
            val last = projectList.last()
            val peopleId = last.peopleId
            practice_name.text =  "我的任务"
            if (peopleId.isNotEmpty()) {
                val split = peopleId.split(",")
                for (id in split) {
                    val find = LitePal.where("personId =? and isMajor =?", id.toUpperCase(), "1")
                        .find(Person::class.java)
                    if (find.size > 0) {
                        personList.add(find.last())
                    }
                }
            }
        }
        return personList
    }

    private fun parseToInt(string: String): Int {
        var int = 0
        if (string.isEmpty()) {
            return int
        }
        try {
            int = Integer.parseInt(string)
        } catch (e: Exception) {
            try {
                int = string.toFloat().toInt()
            } catch (e: Exception) {

            }

        }

        return int
    }

    override fun useEventBus(): Boolean {
        return true
    }


    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = true)
    fun refresh(e: UploadMajorScoreData){
        EventBus.getDefault().removeStickyEvent(e)
        runOnUiThread {
            loadData()
        }
    }
}