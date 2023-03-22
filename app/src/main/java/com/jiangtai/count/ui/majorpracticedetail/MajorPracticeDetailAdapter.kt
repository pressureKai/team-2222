package com.jiangtai.count.ui.majorpracticedetail

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.Project
import com.jiangtai.count.bean.UploadMajorScoreData
import com.jiangtai.count.util.Preference
import de.hdodenhof.circleimageview.CircleImageView
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author xiezekai
 * @des 专项任务详情列表适配器
 * @create time 2021/8/4 5:59 下午
 */
class MajorPracticeDetailAdapter(
    dataList: ArrayList<Project>,
    private val majorPracticeClickListener: MajorPracticeDetailAdapterClickListener,
    private val isScore: Boolean? = false,
    private val personId: String? = ""

) :

    BaseMultiItemQuickAdapter<Project, BaseViewHolder>(dataList) {
    init {
        addItemType(0, R.layout.recycler_major_practice_detail_des)
        addItemType(1, R.layout.recycler_major_practice_detail)
    }

    private var isTaskName: String by Preference("isTaskName", "")
    override fun convert(helper: BaseViewHolder, item: Project) {
        when (helper.itemViewType) {
            0 -> {
                val des = helper.getView<TextView>(R.id.des)
                val is_over = helper.getView<TextView>(R.id.is_over)
                val is_line = helper.getView<View>(R.id.is_line)

                when (item.state) {
                    0 -> {
                        des.text = isTaskName
                        is_over.text = "任务未开始"
                        is_over.setTextColor(mContext.resources.getColor(R.color.white))
                    }
                    2 -> {
                        des.text = isTaskName
                        is_over.text = "任务已结束"
                        is_over.setTextColor(mContext.resources.getColor(R.color.color_blue_2979FF))
                    }
                    1 -> {
                        des.text = isTaskName
                        is_over.text = "任务进行中"
                        is_over.setTextColor(mContext.resources.getColor(R.color.color_blue_2979FF))
                    }
                }

                if (checkStateListIsEmpty(data as ArrayList<Project>, item.state)
                ) {
                    des.visibility = View.GONE
                    is_over.visibility = View.GONE
                    is_line.visibility = View.GONE
                }
            }
            1 -> {
                val taskState = helper.getView<CircleImageView>(R.id.task_state)
                val startProject = helper.getView<CircleImageView>(R.id.start_project)
                val startProject1 = helper.getView<ImageView>(R.id.start_project1)
                val moreInformation = helper.getView<CircleImageView>(R.id.more_information)
                val start_ll = helper.getView<LinearLayout>(R.id.start_ll)
                val taskName = helper.getView<TextView>(R.id.task_name)
                val startTime = helper.getView<TextView>(R.id.start_time)
                val endTime = helper.getView<TextView>(R.id.end_time)
                val taskContent = helper.getView<TextView>(R.id.task_content)
                val state = helper.getView<TextView>(R.id.state)
                val task_intent = helper.getView<LinearLayout>(R.id.task_intent)

                taskName.text = "任务名称:   " + item.projectName

                try {
                    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                    val date = Date(item.startTime)
                    startTime.text = "开始时间："+sdf.format(date)
                    endTime.text = "结束时间：" + sdf.format(Date(item.endTime))
                } catch (e: Exception) {
                    LogUtils.e("MajorPracticeAdapter parse error is $e")
                }



                taskContent.text = "任务内容:   " + item.projectContent

                when (item.state) {
                    // 0:未开始; 1:进行中；2:已结束；
                    1 -> {
                        Glide.with(mContext).load(R.mipmap.blue_dot_icon).into(taskState)
//                        state.text = "进行中"
//                        startProject.visibility = View.INVISIBLE
//                        state.visibility = View.INVISIBLE
                    }
                    0 -> {
                        Glide.with(mContext).load(R.mipmap.gray_dot_icon).into(taskState)
//                        startProject.visibility = View.VISIBLE
                        state.visibility = View.VISIBLE
//                        state.text = "点击下发"
                    }
                    2 -> {
                        Glide.with(mContext).load(R.mipmap.blue_dot_icon).into(taskState)
//                        startProject.visibility = View.INVISIBLE
//                        state.visibility = View.INVISIBLE
//                        state.text = "已结束"
                    }
                }



                if (isScore!!) {
//                    startProject.visibility = View.GONE
                    startProject1.visibility = View.GONE
                    moreInformation.visibility = View.GONE
                    state.visibility = View.VISIBLE


                    if (personId!!.isNotEmpty()) {
                        val times = getTimes(
                            personId!!.toUpperCase(),
                            item.projectId,
                            item.taskId
                        )

                        var startTimeText = "-- --"
                        var endTimeText = "-- ---"
                        try {
                            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
                            val date = Date(times[4].toLong())
                            val date1 = Date(times[5].toLong())
                            startTimeText = sdf.format(date).toString()
                            endTimeText = sdf.format(date1).toString()
                            startTime.text = "开始时间 $startTimeText"
                            endTime.text = "结束时间 $endTimeText"
//                            startProject.visibility = View.INVISIBLE
                            startProject1.visibility = View.INVISIBLE
                            state.text = "已结束"
                        } catch (e: Exception) {
                            var startTimeText = "-- --"
                            var endTimeText = "-- ---"
                            startTime.text = "开始时间 $startTimeText"
                            endTime.text = "结束时间 $endTimeText"
                        }
                    } else {
                        var startTimeText = "-- --"
                        var endTimeText = "-- ---"
                        startTime.text = "开始时间 $startTimeText"
                        endTime.text = "结束时间 $endTimeText"
                    }

                } else {
//                    state.visibility = View.GONE
                }

                startProject.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, true)
                }
                startProject1.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, true)
                }
                state.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, false)
                }

                task_intent.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, false)
                }

                moreInformation.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, false)
                }

                start_ll.setOnClickListener {
                    majorPracticeClickListener.onMajorPracticeAdapterClick(item, false)
                }

            }
        }
    }


    /**
     * @des 获取成绩时间
     * @time 2021/8/20 5:28 下午
     */
    private fun getTimes(
        personId: String,
        projectId: String,
        taskId: String
    ): java.util.ArrayList<String> {
        val arrayList = java.util.ArrayList<String>()

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

    private fun checkStateListIsEmpty(
        originList: ArrayList<Project>,
        targetState: Int
    ): Boolean {
        var stateIsEmpty = true
        for (value in originList) {
            if (!value.isDesItem && value.state == targetState) {
                stateIsEmpty = false
                break
            }
        }

        return stateIsEmpty
    }

    /**
     * @author xiezekai
     * @des   专项计划点击监听
     * @create time 2021/8/5 9:27 上午
     */
    interface MajorPracticeDetailAdapterClickListener {
        /**
         * @des 监听器内部点击事件监听
         * @time 2021/8/5 9:32 上午
         * @params item ：被点击的实体类 isStartProject ：是否点击开始启动计划
         */
        fun onMajorPracticeAdapterClick(item: Project, isStartProject: Boolean)
    }

}