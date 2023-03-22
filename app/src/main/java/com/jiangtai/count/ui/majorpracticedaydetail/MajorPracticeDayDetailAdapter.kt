package com.jiangtai.count.ui.majorpracticedaydetail

import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.ui.returnDate.ReturnDate
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

class MajorPracticeDayDetailAdapter(private val projectId: String) :
    BaseQuickAdapter<Person, BaseViewHolder>(R.layout.recycler_major_practice_day_detail) {
    override fun convert(helper: BaseViewHolder, item: Person?) {
        //人员名称
        val personName = helper.getView<TextView>(R.id.person_name)

        val intentstart = helper.getView<LinearLayout>(R.id.intentstart)
        //任务状态
        val taskState = helper.getView<TextView>(R.id.task_state)
        //项目名称
        val projectName = helper.getView<TextView>(R.id.project_name)
        //开始时间
        val startTime = helper.getView<TextView>(R.id.start_time)
        //结束时间
        val endTime = helper.getView<TextView>(R.id.end_time)


        val is_head = helper.getView<TextView>(R.id.is_head)

        //开始时间布局
        val startTimeLayout = helper.getView<LinearLayout>(R.id.start_time_layout)
        //结束时间布局
        val endTimeLayout = helper.getView<LinearLayout>(R.id.end_time_layout)

        item?.let {
            personName.text = item.name

         //   projectName.text = item.getContext(projectId)
            projectName.text =  item.name
            val projectTime = item.getProjectTime(projectId)

            if (projectTime.size > 0) {
                val times = getTimes(
                    item.personId.toUpperCase(),
                    projectId,
                    item.taskId
                )


                var startTimeText = "-- --"
                var endTimeText = "-- ---"

                var isreturnhead = "0"


                try {
                    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                    val date = Date(times[0].toLong())
                    val date1 = Date(times[1].toLong())
                    startTimeText = sdf.format(date).toString()
                    endTimeText = sdf.format(date1).toString()
                    val locationList =  LitePal.findAll(LocationReceiver::class.java)
                    for ((lacat,e) in locationList.withIndex()){
                        if (item.personId.equals(e.userId)){
                            if (projectId.equals(e.projectId))
                                isreturnhead = e.heartRate



                        }
                    }




                } catch (e: Exception) {
                    LogUtils.e("MajorPracticeItemDetailAdapter parse error is $e")
                }
//                when (getTaskState(times[0],times[1])) {
//                    0 -> {
//                        //未开始
//                        taskState.text = "未开始"
//                    }
//                    1 -> {
//                        //进行中
//                        taskState.text = "进行中"
//                    }
//                    2 -> {
//                        //已结束
//                        taskState.text = "已结束"
//                    }
//                }


                val find = LitePal.where("projectId = ?", projectId).find(Project::class.java)
                intentstart.setOnClickListener{
                    val intent =
                        Intent(mContext, ReturnDate::class.java)
                    intent.putExtra("projectId", projectId)
                    intent.putExtra("name", item.name)
                    intent.putExtra("personId", item.personId)
                    mContext.startActivity(intent)
                }
                if (find.isNotEmpty()) {
                    val last = find.last()
                    try {
                        val taskState1 = getTaskState(last.startTime, last.endTime)
                        when (taskState1) {
                            // 0:未开始; 1:进行中；2:已结束；
                            1 -> {
                                taskState.text = "进行中"
                                if (startTimeText != "-- --") {
                                    taskState.text = "已结束"
                                }
                            }
                            0 -> {
                                taskState.text = "未开始"
                                if (startTimeText != "-- --") {
                                    taskState.text = "已结束"
                                }
                            }
                            2 -> {
                                taskState.text = "已结束"
                            }
                        }
                    } catch (e: Exception) {

                    }

                }


                startTime.text = " $startTimeText"
                endTime.text = " $endTimeText"

                is_head.text="$isreturnhead"


//                    val intent = mContext.Intent(this, MapActivity::class.java)
//                    intent.putExtra("name", person.name)
//                    intent.putExtra("personId", person.personId)
//                    intent.putExtra("projectId", projectId)
//                    intent.putExtra("taskId", taskId)
//                    startActivity(intent)

            }

        }
    }


    private fun getTaskState(startTime: String, endTime: String): Int {
        var state = Constant.STATE_PRE
        state = if (startTime == "-- --" || endTime == "-- --") {
            Constant.STATE_PRE
        } else {
            Constant.STATE_FINISHED
        }

        return state
    }


    /**
     * @des 获取成绩时间
     * @time 2021/8/20 5:28 下午
     */
    private fun getTimes(
        personId: String,
        projectId: String,
        taskId: String
    ): ArrayList<String> {
        val arrayList = ArrayList<String>()


        try {
            val find = LitePal.where(
                "personId = ? and taskId = ? and projectId = ?",
                personId,
                taskId.replace(".0", "").trim(),
                projectId
            ).find(UploadMajorScoreData::class.java)
            if (find.isNotEmpty()) {
                arrayList.add(find.last().startTime)
                arrayList.add(find.last().endTime)
            } else {
                arrayList.clear()
                arrayList.add("-- --")
                arrayList.add("-- --")
            }
        } catch (e: java.lang.Exception) {
            arrayList.add("-- --")
            arrayList.add("-- --")
        }

        return arrayList
    }

    private fun getTaskState(startTime: Long, endTime: Long): Int {
        var state = Constant.STATE_PRE
        val nowTime = System.currentTimeMillis()
        if (nowTime in (startTime + 1) until endTime) {
            // 现在时间处于开始时间以及结束时间之间
            state = Constant.STATE_RUNNING
        } else {
            if (nowTime < startTime) {
                state = Constant.STATE_PRE
            } else if (nowTime > endTime) {
                state = Constant.STATE_FINISHED
            }
        }

        return state
    }
}