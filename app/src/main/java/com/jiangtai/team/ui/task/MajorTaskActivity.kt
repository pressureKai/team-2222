package com.jiangtai.team.ui.task

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.TaskBean
import kotlinx.android.synthetic.main.activity_major_task.*
import org.litepal.LitePal

class MajorTaskActivity : BaseActivity() {
    override fun attachLayoutRes(): Int {
        return R.layout.activity_major_task
    }

    override fun initData() {
     //   val find = LitePal.where("isMajor=?", "1").find(TaskBean::class.java)
        val find =  LitePal.findAll(TaskBean::class.java)
        if(find.size > 0){
            (list.adapter as MajorSelectAdapter).setNewData(find)
        } else {
            (list.adapter as MajorSelectAdapter).setNewData(find)
            (list.adapter as MajorSelectAdapter).setEmptyView(R.layout.empty_view, list)
        }
        (list.adapter as MajorSelectAdapter).notifyDataSetChanged()

    }

    override fun initView() {
        initImmersionBar(view = toolbar, dark = false)
        iv_back.setOnClickListener {
            finish()
        }
        list.layoutManager = LinearLayoutManager(this)
        val majorSelectAdapter = MajorSelectAdapter()
        majorSelectAdapter.setOnItemClickListener { adapter, _, position ->
            val intent = Intent(this, MajorProjectActivity::class.java)
            try {
                val taskId = (adapter.data[position] as TaskBean).taskId
                val taskName = (adapter.data[position] as TaskBean).taskName
                intent.putExtra("taskName",taskName)
                intent.putExtra("taskId",taskId)
            }catch (e:Exception){

            }
            startActivityForResult(intent,0x03)
        }
        list.adapter = majorSelectAdapter
    }

    override fun initListener() {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0x03){
            if(resultCode == RESULT_OK){
                finish()
            }
        }
    }

    inner class MajorSelectAdapter :
        BaseQuickAdapter<TaskBean, BaseViewHolder>(R.layout.recycler_select_major_task) {
        override fun convert(helper: BaseViewHolder, item: TaskBean?) {
            val taskName = helper.getView<TextView>(R.id.task_name)
            item?.let {
                taskName.text = item.taskName
            }
        }

    }
}