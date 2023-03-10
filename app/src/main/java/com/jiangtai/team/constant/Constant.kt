package com.jiangtai.team.constant

/**
 * Created by heCunCun on 2019/12/5
 */
object Constant{
    const val SUCCESSED_CODE="0"
    const val BASE_URL="http://3oi2580368.goho.co"
    const val STATE_PRE = 0
    const val STATE_RUNNING = 1
    const val STATE_FINISHED = 2
    const val PHONE_ID ="phone_id"
    const val START_ID = "start_id"
    const val SERVER_IP ="server_ip"
    const val PHONE_IP ="phone_ip"
    const val LAST_REQUEST_TIME = "last_request_time"

    const val CURRENT_TASK_ID = "current_task_id"
    const val CHANNEL_NUM ="channel_num"

    const val SOS_ID = "sos_id"
    const val ELE_MODE = "ele_mode"
    const val ELE_DISTANCE = "ele_distance"
    const val ELE_TIME = "ele_time"
    const val ELE_START_TIME = "ele_start_time"
    const val MQTT_ID = "mqtt_id"
    const val MQTT_URL = "mqtt_url"
    const val DEVICE_SN = "device_sn"


    const val LAT_CENTER = "lat_center"
    const val LNG_CENTER = "lng_center"
    const val LAST_COMMIT_TIME = "last_commit_time"


    const val BEI_DOU_CHANNEL = "BEI_DOU_CHANNEL"

    //北斗平台上的最小已发送待确认消息ID(已收ID下发中的minSentId)
    const val MIN_CONFIRM_ID = "min_confirm_id"
    //北斗平台上的最大已发送待确认消息ID(已收ID下发中的maxSentId)
    const val MAX_CONFIRM_ID = "max_confirm_id"


    const val RECEIVED_ID_DOWNLOAD = 11
    const val RECEIVED_ID_UPLOAD = 10

    const val CURRENT_MAJOR_PROJECT = "current_major_project"
    const val START_CHECK_DISTANCE = "start_check_distance"
    const val TEMP_CURRENT_MAJOR_PROJECT = "temp_current_major_project"
    const val LAST_SEND_TIME = "last_send_time"
    const val LAST_SEND_COMPLETE = "last_send_complete"

    //手持机向北斗平台发送已收ID上报（手持机发送的最小已发送待确认的ID minSentID）可改变下一次已收ID下发的StartID
    //改变时机为 (北斗平台的已收ID下发中的startID)
    const val SORT_START_ID = "sort_start_id"

    const val RevBDAddress = "_RevBDAddress"
    const val RevBDInfo = "_RevBDInfo"
    const val RevBDType = "_RevBDType"


    const val SIG_RET_INFO_0 = "SignalInfo0"
    const val SIG_RET_INFO_1 = "SignalInfo1"
    const val SIG_RET_INFO_2 = "SignalInfo2"
    const val SIG_RET_INFO_3 = "SignalInfo3"
    const val SIG_RET_INFO_4 = "SignalInfo4"
    const val SIG_RET_INFO_5 = "SignalInfo5"
    const val SIG_RET_INFO_6 = "SignalInfo6"
    const val SIG_RET_INFO_7 = "SignalInfo7"
    const val SIG_RET_INFO_8 = "SignalInfo8"
    const val SIG_RET_INFO_9 = "SignalInfo9"




    const val SIGN_TYPE = "sign_type"
    const val SIGN_TIME = "sign_time"
    const val SIGN_PHONE_ID = "sign_phone_id"
    const val SIGN_TASK_ID = "sign_task_id"
    const val SIGN_START = "sign_start"
    const val UPLOAD_DATA = "upload_data"

    const val LOGIN_USER_NAME = "login_user_name"
    const val LOGIN_USER_ID = "login_user_id"
    const val LOGIN_USER_PART = "login_user_part"
    const val LOGIN_USER_TYPE = "login_user_type"


    val SIG_INFO = arrayOf<String>(
        SIG_RET_INFO_0,
        SIG_RET_INFO_1,
        SIG_RET_INFO_2,
        SIG_RET_INFO_3,
        SIG_RET_INFO_4,
        SIG_RET_INFO_5,
        SIG_RET_INFO_6,
        SIG_RET_INFO_7,
        SIG_RET_INFO_8,
        SIG_RET_INFO_9
    )


    const val SIG_RET = "strSignalInfoChanged"

    const val IS_SOS = true
    const val SERVER = 100
    const val LOC=101
}