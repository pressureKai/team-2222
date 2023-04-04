package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class DeviceFeelInfoBean  extends LitePalSupport {



    public DeviceFeelInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;
    private String recordID;
    private String recordTime;

    private String ssbh;
    private String qwxx;
    private String wd;
    private String sjsj;
    private String sslb;
    private String bz;


    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getSsbh() {
        return ssbh;
    }

    public void setSsbh(String ssbh) {
        this.ssbh = ssbh;
    }

    public String getQwxx() {
        return qwxx;
    }

    public void setQwxx(String qwxx) {
        this.qwxx = qwxx;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getSjsj() {
        return sjsj;
    }

    public void setSjsj(String sjsj) {
        this.sjsj = sjsj;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }


    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }


    public String getSslb() {
        return sslb;
    }

    public void setSslb(String sslb) {
        this.sslb = sslb;
    }

    public boolean save(Boolean isUpdate) {
        if(!isUpdate){
            CountRecordBean countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.DEVICE_FILL_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
