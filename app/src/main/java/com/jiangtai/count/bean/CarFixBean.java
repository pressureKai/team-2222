package com.jiangtai.count.bean;

import android.util.Log;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class CarFixBean extends LitePalSupport {



    public CarFixBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID
    private String loginId;
    private String recordTime;
    private String recordID;
    private String wssj;
    private String wxbj;
    //维修对象 -> 装备编号
    private String wxdx;
    private String wxry;

    //装备类别
    private String wxlx;
    //装备名称
    private String zbmc;


    private String bz;

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getWXNR() {
        return WXNR;
    }

    public void setWXNR(String WXNR) {
        this.WXNR = WXNR;
    }

    //维修内容
    private String WXNR;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getWxlx() {
        return wxlx;
    }

    public void setWxlx(String wxlx) {
        this.wxlx = wxlx;
    }

    public String getZbmc() {
        return zbmc;
    }

    public void setZbmc(String zbmc) {
        this.zbmc = zbmc;
    }

//    public String getBZ() {
//        return BZ;
//    }
//
//    public void setBZ(String BZ) {
//        this.BZ = BZ;
//    }

    public String getWssj() {
        return wssj;
    }

    public void setWssj(String wssj) {
        this.wssj = wssj;
    }

    public String getWxbj() {
        return wxbj;
    }

    public void setWxbj(String wxbj) {
        this.wxbj = wxbj;
    }

    public String getWxdx() {
        return wxdx;
    }

    public void setWxdx(String wxdx) {
        this.wxdx = wxdx;
    }

    public String getWxry() {
        return wxry;
    }

    public void setWxry(String wxry) {
        this.wxry = wxry;
    }


    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public CountRecordBean save(Boolean isUpdate) {
        CountRecordBean countRecordBean = null;
        Log.e("CarFixBean","recordID " + recordID + " recordTime "+ recordTime);
        if(recordID.isEmpty() || recordTime.isEmpty()){
            String  recordTime = System.currentTimeMillis()+"";
            this.recordTime = recordTime;
            recordID = CountRecordBean.Companion.getCountId(recordTime);
        }
        if(!isUpdate){
            countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.CAR_FIX_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        wxry = this.loginId;


       // Log.e("CarFixBean","id  : "+ Long.parseLong(getID()));
        boolean isExist = false;
        try {
            CarFixBean carFixBean = LitePal.find(CarFixBean.class, getId());
            if(carFixBean != null){
                isExist = true;
            }
        }catch (Exception e){

        }


        if(!isExist){

            save();
        } else {
            Log.e("CarFixBean","update carFixBean : "+getId());
            update(getId());
        }


        return countRecordBean;
    }
}
