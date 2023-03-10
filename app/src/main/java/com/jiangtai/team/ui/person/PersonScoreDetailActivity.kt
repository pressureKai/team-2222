package com.jiangtai.team.ui.person

import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.Score
import kotlinx.android.synthetic.main.activity_person_score_detail.*
import org.litepal.LitePal

/**
 * Created by heCunCun on 2021/3/8
 */
class PersonScoreDetailActivity : BaseActivity() {
    private var list = mutableListOf<Score>()
    private val personScoreDetailAdapter by lazy {
        PersonScoreDetailAdapter()
    }

    override fun attachLayoutRes(): Int = R.layout.activity_person_score_detail
    private var personId = ""
    private var taskId = ""
    override fun initData() {
        personId = intent.getStringExtra("personId")
        taskId = intent.getStringExtra("taskId")
        val name = intent.getStringExtra("name")
        tv_name.text = "考生:$name"
        refreshData()
    }

    private fun refreshData() {
        Log.e("PersonActivity", "taskId=$taskId,personId=$personId")
        list = LitePal.where("taskId like ? and personId like ?", taskId, personId)
            .find(Score::class.java)
        if (list.isEmpty()) {
            personScoreDetailAdapter.setEmptyView(R.layout.empty_view, rv_score)
        } else {
            personScoreDetailAdapter.setNewData(list)
        }

    }

    override fun initView() {
        rv_score.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PersonScoreDetailActivity)
            isNestedScrollingEnabled = false
            adapter = personScoreDetailAdapter
        }
        initImmersionBar(view = toolbar, dark = false)
    }

    override fun initListener() {
        iv_back.setOnClickListener { finish() }
    }
}