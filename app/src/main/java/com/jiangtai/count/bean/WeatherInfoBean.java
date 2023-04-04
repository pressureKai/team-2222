package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.LitePal;
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
    private String zyl;
    //低云量
    private String dyl;
    //SC云量
    private String scl;
    //SC云高
    private String scg;
    //Fn云量
    private String fnl;
    //Fn云高
    private String fng;
    //风向（度）
    private String fx;
    //风速
    private String fs;
    //能见度
    private String njd;
    //当前天气
    private String dqtq;
    //气温
    private String qw;
    //露点温度
    private String ldwd;
    //相对湿度
    private String xdsd;
    //水汽压
    private String sqy;
    //本站气压
    private String bzqy;
    //海平面气压
    private String hpmqy;
    //备注
    private String bz;

    private String gcrid;

    private String recordID;
    private String recordTime;


    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGcrid() {
        return gcrid;
    }

    public void setGcrid(String gcrid) {
        this.gcrid = gcrid;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getZyl() {
        return zyl;
    }

    public void setZyl(String zyl) {
        this.zyl = zyl;
    }

    public String getDyl() {
        return dyl;
    }

    public void setDyl(String dyl) {
        this.dyl = dyl;
    }

    public String getScl() {
        return scl;
    }

    public void setScl(String scl) {
        this.scl = scl;
    }

    public String getScg() {
        return scg;
    }

    public void setScg(String scg) {
        this.scg = scg;
    }

    public String getFnl() {
        return fnl;
    }

    public void setFnl(String fnl) {
        this.fnl = fnl;
    }

    public String getFng() {
        return fng;
    }

    public void setFng(String fng) {
        this.fng = fng;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public String getFs() {
        return fs;
    }

    public void setFs(String fs) {
        this.fs = fs;
    }

    public String getNjd() {
        return njd;
    }

    public void setNjd(String njd) {
        this.njd = njd;
    }

    public String getDqtq() {
        return dqtq;
    }

    public void setDqtq(String dqtq) {
        this.dqtq = dqtq;
    }

    public String getQw() {
        return qw;
    }

    public void setQw(String qw) {
        this.qw = qw;
    }

    public String getLdwd() {
        return ldwd;
    }

    public void setLdwd(String ldwd) {
        this.ldwd = ldwd;
    }

    public String getXdsd() {
        return xdsd;
    }

    public void setXdsd(String xdsd) {
        this.xdsd = xdsd;
    }

    public String getSqy() {
        return sqy;
    }

    public void setSqy(String sqy) {
        this.sqy = sqy;
    }

    public String getBzqy() {
        return bzqy;
    }

    public void setBzqy(String bzqy) {
        this.bzqy = bzqy;
    }

    public String getHpmqy() {
        return hpmqy;
    }

    public void setHpmqy(String hpmqy) {
        this.hpmqy = hpmqy;
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



    public CountRecordBean save(Boolean isUpdate) {
        CountRecordBean countRecordBean = null;
        if(recordID.isEmpty() || recordTime.isEmpty()){
            String  recordTime = System.currentTimeMillis()+"";
            this.recordTime = recordTime;
            recordID = CountRecordBean.Companion.getCountId(recordTime);
        }
        if(!isUpdate){
            countRecordBean = new CountRecordBean();
            countRecordBean.setRecordTime(this.recordTime);
            countRecordBean.setRecordType(CountRecordBean.WEATHER_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        gcrid = loginId;
        boolean isExist = false;
        try {
            WeatherInfoBean carFixBean = LitePal.find(WeatherInfoBean.class, getId());
            if(carFixBean != null){
                isExist = true;
            }
        }catch (Exception e){

        }


        if(!isExist){
            save();
        } else {
            update(getId());
        }
        return countRecordBean;
    }


}
