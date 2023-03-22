package com.jiangtai.count.ui.task

import android.support.v7.widget.LinearLayoutManager
import android.widget.CheckBox
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.Project
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.util.Preference
import kotlinx.android.synthetic.main.activity_major_project.*
import kotlinx.android.synthetic.main.activity_major_task.iv_back
import kotlinx.android.synthetic.main.activity_major_task.list
import kotlinx.android.synthetic.main.activity_major_task.practice_name
import kotlinx.android.synthetic.main.activity_major_task.toolbar
import org.litepal.LitePal

class MajorProjectActivity : BaseActivity() {
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")

    private var mTempCurrentMajorProject: String by Preference(Constant.TEMP_CURRENT_MAJOR_PROJECT, "")

    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_project
    }

    override fun initData() {
        mTempCurrentMajorProject = mCurrentMajorProject
        loadData()
    }

    private fun loadData() {
        val taskId = intent.getStringExtra("taskId")
        taskId?.let {
            val find = LitePal.where("taskId = ?", taskId).find(Project::class.java)
//            if (find.size > 0) {
//                if (mTempCurrentMajorProject.isEmpty()) {
//                    mTempCurrentMajorProject = find.first().projectId
//                }
//            }
            if (find.size > 0) {
                (list.adapter as MajorProjectAdapter).setNewData(find)
            } else {
                (list.adapter as MajorProjectAdapter).setNewData(find)
                (list.adapter as MajorProjectAdapter).setEmptyView(R.layout.empty_view, list)
            }
            (list.adapter as MajorProjectAdapter).notifyDataSetChanged()
        }


        save.setOnClickListener {
            mCurrentMajorProject = mTempCurrentMajorProject
            loadData()
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)
        iv_back.setOnClickListener {
            finish()
        }
        val taskName = intent.getStringExtra("taskName")
        taskName?.let {
            practice_name.text = taskName
        }
        list.layoutManager = LinearLayoutManager(this)
        val majorProjectAdapter = MajorProjectAdapter()
        majorProjectAdapter.setOnItemClickListener { adapter, _, position ->
            if(mTempCurrentMajorProject.isNotEmpty()){
                mTempCurrentMajorProject = if( mTempCurrentMajorProject == (adapter.data[position] as Project).projectId){
                    ""
                } else {
                    (adapter.data[position] as Project).projectId
                }
                loadData()
            } else{
                mTempCurrentMajorProject = (adapter.data[position] as Project).projectId
                loadData()
            }

        }
        list.adapter = majorProjectAdapter
        (list.adapter as MajorProjectAdapter).setEmptyView(R.layout.empty_view, list)
    }

    override fun initListener() {
    }


    inner class MajorProjectAdapter :
        BaseQuickAdapter<Project, BaseViewHolder>(R.layout.recycler_select_major_project) {
        override fun convert(helper: BaseViewHolder, item: Project?) {
            val projectName = helper.getView<TextView>(R.id.project_name)
            val checkBox = helper.getView<CheckBox>(R.id.checkbox)


            item?.let {
                projectName.text = it.projectName
                checkBox.isChecked = item.projectId == mTempCurrentMajorProject
            }
        }
    }
}