package com.jiangtai.team.bean

class BigDipperPersonVi {
    //任务id
    var taskid = ""
    //人员或设备的短id
    var shortid = ""
    //状态，normal正常，sosing求救，sosstarted开始救援，sosfinished救援完成，offline离线
    var status = "normal"
    //搜救类型，
    // 包含主动求救seekhelp、
    // 落水overboard、
    // 摔倒falldown、
    // 体征异常abnormalsigns,
    // watchstatus为正常时sostype为”“
    var sostype = ""
    //人员位置纬度(可不存在)
    var lat = "39.8575821"
    //人员位置经度(可不存在)
    var lng = "116.2889938"
    //人员定位时间
    var time = ""
    //设备唯一编号
    var deviceid = ""
    //心率
    var heartrate = ""
    //状态，normal正常，sosing求救，sosstarted开始救援，sosfinished救援完成，offline离线
    var devicestatus = "normal"
}