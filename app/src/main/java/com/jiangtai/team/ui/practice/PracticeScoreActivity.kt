package com.jiangtai.team.ui.practice

import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseActivity
import com.jiangtai.team.bean.NormalScore
import com.jiangtai.team.event.RefreshPracticeEvent
import kotlinx.android.synthetic.main.activity_practice_score.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import kotlin.math.abs

/**
 * Created by heCunCun on 2021/3/9
 */
class PracticeScoreActivity : BaseActivity() {
    override fun useEventBus(): Boolean =true
    private var list = mutableListOf<NormalScore>()
    private val practiceScoreAdapter by lazy {
        PracticeScoreAdapter(object :PracticeScoreAdapter.OnSlideClickListener{
            override fun onClick(position: Int, type: Int) {
                if(type == 1){
                    if(position < list.size){
                        list[position].delete()
                    }
                    initData()
                }
            }
        })
    }

    override fun attachLayoutRes(): Int = R.layout.activity_practice_score
    private var personId = ""
    private var name = ""
    override fun initData() {
        personId = intent.getStringExtra("personId")
        name = intent.getStringExtra("name")
        tv_name.text = "$name(ID:$personId)"
        refreshData()
    }

    private fun refreshData() {
        list = LitePal.where("personId=?", personId).find(NormalScore::class.java)
        if (list.isEmpty()) {
            list.clear()
            practiceScoreAdapter.setNewData(list)
            practiceScoreAdapter.notifyDataSetChanged()
            practiceScoreAdapter.setEmptyView(R.layout.empty_view, rv_score)
        } else {
            practiceScoreAdapter.setNewData(list)
            practiceScoreAdapter.notifyDataSetChanged()
        }

    }

    override fun initView() {
        rv_score.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PracticeScoreActivity)
            isNestedScrollingEnabled = false
            adapter = practiceScoreAdapter
        }
    }

    override fun initListener() {
        iv_back.setOnClickListener { finish() }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPractice(e: RefreshPracticeEvent) {
        refreshData()
    }


    var firstTouchX = 0f
    var firstTouchY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var dispatch = true
        ev?.let {
            when(it.action){
                MotionEvent.ACTION_DOWN ->{
                    firstTouchX = it.rawX
                    firstTouchY = it.rawY
                }
                MotionEvent.ACTION_MOVE ->{
                    dispatch = abs(it.rawY - firstTouchY) <= abs(it.rawX - firstTouchX)
                }
                MotionEvent.ACTION_UP->{

                }
                else -> {

                }
            }
        }

        return if(!dispatch){
            rv_score.dispatchTouchEvent(ev)
            false
        } else {
            super.dispatchTouchEvent(ev)
        }
    }
}