package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class WeatherInfoBean extends LitePalSupport {

    public WeatherInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;
    //总云量
    private String ZYL;
    //低云量
    private String DYL;
    //SC云量
    private String SCL;
    //SC云高
    private String SCG;
    //Fn云量
    private String FNL;
    //Fn云高
    private String FNG;
    //风向（度）
    private String FX;
    //风速
    private String FS;
    //能见度
    private String NJD;
    //当前天气
    private String DQTQ;
    //气温
    private String QW;
    //露点温度
    private String LDWD;
    //相对湿度
    private String XDSD;
    //水汽压
    private String SQY;
    //本站气压
    private String BZQY;
    //海平面气压
    private String HPMQY;
    //备注
    private String BZ;

    private String recordID;
    private String recordTime;


    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getZYL() {
        return ZYL;
    }

    public void setZYL(String ZYL) {
        this.ZYL = ZYL;
    }

    public String getDYL() {
        return DYL;
    }

    public void setDYL(String DYL) {
        this.DYL = DYL;
    }

    public String getSCL() {
        return SCL;
    }

    public void setSCL(String SCL) {
        this.SCL = SCL;
    }

    public String getSCG() {
        return SCG;
    }

    public void setSCG(String SCG) {
        this.SCG = SCG;
    }

    public String getFNL() {
        return FNL;
    }

    public void setFNL(String FNL) {
        this.FNL = FNL;
    }

    public String getFNG() {
        return FNG;
    }

    public void setFNG(String FNG) {
        this.FNG = FNG;
    }

    public String getFX() {
        return FX;
    }

    public void setFX(String FX) {
        this.FX = FX;
    }

    public String getFS() {
        return FS;
    }

    public void setFS(String FS) {
        this.FS = FS;
    }

    public String getNJD() {
        return NJD;
    }

    public void setNJD(String NJD) {
        this.NJD = NJD;
    }

    public String getDQTQ() {
        return DQTQ;
    }

    public void setDQTQ(String DQTQ) {
        this.DQTQ = DQTQ;
    }

    public String getQW() {
        return QW;
    }

    public void setQW(String QW) {
        this.QW = QW;
    }

    public String getLDWD() {
        return LDWD;
    }

    public void setLDWD(String LDWD) {
        this.LDWD = LDWD;
    }

    public String getXDSD() {
        return XDSD;
    }

    public void setXDSD(String XDSD) {
        this.XDSD = XDSD;
    }

    public String getSQY() {
        return SQY;
    }

    public void setSQY(String SQY) {
        this.SQY = SQY;
    }

    public String getBZQY() {
        return BZQY;
    }

    public void setBZQY(String BZQY) {
        this.BZQY = BZQY;
    }

    public String getHPMQY() {
        return HPMQY;
    }

    public void setHPMQY(String HPMQY) {
        this.HPMQY = HPMQY;
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



    public boolean save(Boolean isUpdate) {
        if(!isUpdate){
            CountRecordBean countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.WEATHER_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }


}
