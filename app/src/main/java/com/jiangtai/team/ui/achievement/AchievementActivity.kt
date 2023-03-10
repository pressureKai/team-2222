package com.jiangtai.team.ui.achievement

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.LoRaManager
import android.content.SndData
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.LocationInfoBean
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.Score
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.constant.Constant.STATE_RUNNING
import com.jiangtai.team.dialog.ConfirmDialog
import com.jiangtai.team.dialog.ScoreInputDialog
import com.jiangtai.team.dialog.ScoreInputDialog2
import com.jiangtai.team.event.RefeshProjectStateEvent
import com.jiangtai.team.event.RefreshScoreStateEvent
import com.jiangtai.team.ui.map.MapActivity
import com.jiangtai.team.util.CrcUtil
import com.jiangtai.team.util.HexUtil
import com.jiangtai.team.util.Preference
import kotlinx.android.synthetic.main.activity_achievement.*
import kotlinx.android.synthetic.main.activity_achievement.iv_back
import kotlinx.android.synthetic.main.activity_achievement.tv_finish
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import showToast

/**
 * Created by heCunCun on 2021/3/8
 */
class AchievementActivity : BaseActivity() {
    override fun useEventBus(): Boolean = true
    private var list = mutableListOf<Person>()
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private val achievementAdapter by lazy {
        AchievementAdapter()
    }

    override fun attachLayoutRes(): Int = R.layout.activity_achievement
    private var taskId = ""
    private var projectId = ""
    private var projectName = ""
    override fun initData() {
        taskId = intent.getStringExtra("taskId")
        projectId = intent.getStringExtra("projectId")
        projectName = intent.getStringExtra("projectName")
        val showFinish = intent.getBooleanExtra("showFinish", false)
        if (showFinish) {
            tv_finish.visibility = View.VISIBLE
        } else {
            tv_finish.visibility = View.GONE
        }
        tv_project_name.text = projectName

        re_do.setOnClickListener {
            if(projectId == "E100"){
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
                Log.e("SEND", "SEND==>$formatHexString")
                //showToast("SEND==>$formatHexString")
                val sndData = SndData(bytes.toByteArray(), bytes.size)
                loRaManager.sendContent(sndData)

                val projects =
                    LitePal.where("taskId like ? and projectId like ?", taskId, projectId)
                        .find(Project::class.java)
                if (projects.isNotEmpty()) {
                    for(value in projects){
                        value.state = STATE_RUNNING
                        value?.save()
                        EventBus.getDefault().post(RefeshProjectStateEvent())
                    }
                    achievementAdapter.notifyDataSetChanged()
                }


                tv_finish.visibility = View.VISIBLE
            }

        }
        refreshData()
    }

    private fun refreshData() {
        list = LitePal.where("taskId=?", taskId).find(Person::class.java)
        list.forEach {
            it.projectId = projectId
            val scoreList = LitePal.where(
                "taskId like ? and projectId like ? and personId like ?",
                taskId,
                projectId,
                it.personId
            ).find(Score::class.java)
            if (scoreList.isNotEmpty()) {
                it.score = scoreList[0].score
                it.state = scoreList[0].state
                Log.e("ss", "score=${scoreList[0].score}")
            }
        }

        for(value in list){
            if(value.state == STATE_RUNNING && projectId == "E100"){
                re_do.visibility = View.VISIBLE
                break
            }
        }


        achievementAdapter.setNewData(list)
        if(checkSearchState()){
            iv_search.visibility = View.VISIBLE
        } else {
            iv_search.visibility = View.INVISIBLE
        }
    }

