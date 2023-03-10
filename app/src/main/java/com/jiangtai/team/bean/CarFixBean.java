package com.jiangtai.team.bean;

import com.jiangtai.team.util.CommonUtil;

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
    private String WXSJ;
    private String WXPJ;
    //维修对象 -> 装备编号
    private String WXDX;
    private String WXRY;
    private String ZBLB;
    private String ZBMC;
    private String BZ;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getZBLB() {
        return ZBLB;
    }

    public void setZBLB(String ZBLB) {
        this.ZBLB = ZBLB;
    }

    public String getZBMC() {
        return ZBMC;
    }

    public void setZBMC(String ZBMC) {
        this.ZBMC = ZBMC;
    }

    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getWXSJ() {
        return WXSJ;
    }

    public void setWXSJ(String WXSJ) {
        this.WXSJ = WXSJ;
    }

    public String getWXPJ() {
        return WXPJ;
    }

    public void setWXPJ(String WXPJ) {
        this.WXPJ = WXPJ;
    }

    public String getWXDX() {
        return WXDX;
    }

    public void setWXDX(String WXDX) {
        this.WXDX = WXDX;
    }

    public String getWXRY() {
        return WXRY;
    }

    public void setWXRY(String WXRY) {
        this.WXRY = WXRY;
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

    @Override
    public boolean save() {
        CountRecordBean countRecordBean = new CountRecordBean();
        countRecordBean.setRecordTime(this.recordTime);
        countRecordBean.setRecordType(CountRecordBean.CAR_FIX_TYPE);
        countRecordBean.setRecordID(this.recordID);
        countRecordBean.save();
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
