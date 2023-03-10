package com.jiangtai.team.response
import com.google.gson.annotations.SerializedName


/**
 * Created by heCunCun on 2021/3/24
 */
data class TaskListModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error_code")
    val errorCode: String,
    @SerializedName("error_msg")
    val errorMsg: String
)

data class Data(
    @SerializedName("aPageNo")
    val aPageNo: Int,
    @SerializedName("aPageSize")
    val aPageSize: Int,
    @SerializedName("beginDateStr")
    val beginDateStr: Any,
    @SerializedName("cabStatus")
    val cabStatus: Any,
    @SerializedName("cabinets")
    val cabinets: Any,
    @SerializedName("_class")
    val classX: Any,
    @SerializedName("createBy")
    val createBy: String,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("createDateStr")
    val createDateStr: String,
    @SerializedName("createDateStr2")
    val createDateStr2: String,
    @SerializedName("createOrgId")
    val createOrgId: Any,
    @SerializedName("createUser")
    val createUser: Any,
    @SerializedName("customCreateByStr")
    val customCreateByStr: Any,
    @SerializedName("dbType")
    val dbType: String,
    @SerializedName("dwid")
    val dwid: Any,
    @SerializedName("dwmc")
    val dwmc: Any,
    @SerializedName("endDateStr")
    val endDateStr: Any,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("handStatus")
    val handStatus: Any,
    @SerializedName("id")
    val id: Any,
    @SerializedName("isNewId")
    val isNewId: Boolean,
    @SerializedName("limitBy")
    val limitBy: Any,
    @SerializedName("orderBy")
    val orderBy: Any,
    @SerializedName("orgId")
    val orgId: Any,
    @SerializedName("orgNm")
    val orgNm: Any,
    @SerializedName("orgNoQuery")
    val orgNoQuery: Any,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("personCount")
    val personCount: Int,
    @SerializedName("personIds")
    val personIds: Any,
    @SerializedName("persons")
    val persons: Any,
    @SerializedName("pullStatus")
    val pullStatus: Any,
    @SerializedName("pushDate")
    val pushDate: Any,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("subjectIds")
    val subjectIds: String,
    @SerializedName("subjectNms")
    val subjectNms: String,
    @SerializedName("subjects")
    val subjects: List<Subject>,
    @SerializedName("taskEndTm")
    val taskEndTm: String,
    @SerializedName("taskEndTmStr")
    val taskEndTmStr: String,
    @SerializedName("taskId")
    val taskId: String,
    @SerializedName("taskName")
    val taskName: String,
    @SerializedName("taskNm")
    val taskNm: String,
    @SerializedName("taskStartTm")
    val taskStartTm: String,
    @SerializedName("taskStartTmStr")
    val taskStartTmStr: String,
    @SerializedName("updateBy")
    val updateBy: String,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("updateDateStr")
    val updateDateStr: String,
    @SerializedName("updateDateStr2")
    val updateDateStr2: String,
    @SerializedName("updateUser")
    val updateUser: Any
)

data class Subject(
    @SerializedName("subjectId")
    val subjectId: String,
    @SerializedName("subjectNm")
    val subjectNm: String
)