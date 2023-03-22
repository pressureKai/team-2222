package com.jiangtai.count.bean

import com.blankj.utilcode.util.LogUtils
import com.jiangtai.count.constant.Constant
import org.litepal.LitePal
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/10
 * 考核人员表
 */
data class Person(
    //原表数据
    var gradeName: String = "",
    var gradeId: String = "",
    var name: String = "",
    var personId: String = "",
    var sex: String = "",
    var watchId: String = "",
    //自定义数据
    var updateTime: Long = 0,//成绩更新时间
    var state: Int = Constant.STATE_PRE,
    var taskId: String = "",//taskId
    @Column(ignore = true)
    var score: String = "",
    @Column(ignore = true)
    var projectId: String = "",//科目id
    var isMajor: String = "0",
    var takeDevice :String= "0",
    //本地获取设备列表，列表ID以逗号分隔
    var deviceListLocal :String = "",
    //从服务器获取的json，方便转换为Bean
    @Column(ignore = true)
    var deviceList:ArrayList<String> = ArrayList()
) : LitePalSupport() {
    val id: Long = 1


    fun mineSave() {
        val find = LitePal.where(
            "taskId =? and personId=? and isMajor = ?",
            this.taskId,
            this.personId,
            this.isMajor.toString()
        )
            .find(Person::class.java)
        if (find.size > 0) {
            return
        } else {
            save()
        }
    }

    fun getContext(projectId: String): String {
        var proContext = ""
        if (projectId.isEmpty()) {
            return proContext
        }

        val find = LitePal.where("projectId =?",projectId)
            .find(Project::class.java)

        if (find.size > 0) {
            proContext = find.last().projectContent
        }


        LogUtils.e("person projectId is $projectId $proContext")

        return proContext
    }


    /**
     * @des 获取项目名称
     * @time 2021/8/10 9:31 上午
     */
    fun getProjectName(projectId: String): String {
        var projectName = ""
        if (projectId.isEmpty()) {
            return projectName
        }

        val find = LitePal.where("projectId =?",projectId)
            .find(Project::class.java)

        if (find.size > 0) {
            projectName = find.last().projectName
        }


        LogUtils.e("person projectId is $projectId $projectName")

        return projectName
    }


    /**
     * @des 获取项目名称
     * @time 2021/8/10 9:31 上午
     */
    fun getProjectTime(projectId: String): ArrayList<Long> {
        val timeList: ArrayList<Long> = ArrayList()
        if (projectId.isEmpty()) {
            return timeList
        }

        val find = LitePal.where("projectId =?", projectId)
            .find(Project::class.java)

        if (find.size > 0) {
            timeList.add(find.last().startTime)
            timeList.add(find.last().endTime)

            LogUtils.e("person projectId is $projectId ${timeList[0]}")
            LogUtils.e("person projectId is $projectId ${timeList[1]}")
        }



        return timeList
    }
}