    override fun initView() {
        rv_person.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AchievementActivity)
            isNestedScrollingEnabled = false
            adapter = achievementAdapter
        }
        initImmersionBar(view = toolbar,dark = false)
    }

    @SuppressLint("WrongConstant")
    override fun initListener() {
        iv_back.setOnClickListener { finish() }
        tv_finish.setOnClickListener {
            //????????????
            var message = "???????????????????????????????????????????????????????????????????????????????????????????????????????????????"

            for(value in list){
                if(value.state == STATE_RUNNING || value.state == Constant.STATE_PRE){
                    message = "???????????????????????????????????????????????????????????????????????????????????????????????????"
                    break
                }
            }
            val finishDialog = ConfirmDialog(
                this, "????????????",
                message, "??????"
            )
            finishDialog.show()
            finishDialog.setonConfirmListen(object : ConfirmDialog.OnConfirmListener {
                override fun onConfirm() {
                    val projects =
                        LitePal.where("taskId like ? and projectId like ?", taskId, projectId)
                            .find(Project::class.java)
                    if (projects.isNotEmpty()) {
                        for(value in projects){
                            value.state = Constant.STATE_FINISHED
                            value?.save()
                            EventBus.getDefault().post(RefeshProjectStateEvent())
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            finishDialog.dismiss()
                            finish()
                        },10)

                    }

                }

            })
        }

        achievementAdapter.setOnItemChildClickListener { adapter, view, position ->
       //     ToastUtils.showShort("??????")
            when (view.id) {
                R.id.tv_score -> {
                    val person1 = adapter.data[position] as Person
                    person1?.let {
                        val projects =
                            LitePal.where("taskId like ? and projectId like ?", taskId, projectId)
                                .find(Project::class.java)

                        if(projects.isNotEmpty()){
                           if(projects[0].state == STATE_RUNNING){
                                  // ????????????????????????
 //                               if(person1.state != STATE_RUNNING){
                                    //????????????
 //                                   val person = adapter.data[position] as Person
 //                                   Log.e("AC", "projectId=${projectId}")
//                                    if (person.score.isNotEmpty()) {
//                                        ToastUtils.showShort("????????????")
//                                    } else {
                                        when (projectId) {
                                            "E011", "E012", "E026" -> {
                                                val scoreInputDialog =
                                                    ScoreInputDialog(
                                                        this,
                                                        projectName + "??????:" + list[position].name,
                                                        "???"
                                                    )
                                                scoreInputDialog.show()
                                                scoreInputDialog.setonConfirmListen(object :
                                                    ScoreInputDialog.OnConfirmListener {
                                                    override fun onConfirm(etScore: String) {
                                                        scoreInputDialog.dismiss()
                                                        //????????????
                                                        val scoreList = LitePal.where(
                                                            "taskId like ? and projectId like ? and personId like ?",
                                                            taskId,
                                                            projectId,
                                                            list[position].personId
                                                        ).find(Score::class.java)
                                                        if (scoreList.isEmpty()) {
                                                            val bean = Score()
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            when (projectId) {
                                                                "E011" -> {
                                                                    bean.projectName = "????????????"
                                                                }
                                                                "E012" -> {
                                                                    bean.projectName = "????????????"
                                                                }
                                                                "E026" -> {
                                                                    bean.projectName = "?????????"
                                                                }
                                                            }

                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.startTime = System.currentTimeMillis()
                                                            bean.score = "${etScore}???"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        } else {
                                                            val bean = scoreList[0]
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.score = "${etScore}???"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        }
                                                    }
                                                })
                                            }
                                            "E100" -> {
                                                val scoreInputDialog2 =
                                                    ScoreInputDialog2(
                                                        this,
                                                        projectName + "??????:" + list[position].name,
                                                        "???",
                                                        "???"
                                                    )
                                                scoreInputDialog2.show()
                                                scoreInputDialog2.setonConfirmListen(object :
                                                    ScoreInputDialog2.OnConfirmListener {
                                                    override fun onConfirm(etScore1: String, etScore2: String) {
                                                        scoreInputDialog2.dismiss()
                                                        //????????????
                                                        val scoreList = LitePal.where(
                                                            "taskId like ? and projectId like ? and personId like ?",
                                                            taskId,
                                                            projectId,
                                                            list[position].personId
                                                        ).find(Score::class.java)
                                                        if (scoreList.isEmpty()) {
                                                            val bean = Score()
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.projectName = "3000M"
                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.startTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}???${etScore2}???"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        } else {
                                                            val bean = scoreList[0]
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}???${etScore2}???"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        }
                                                    }
                                                })
                                            }
                                            "E027" -> {
                                                val scoreInputDialog2 =
                                                    ScoreInputDialog2(
                                                        this,
                                                        projectName + "??????:" + list[position].name,
                                                        "s",
                                                        "ms"
                                                    )
                                                scoreInputDialog2.show()
                                                scoreInputDialog2.setonConfirmListen(object :
                                                    ScoreInputDialog2.OnConfirmListener {
                                                    override fun onConfirm(etScore1: String, etScore2: String) {
                                                        scoreInputDialog2.dismiss()
                                                        //????????????
                                                        val scoreList = LitePal.where(
                                                            "taskId like ? and projectId like ? and personId like ?",
                                                            taskId,
                                                            projectId,
                                                            list[position].personId
                                                        ).find(Score::class.java)
                                                        if (scoreList.isEmpty()) {
                                                            val bean = Score()
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.projectName = "?????????"
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.startTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}s,${etScore2}ms"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        } else {
                                                            val bean = scoreList[0]
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}s,${etScore2}ms"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        }
                                                    }

                                                })

                                            }
                                            "E016" -> {
                                                val scoreInputDialog2 =
                                                    ScoreInputDialog2(
                                                        this,
                                                        projectName + "??????:" + list[position].name,
                                                        "cm",
                                                        "kg"
                                                    )
                                                scoreInputDialog2.show()
                                                scoreInputDialog2.setonConfirmListen(object :
                                                    ScoreInputDialog2.OnConfirmListener {
                                                    override fun onConfirm(etScore1: String, etScore2: String) {
                                                        scoreInputDialog2.dismiss()
                                                        //????????????
                                                        val scoreList = LitePal.where(
                                                            "taskId like ? and projectId like ? and personId like ?",
                                                            taskId,
                                                            projectId,
                                                            list[position].personId
                                                        ).find(Score::class.java)
                                                        if (scoreList.isEmpty()) {
                                                            val bean = Score()
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.projectName = "????????????"
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.startTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}cm,${etScore2}kg"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        } else {
                                                            val bean = scoreList[0]
                                                            bean.state = Constant.STATE_FINISHED
                                                            bean.taskId = taskId
                                                            bean.projectId = projectId
                                                            bean.personId = list[position].personId
                                                            bean.endTime = System.currentTimeMillis()
                                                            bean.score = "${etScore1}cm,${etScore2}kg"
                                                            bean.save()
                                                            Log.e("TTT", "????????????")
                                                            refreshData()
                                                        }
                                                    }

                                                })
                                            }
                                        }
                                   // }
//                                } else {
//                                    ToastUtils.showShort("?????????????????????")
//                                }

                            } else {
                                ToastUtils.showShort("?????????????????????")
                            }
                        }
                    }
                }
            }

        }

         //?????????  ?????????????????????
        achievementAdapter.setOnItemClickListener { adapter, _, position ->
            val person = adapter.data[position] as Person
            if ((person.state == STATE_RUNNING || (getLastPoint(person.personId) && person.state == Constant.STATE_FINISHED )) && projectId == "E100" ) {
                //???????????????
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("name", person.name)
                intent.putExtra("personId", person.personId)
                intent.putExtra("projectId", projectId)
                intent.putExtra("taskId", taskId)
                startActivity(intent)
            }
        }

        iv_search.setOnClickListener {
            //????????????,??????????????????""??????
            showToast("?????????????????????")
            val filter = list.filter {
                it.score == ""
            }
            Thread {
                Looper.prepare()
                var count = 0
                while (count < filter.size) {
                    filter.forEach { person ->
                        //val watchId = HexUtil.str2Bcd(person.WatchId)
                        if (person.personId.length == 32 && person.state == STATE_RUNNING) {
                            val personId = HexUtil.decodeHex(person.personId.toCharArray())
                            Log.e("sendCMD", "personId =${person.personId}")
                            searchScore(personId)
                            Thread.sleep(1500)
                        } else {
                            Log.e("sendCMD", "personId????????????")
                        }
                        count++
                    }

                }
            }.start()

        }
    }


    private fun checkSearchState():Boolean{
        var rightState = false
        for(value in list){
            if(value.personId.length == 32 && value.state == STATE_RUNNING){
                rightState = true
                break
            }
        }
        return rightState
    }

    @SuppressLint("WrongConstant")
    private fun searchScore(peronId: ByteArray) {
        val loRaManager = getSystemService(LORA_SERVICE) as LoRaManager
        val bytes = arrayOf(
            0xE7.toByte(),
            0xEA.toByte(),
            0x70.toByte(),
            18,
            HexUtil.hexStringToByteArray(projectId)[0],
            HexUtil.hexStringToByteArray(projectId)[1],
            peronId[0], peronId[1], peronId[2], peronId[3], peronId[4], peronId[5],
            peronId[6], peronId[7], peronId[8], peronId[9], peronId[10], peronId[11],
            peronId[12], peronId[13], peronId[14], peronId[15],
            0,
            0
        )
        //crc??????
        val calculateCrc = CrcUtil.Calculate_Crc(bytes.toByteArray(), 0, 22)
        val shortToBytes = HexUtil.shortToBytes(calculateCrc)
        bytes[22] = shortToBytes[0]
        bytes[23] = shortToBytes[1]
        val formatHexString = HexUtil.formatHexString(bytes.toByteArray(), true)
        Log.e("SEND", "Search==>$formatHexString")
        val sndData = SndData(bytes.toByteArray(), bytes.size)
        loRaManager.sendContent(sndData)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPersonState(e: RefreshScoreStateEvent) {
        refreshData()
    }



    private fun getLastPoint(personId: String): Boolean {
        val find =
            LitePal.where("taskId=? and personId=? and projectId=?", taskId, personId, projectId)
                .find(LocationInfoBean::class.java)
        return find.size > 0
    }


}