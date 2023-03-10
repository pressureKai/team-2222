package com.jiangtai.team.bean;

import com.jiangtai.team.util.CommonUtil;

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

    private String SSBH;
    private String QWXX;
    private String WD;
    private String SJSJ;
    private String SSLB;
    private String BZ;


    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getSSBH() {
        return SSBH;
    }

    public void setSSBH(String SSBH) {
        this.SSBH = SSBH;
    }

    public String getQWXX() {
        return QWXX;
    }

    public void setQWXX(String QWXX) {
        this.QWXX = QWXX;
    }

    public String getWD() {
        return WD;
    }

    public void setWD(String WD) {
        this.WD = WD;
    }

    public String getSJSJ() {
        return SJSJ;
    }

    public void setSJSJ(String SJSJ) {
        this.SJSJ = SJSJ;
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


    public String getSSLB() {
        return SSLB;
    }

    public void setSSLB(String SSLB) {
        this.SSLB = SSLB;
    }

    @Override
    public boolean save() {
        CountRecordBean countRecordBean = new CountRecordBean();
        countRecordBean.setRecordTime(this.recordTime);
        countRecordBean.setRecordType(CountRecordBean.DEVICE_FILL_TYPE);
        countRecordBean.setRecordID(this.recordID);
        countRecordBean.save();
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
