package com.jiangtai.team.ui.majorpracticepeople

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.ui.majorpracticescore.MajorPracticeScoreActivity
import com.jiangtai.team.util.CommandUtils
import com.jiangtai.team.util.Preference
import dismissLoading
import kotlinx.android.synthetic.main.activity_major_practice_day_detail.*
import kotlinx.android.synthetic.main.activity_major_practice_people.*
import kotlinx.android.synthetic.main.activity_major_practice_people.iv_back
import kotlinx.android.synthetic.main.activity_major_practice_people.list
import kotlinx.android.synthetic.main.activity_major_practice_people.practice_name
import kotlinx.android.synthetic.main.activity_major_practice_people.search
import kotlinx.android.synthetic.main.activity_major_practice_people.toolbar
import org.litepal.LitePal
import showLoading
import java.lang.Exception

/**
 * @author xiezekai
 * @des  人员名单页面（专项任务详情页 -> 按计划名单查看 -> 显示人员名单）
 * @create time 2021/8/5 11:17 上午
 */
class MajorPracticePeopleActivity : BaseActivity() {
    private var taskId = ""
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_practice_people
    }

    override fun initData() {

    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)

        val taskId = intent.getStringExtra("taskId")
        val taskName = intent.getStringExtra("taskName")
        if (taskId != null) {
            this.taskId = taskId
        }

        if (taskName != null) {
            practice_name.text = taskName
        }
        iv_back.setOnClickListener {
            finish()
        }

        list.layoutManager = LinearLayoutManager(this)
        val majorPracticePeopleAdapter = MajorPracticePeopleAdapter()
        majorPracticePeopleAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.more) {
                val intent =
                    Intent(this@MajorPracticePeopleActivity, MajorPracticeScoreActivity::class.java)
                intent.putExtra("taskName",practice_name.text.toString())
                intent.putExtra("taskId",taskId)
                val person = adapter.data[position] as Person
                intent.putExtra("personId",person.personId)
                startActivity(intent)
            }
        }
        list.adapter = majorPracticePeopleAdapter
        (list.adapter as MajorPracticePeopleAdapter).setEmptyView(R.layout.empty_view, list)

        search.setOnClickListener {
            ToastUtils.showShort("查询全部人员成绩")
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
                                        getCurrentProjectId(value.personId))
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
                                    CommandUtils.inquiryVehicleHistory(this,phoneId = phoneId,value.personId,taskId,getCurrentProjectId(value.personId))
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
                            CommandUtils.inquiryShootNumber(this,phoneId = phoneId,value.personId)
                        }
                    } else {
                        ToastUtils.showShort("请设置手持机ID")
                    }
                }
            }
        }

        loadData()

    }


    private fun loadData() {
        if (taskId.isEmpty()) {
            return
        }
//        val personList =
//            LitePal.where("taskId =? and isMajor =?", taskId, "1").find(Person::class.java)
        val personList =
            LitePal.where("taskId =?", taskId).find(Person::class.java)

        if (personList.size > 0) {
            (list.adapter as MajorPracticePeopleAdapter).setNewData(personList)
            (list.adapter as MajorPracticePeopleAdapter).notifyDataSetChanged()
        } else {
            (list.adapter as MajorPracticePeopleAdapter).setEmptyView(R.layout.empty_view, list)
        }
    }

    override fun initListener() {

    }


    private fun getPersonList(): ArrayList<Person> {
        val personList =
            LitePal.where("taskId =? and isMajor =?", taskId, "1").find(Person::class.java)
        return personList as ArrayList<Person>
    }

    private fun getCurrentProjectId(personId:String): String {
        var projectId = ""
        val find = LitePal.where("taskId = ?", taskId).find(Project::class.java)

        try {
            for(value in find){
                val peopleId = value.peopleId
                if(peopleId.isNotEmpty()){
                    val split = peopleId.split(",")
                    for(pId in split){
                        if(pId.equals(personId, ignoreCase = true)){
                            projectId = value.projectId
                        }
                    }
                }
            }
        }catch (e:Exception){

        }

        return projectId
    }



}