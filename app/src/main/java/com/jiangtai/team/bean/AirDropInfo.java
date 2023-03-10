package com.jiangtai.team.bean;

import com.jiangtai.team.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class AirDropInfo extends LitePalSupport {


    public AirDropInfo(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID
    private String loginId;

    //人员ID(当前登录用户的人员ID)
    private String RYID;
    //物资编号
    private String WZBH;
    //物资接收状态（接收/未接收）
    private String WZJSZT;
    //数据时间
    private String SJSJ;
    //物资名称
    private String WZMC;
    //物资备注
    private String WZBZ;
    //记录ID
    private String recordID;
    //记录时间
    private String recordTime;


    public String getWZMC() {
        return WZMC;
    }

    public void setWZMC(String WZMC) {
        this.WZMC = WZMC;
    }

    public String getWZBZ() {
        return WZBZ;
    }

    public void setWZBZ(String WZBZ) {
        this.WZBZ = WZBZ;
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

    public String getRYID() {
        return RYID;
    }

    public void setRYID(String RYID) {
        this.RYID = RYID;
    }

    public String getWZBH() {
        return WZBH;
    }

    public void setWZBH(String WZBH) {
        this.WZBH = WZBH;
    }

    public String getWZJSZT() {
        return WZJSZT;
    }

    public void setWZJSZT(String WZJSZT) {
        this.WZJSZT = WZJSZT;
    }

    public String getSJSJ() {
        return SJSJ;
    }

    public void setSJSJ(String SJSJ) {
        this.SJSJ = SJSJ;
    }


    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @Override
    public boolean save() {
        CountRecordBean countRecordBean = new CountRecordBean();
        countRecordBean.setRecordTime(this.recordTime);
        countRecordBean.setRecordType(CountRecordBean.AIR_TYPE);
        countRecordBean.setRecordID(this.recordID);
        countRecordBean.save();
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
