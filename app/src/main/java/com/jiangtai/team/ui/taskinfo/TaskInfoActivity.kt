package com.jiangtai.team.ui.taskinfo

import android.annotation.SuppressLint
import android.content.*
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.base.BaseNoDataBean
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.Score
import com.jiangtai.team.bean.TaskBean
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.dialog.ConfirmDialog
import com.jiangtai.team.event.RefeshProjectStateEvent
import com.jiangtai.team.event.RefreshTaskState
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.request.ScoreUploadModel
import com.jiangtai.team.ui.achievement.AchievementActivity
import com.jiangtai.team.ui.person.PersonListActivity
import com.jiangtai.team.util.*
import dismissLoading
import kotlinx.android.synthetic.main.activity_task_info.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import org.litepal.extension.find
import showLoading
import showToast

/**
 * Created by heCunCun on 2021/3/8
 */
class TaskInfoActivity : BaseActivity() {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    override fun useEventBus(): Boolean = true
    private var currentTaskId: String by Preference(Constant.CURRENT_TASK_ID, "")
    private var list = mutableListOf<Project>()
    private var listPre = mutableListOf<Project>()
    private var listRunning = mutableListOf<Project>()
    private var listFinished = mutableListOf<Project>()
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private val taskPreAdapter by lazy {
        TaskPreAdapter()
    }
    private val taskRunningAdapter by lazy {
        TaskRunningAdapter()
    }
    private val taskFinishedAdapter by lazy {
        TaskFinishedAdapter()
    }

    override fun attachLayoutRes(): Int = R.layout.activity_task_info
    private var taskId = ""
    override fun initData() {
        taskId = intent.getStringExtra("taskId")
        refreshData()
    }

    private fun refreshData() {
        list = LitePal.where("taskId=?", taskId).find(Project::class.java)
        if (list.isNotEmpty()) {
            listPre = list.filter { it.state == Constant.STATE_PRE } as MutableList<Project>
            listRunning = list.filter { it.state == Constant.STATE_RUNNING } as MutableList<Project>
            listFinished =
                list.filter { it.state == Constant.STATE_FINISHED } as MutableList<Project>
        }
        taskPreAdapter.setNewData(listPre)
        taskRunningAdapter.setNewData(listRunning)
        taskFinishedAdapter.setNewData(listFinished)
    }

