package com.jiangtai.count.ui.majorpracticedaydetail

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.Person
import com.jiangtai.count.bean.Project
import com.jiangtai.count.bean.UploadMajorScoreData
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.CommandUtils
import com.jiangtai.count.util.Preference
import dismissLoading
import kotlinx.android.synthetic.main.activity_major_practice_day_detail.*
import kotlinx.android.synthetic.main.activity_major_practice_day_detail.iv_back
import kotlinx.android.synthetic.main.activity_major_practice_day_detail.list
import kotlinx.android.synthetic.main.activity_major_practice_day_detail.toolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import showLoading

/**
* @author xiezekai
* @des 专项计划每天详情页 --> (包含人员名单是否完成)
 * //应建表，专项计划成绩单
* @create time 2021/8/5 2:04 下午
*/
class MajorPracticeDayDetailActivity: BaseActivity() {
    private var taskId = ""
    private var projectId = ""
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_practice_day_detail
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(view = toolbar,dark = false)
        val taskId = intent.getStringExtra("taskId")
        val projectId = intent.getStringExtra("projectId")
        if(taskId != null){
            this.taskId = taskId
        }
        if(projectId != null){
            this.projectId = projectId
        }

        iv_back.setOnClickListener {
            finish()
        }


        list.layoutManager = LinearLayoutManager(this)
        list.adapter = MajorPracticeDayDetailAdapter(projectId)

        search.setOnClickListener {
            ToastUtils.showShort("查询所有人员成绩")
            val packageName = packageName
            packageName?.let { packageName ->
                val isTraining = packageName.contains("training")
                if(isTraining){
                    if (phoneId.isNotEmpty()) {
                        val personList = getPersonList()
                        showLoading()
                        Thread{
                            for(value in personList){
                                Handler(Looper.getMainLooper()).post {
                                    CommandUtils.inquiryTrainingScore(this,
                                        phoneId = phoneId,
                                        value.personId,
                                        taskId,
                                        projectId)
                                }

                                Thread.sleep(3000)
                            }
                            Handler(Looper.getMainLooper()).post {
                                dismissLoading()
                            }
                        }.start()

                    } else {
                        ToastUtils.showShort("请设置手持机ID")
                    }
                }
                val vehicle =packageName.contains("vehicle")
                if(vehicle){
                    if (phoneId.isNotEmpty()) {
                        val personList = getPersonList()
                        showLoading()
                        Thread{
                            for(value in personList){
                                Handler(Looper.getMainLooper()).post {
                                    CommandUtils.inquiryVehicleHistory(this,phoneId = phoneId,value.personId,taskId,projectId)
                                }
                                Thread.sleep(3000)
                            }
                            Handler(Looper.getMainLooper()).post {
                                dismissLoading()
                            }
                        }.start()

                    } else {
                        ToastUtils.showShort("请设置手持机ID")
                    }
                }

                val equipage =packageName.contains("equipage")
                if(equipage){
                    if (phoneId.isNotEmpty()) {
                        val personList = getPersonList()
                        for(value in personList){
                            CommandUtils.inquiryShootNumber(this,
                                phoneId = phoneId,
                                value.personId)
                        }
                    } else {
                        ToastUtils.showShort("请设置手持机ID")
                    }
                }
            }
        }

        loadData()
    }

    /**
    * @des 根据TaskId 与 projectId 加载数据
    * @time 2021/8/9 11:11 下午
    */
    private fun loadData(){
        if(projectId.isEmpty() || taskId.isEmpty()){
            return
        }
        val personList = ArrayList<Person>()
        val projectList =
            LitePal.where("taskId =? and projectId =?", taskId, projectId).find(Project::class.java)
        if(projectList.isNotEmpty()){
            val last = projectList.last()
            val peopleId = last.peopleId
          //  practice_name.text = last.projectName
            practice_name.text="任务详情"
            if(peopleId.isNotEmpty()){
                val split = peopleId.split(",")
                for(id in split){

//                    val find = LitePal.where("personId =? and isMajor =?", id.toUpperCase(), "1")
//                        .find(Person::class.java)
                    val find = LitePal.where("personId =?", id.toUpperCase())
                         .find(Person::class.java)
                    if(find.size > 0){
                        personList.add(find.last())
                    } else{
                        LogUtils.e("Major 没有找到该人员 $id")
                    }
                }

                if(personList.size > 0){
                    (list.adapter as MajorPracticeDayDetailAdapter).setNewData(personList)
                    (list.adapter as MajorPracticeDayDetailAdapter).notifyDataSetChanged()
                } else {
                    (list.adapter as MajorPracticeDayDetailAdapter).setNewData(personList)
                    (list.adapter as MajorPracticeDayDetailAdapter).setEmptyView(R.layout.empty_view, list)
                    (list.adapter as MajorPracticeDayDetailAdapter).notifyDataSetChanged()
                }
            }
        }else{
            (list.adapter as MajorPracticeDayDetailAdapter).setNewData(personList)
            (list.adapter as MajorPracticeDayDetailAdapter).setEmptyView(R.layout.empty_view, list)
            (list.adapter as MajorPracticeDayDetailAdapter).notifyDataSetChanged()
        }

    }

    override fun initListener() {

    }




    private fun getPersonList(): ArrayList<Person>{
        val personList = ArrayList<Person>()
        val projectList =
            LitePal.where("taskId =? and projectId =?", taskId, projectId).find(Project::class.java)
        if(projectList.isNotEmpty()) {
            val last = projectList.last()
            val peopleId = last.peopleId
        //    practice_name.text = last.projectName
            practice_name.text="任务详情"
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