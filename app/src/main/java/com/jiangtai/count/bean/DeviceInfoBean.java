package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class DeviceInfoBean  extends LitePalSupport {


    public DeviceInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;
    //装备实体ID
    private String vid;
    //装备类别
    private String zblb;
    //装备维修信息
    private String zbwxxx;
    //累计摩托小时/飞行小时
    private String mtxs;
    //余油量
    private String yyl;
    //任务状态（待命、在执行）
    private String rwzt;
    //完好（正常、损坏）
    private String gzbj;
    //备注
    private String bz;


    private String recordID;
    private String recordTime;


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

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getZblb() {
        return zblb;
    }

    public void setZblb(String zblb) {
        this.zblb = zblb;
    }

    public String getZbwxxx() {
        return zbwxxx;
    }

    public void setZbwxxx(String zbwxxx) {
        this.zbwxxx = zbwxxx;
    }

    public String getMtxs() {
        return mtxs;
    }

    public void setMtxs(String mtxs) {
        this.mtxs = mtxs;
    }

    public String getYyl() {
        return yyl;
    }

    public void setYyl(String yyl) {
        this.yyl = yyl;
    }

    public String getRwzt() {
        return rwzt;
    }

    public void setRwzt(String rwzt) {
        this.rwzt = rwzt;
    }

    public String getGzbj() {
        return gzbj;
    }

    public void setGzbj(String gzbj) {
        this.gzbj = gzbj;
    }


    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public boolean save(Boolean isUpdate) {
        if(!isUpdate){
            CountRecordBean countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.DEVICE_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
