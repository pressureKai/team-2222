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
    private String VID;
    //装备类别
    private String ZBLB;
    //装备维修信息
    private String ZBWXXX;
    //累计摩托小时/飞行小时
    private String MTXS;
    //余油量
    private String YYL;
    //任务状态（待命、在执行）
    private String RWZT;
    //完好（正常、损坏）
    private String GZBJ;
    //备注
    private String BZ;


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

    public String getVID() {
        return VID;
    }

    public void setVID(String VID) {
        this.VID = VID;
    }

    public String getZBLB() {
        return ZBLB;
    }

    public void setZBLB(String ZBLB) {
        this.ZBLB = ZBLB;
    }

    public String getZBWXXX() {
        return ZBWXXX;
    }

    public void setZBWXXX(String ZBWXXX) {
        this.ZBWXXX = ZBWXXX;
    }

    public String getMTXS() {
        return MTXS;
    }

    public void setMTXS(String MTXS) {
        this.MTXS = MTXS;
    }

    public String getYYL() {
        return YYL;
    }

    public void setYYL(String YYL) {
        this.YYL = YYL;
    }

    public String getRWZT() {
        return RWZT;
    }

    public void setRWZT(String RWZT) {
        this.RWZT = RWZT;
    }

    public String getGZBJ() {
        return GZBJ;
    }

    public void setGZBJ(String GZBJ) {
        this.GZBJ = GZBJ;
    }


    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
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
