package com.jiangtai.team.ui.majorpracticescore

import android.support.v7.widget.LinearLayoutManager
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.UploadMajorScoreData
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.ui.majorpracticedetail.MajorPracticeDetailAdapter
import kotlinx.android.synthetic.main.activity_major_practice_score.iv_back
import kotlinx.android.synthetic.main.activity_major_practice_score.list
import kotlinx.android.synthetic.main.activity_major_practice_score.practice_name
import kotlinx.android.synthetic.main.activity_major_practice_score.toolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import kotlin.collections.ArrayList

/**
 * @author xiezekai
 * @des  专项计划成绩列表 （个人整个计划计划成绩列表）
 * @create time 2021/8/5 12:43 下午
 */
class MajorPracticeScoreActivity : BaseActivity() {
    private val dataList: ArrayList<Project> = ArrayList()
    private var taskId = ""
    private var personId = ""
    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_practice_score
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)
        val practiceName = intent.getStringExtra("taskName")
        if (practiceName != null && practiceName.isNotEmpty()) {
            practice_name.text = practiceName
        } else {
            practice_name.text = "演习专项计划"
        }


        val taskId = intent.getStringExtra("taskId")
        val personId = intent.getStringExtra("personId")


        if (taskId != null) {
            this.taskId = taskId
        }

        if (personId != null) {
            this.personId = personId
        }

        iv_back.setOnClickListener {
            finish()
        }

        list.layoutManager = LinearLayoutManager(this)
        val majorPracticeDetailAdapter = MajorPracticeDetailAdapter(dataList,
            object : MajorPracticeDetailAdapter.MajorPracticeDetailAdapterClickListener {
                override fun onMajorPracticeAdapterClick(
                    item: Project,
                    isStartProject: Boolean
                ) {
                    //控件已隐藏不实现
                }
            }, isScore = true,personId
        )
        list.adapter = majorPracticeDetailAdapter
        (list.adapter as MajorPracticeDetailAdapter).setEmptyView(R.layout.empty_view, list)

        loadData()
    }


    private fun loadData() {
        if (taskId.isEmpty() || personId.isEmpty()) {
            return
        }

        val arrayList = ArrayList<Project>()

        val find = LitePal.where("taskId =?", taskId).find(Project::class.java)
        for(value in find){
            value.state = getTaskState(value.startTime,value.endTime)
        }
        if (find.size > 0) {
            for (value in find) {
                val peopleId = value.peopleId
                if (peopleId.isNotEmpty()) {
                    val split = peopleId.split(",")
                    for (id in split) {
                        if (id.equals(personId, ignoreCase = true)) {
                            arrayList.add(value)
                            break
                        }
                    }
                }
            }
        }


        if (arrayList.size > 0) {
            (list.adapter as MajorPracticeDetailAdapter).setNewData(arrayList)
            (list.adapter as MajorPracticeDetailAdapter).notifyDataSetChanged()
        } else {
            (list.adapter as MajorPracticeDetailAdapter).setEmptyView(R.layout.empty_view, list)
        }
    }

    override fun initListener() {

    }


    private fun getTaskState(startTime:Long,endTime:Long):Int{
        var state = Constant.STATE_PRE

        val nowTime = System.currentTimeMillis()


        if(nowTime in (startTime + 1) until endTime){
            // 现在时间处于开始时间以及结束时间之间
            state = Constant.STATE_RUNNING
        } else {
            if(nowTime < startTime){
                state = Constant.STATE_PRE
            } else if(nowTime > endTime){
                state = Constant.STATE_FINISHED
            }
        }

        return state
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