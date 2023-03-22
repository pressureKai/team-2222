package com.jiangtai.count.ui.person

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.Person
import kotlinx.android.synthetic.main.activity_person_list.*
import org.litepal.LitePal


class PersonListActivity : BaseActivity() {
    private var list = mutableListOf<Person>()
    private val personAdapter by lazy {
        PersonAdapter()
    }

    override fun attachLayoutRes(): Int = R.layout.activity_person_list
    private var taskId = ""
    override fun initData() {
        taskId = intent.getStringExtra("taskId")
        refreshData()
    }

    private fun refreshData() {
        list = LitePal.where("taskId=?", taskId).find(Person::class.java)
        personAdapter.setNewData(list)
    }

    override fun initView() {
        initImmersionBar(view = toolbar,dark = false)
        rv_person.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PersonListActivity)
            isNestedScrollingEnabled = false
            adapter = personAdapter
        }
    }

    override fun initListener() {
        iv_back.setOnClickListener { finish() }
        personAdapter.setOnItemChildClickListener { _, _, position ->
            val intent = Intent(this, PersonScoreDetailActivity::class.java)
            intent.putExtra("personId",list[position].personId)
            intent.putExtra("name",list[position].name)
            intent.putExtra("taskId",taskId)
            startActivity(intent)
        }
    }

}