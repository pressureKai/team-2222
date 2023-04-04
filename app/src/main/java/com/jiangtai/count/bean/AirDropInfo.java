package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

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
    private String ryid;
    //物资编号
    private String wzbh;
    //物资接收状态（接收/未接收）
    private String wzjszt;
    //数据时间
    private String sjsj;
    //物资名称
    private String wzmc;
    //物资备注
    private String wzbz;
    //记录ID
    private String recordID;
    //记录时间
    private String recordTime;


    public String getWzmc() {
        return wzmc;
    }

    public void setWzmc(String wzmc) {
        this.wzmc = wzmc;
    }

    public String getWzbz() {
        return wzbz;
    }

    public void setWzbz(String wzbz) {
        this.wzbz = wzbz;
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

    public String getRyid() {
        return ryid;
    }

    public void setRyid(String ryid) {
        this.ryid = ryid;
    }

    public String getWzbh() {
        return wzbh;
    }

    public void setWzbh(String wzbh) {
        this.wzbh = wzbh;
    }

    public String getWzjszt() {
        return wzjszt;
    }

    public void setWzjszt(String wzjszt) {
        this.wzjszt = wzjszt;
    }

    public String getSjsj() {
        return sjsj;
    }

    public void setSjsj(String sjsj) {
        this.sjsj = sjsj;
    }


    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public boolean save(Boolean isUpdate) {
        if(!isUpdate){
            CountRecordBean countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.AIR_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