    override fun initView() {
        rv_task_pre.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TaskInfoActivity)
            isNestedScrollingEnabled = false
            adapter = taskPreAdapter
        }
        rv_task_running.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TaskInfoActivity)
            isNestedScrollingEnabled = false
            adapter = taskRunningAdapter
        }
        rv_task_finished.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TaskInfoActivity)
            isNestedScrollingEnabled = false
            adapter = taskFinishedAdapter
        }
    }

    @SuppressLint("WrongConstant")
    override fun initListener() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_open.setOnClickListener {
            val intent = Intent(this, PersonListActivity::class.java)
            intent.putExtra("taskId", taskId)
            startActivity(intent)
        }

        tv_upload.setOnClickListener {
            if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
                showToast("??????????????????????????????IP")
                return@setOnClickListener
            }
            val uploadDialog = ConfirmDialog(
                this, "??????????????????",
                "???????????????????????????????????????????????????????????????????????????????????????????????????", "????????????"
            )
            uploadDialog.show()
            uploadDialog.setonConfirmListen(object : ConfirmDialog.OnConfirmListener {
                override fun onConfirm() {
                    uploadDialog.dismiss()
                    //?????????????????????????????????
                    val taskList = LitePal.where("taskId =?", taskId).find(TaskBean::class.java)

                    //????????????????????????
                    val uploadScoreList = mutableListOf<ScoreUploadModel>()

                    if (taskList.isNotEmpty()) {
                        val task = taskList[0]

                        //??????????????????????????????
                        val projects = LitePal.where("taskId =?", taskId).find(Project::class.java)

                        if(projects != null){
                            //????????????????????????????????????
                            val allScoreList = ArrayList<Score>()
                            for(value in projects){
                                //???????????????????????????
                                if(value.state == Constant.STATE_FINISHED){
                                    val scoreList = LitePal.where("taskId =? and projectId =?",
                                        taskId,value.projectId).find(Score::class.java)
                                    allScoreList.addAll(scoreList)
                                }
                            }

                           if(allScoreList.isEmpty()){
                                //?????????????????????????????????
                                showToast("??????????????????")
                            } else {
                                for (score in allScoreList) {
                                    val scoreUploadModel = ScoreUploadModel(
                                        taskId,
                                        task.startTime,
                                        task.endTime,
                                        score.projectId,
                                        score.startTime,
                                        score.endTime,
                                        score.personId!!,
                                        score.score,
                                        System.currentTimeMillis(),
                                        System.currentTimeMillis()
                                    )
                                    uploadScoreList.add(scoreUploadModel)
                                }
                                //????????????
                                val requestBody =
                                    RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),ToJsonUtil.getInstance().toJson(uploadScoreList))
                                val uploadCallback = MyRetrofit.instance.api.uploadScoreList(requestBody)
                                uploadCallback.compose(ThreadSwitchTransformer()).subscribe(object :CallbackListObserver<BaseNoDataBean>(){
                                    override fun onSucceed(t: BaseNoDataBean?) {
                                        if(t?.error_code == Constant.SUCCESSED_CODE){
                                            showToast("????????????")
                                        }else{
                                            showToast("????????????,msg =${t?.error_msg}")
                                        }
                                    }

                                    override fun onFailed() {
                                        showToast("????????????")
                                    }
                                })
                            }
                        } else {
                            //??????????????????
                            showToast("??????????????????")
                        }

                    }
                }
            })
        }
        taskPreAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.iv_more -> {
                    val intent = Intent(this, AchievementActivity::class.java)
                    intent.putExtra("taskId", taskId)
                    intent.putExtra("projectId", listPre[position].projectId)
                    intent.putExtra("projectName", listPre[position].projectName)
                    intent.putExtra("showFinish", false)
                    startActivity(intent)
                }
                R.id.iv_change_state -> {
                    if (currentTaskId.isNotEmpty() && currentTaskId != taskId){
                        showToast("??????????????????????????????")
                        return@setOnItemChildClickListener
                    }
                    val preBean = LitePal.find<Project>(listPre[position].id)
                    preBean?.state = Constant.STATE_RUNNING
                    preBean?.save()
                    refreshData()
                    //3000M??????????????????  ????????????
                    //????????????????????????
                    if (preBean?.projectId == "E100") {
                        showLoading()
                        val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
                        val bytes = arrayOf(
                            0xE7.toByte(),
                            0xEA.toByte(),
                            0x53.toByte(),
                            3,
                            0xE1.toByte(),
                            0x00.toByte(),
                            phoneId.toByte(),
                            0,
                            0
                        )
                        val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 7)
                        val shortToBytes = HexUtil.shortToBytes(calculateCrc)
                        bytes[7] = shortToBytes[0]
                        bytes[8] = shortToBytes[1]
                        val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
                        //????????????????????????
                        Log.e("SEND", "SEND==>$formatHexString")
                        //showToast("SEND==>$formatHexString")
                        val sndData = SndData(bytes.toByteArray(), bytes.size)
                        loRaManager.sendContent(sndData)
                        //?????? ??????????????????
                        //???????????????????????????3000M???????????????
                        val personList = LitePal.findAll(Person::class.java)
                        for (person in personList) {
                            val score = Score(
                                taskId,
                                person.personId,
                                "E100",
                                "3000M",
                                "",
                                Constant.STATE_RUNNING,
                                System.currentTimeMillis(),
                                0
                            )
                            score.save()
                        }
                    }
                    startTask()

                }
            }
        }
        taskRunningAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.iv_more -> {
                    val intent = Intent(this, AchievementActivity::class.java)
                    intent.putExtra("taskId", taskId)
                    intent.putExtra("projectId", listRunning[position].projectId)
                    intent.putExtra("projectName", listRunning[position].projectName)
                    intent.putExtra("showFinish", true)
                    startActivity(intent)
                }
                R.id.iv_change_state -> {
//                    val preBean = LitePal.find<Project>(listRunning[position].id)
//                    preBean?.state = Constant.STATE_FINISHED
//                    preBean?.save()
//                    refreshData()
                }
            }
        }
        taskFinishedAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.iv_more -> {
                    val intent = Intent(this, AchievementActivity::class.java)
                    intent.putExtra("taskId", taskId)
                    intent.putExtra("projectId", listFinished[position].projectId)
                    intent.putExtra("projectName", listFinished[position].projectName)
                    intent.putExtra("showFinish", false)
                    startActivity(intent)
                }
                R.id.iv_change_state -> {
                    //todo ???????????????????????????????????????
//                    val preBean = LitePal.find<Project>(listFinished[position].id)
//                    preBean?.state = Constant.STATE_PRE
//                    preBean?.save()
//                    refreshData()
                    showToast("???????????????")
                }
            }
        }

        tv_finish.setOnClickListener {
                //????????????
                val finishDialog = ConfirmDialog(
                    this, "????????????",
                    "????????????????????????????????????????????????????????????????????????????????????????????????????????????", "??????"
                )
                finishDialog.show()
                finishDialog.setonConfirmListen(object :ConfirmDialog.OnConfirmListener{
                    override fun onConfirm() {
                        val listProject = LitePal.where("taskId = ?", currentTaskId).find(Project::class.java)
                        listProject.forEach {
                            it.state = Constant.STATE_FINISHED
                            it.save()
                        }
                        refreshData()//??????????????????
                        //????????????????????????
                        currentTaskId = ""
                        list = LitePal.where("taskId=?", taskId).find(Project::class.java)
                        val taskBean = LitePal.where("taskId=?", taskId).find(TaskBean::class.java)[0]
                        taskBean.state = Constant.STATE_FINISHED
                        taskBean.endTime = System.currentTimeMillis()
                        taskBean.save()
                        //??????????????????????????????
                        EventBus.getDefault().post(RefreshTaskState(Constant.STATE_FINISHED))
                        finishDialog.dismiss()
                    }
                })
        }
    }

    //????????????
    private fun startTask() {
        if (currentTaskId == "") {//???????????????????????????????????????
            currentTaskId = taskId
            //???????????????
            val taskBean = LitePal.where("taskId=?", taskId).find(TaskBean::class.java)[0]
            taskBean.state = Constant.STATE_RUNNING
            taskBean.startTime = System.currentTimeMillis()
            taskBean.save()
            //??????????????????????????????
            EventBus.getDefault().post(RefreshTaskState(Constant.STATE_RUNNING))
        } else {
            if (currentTaskId != taskId) {
                showToast("??????????????????????????????")
            }
        }
        dismissLoading()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshProjectState(e: RefeshProjectStateEvent){
        refreshData()
    }

}