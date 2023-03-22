package com.jiangtai.count.bean

class DipperBig {
    //确认代码指示personlocationindication/personsosindication
    var cmd = "personlocationindication"
    //业务平台分配给北斗通信平台的会话ID
    var sessionidbd = "1"
    //项目ID
    var registerid = "1"
    //手持机唯一编号(MAC地址)
    var handsetnumber = "0000000"
    //手持机位置纬度(可不存在)
    var lat = "39.8575821"
    //手持机位置经度(可不存在)
    var lng = "116.2889938"
    //手持机位置记录时间(可不存在)
    var time = ""
    //人员位置列表，列表中未上报的手表（信标）为离线状态，
    var taskpersonloclist :ArrayList<BigDipperPersonVi> = ArrayList()
    //求救设备位置
    var soslist :ArrayList<BigDipperPersonVi> = ArrayList()
}