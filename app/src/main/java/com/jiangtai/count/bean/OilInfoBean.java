package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class OilInfoBean  extends LitePalSupport {



    public OilInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;


    private int id;

    //罐号
    private String gh;
    //油品名称
    private String ypmc;
    //油高
    private String yg;
    //容积表号
    private String rjbh;
    //视密度
    private String smd;
    //油温
    private String yw;
    //测量时间
    private String sjsj;
    //备注
    private String bz;


    private String CLRID;
    private String YTJ;

    public String getCLRID() {
        return CLRID;
    }

    public void setCLRID(String CLRID) {
        this.CLRID = CLRID;
    }

    public String getYTJ() {
        return YTJ;
    }

    public void setYTJ(String YTJ) {
        this.YTJ = YTJ;
    }

    private String recordID;
    private String recordTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getGh() {
        return gh;
    }

    public void setGh(String gh) {
        this.gh = gh;
    }

    public String getYpmc() {
        return ypmc;
    }

    public void setYpmc(String ypmc) {
        this.ypmc = ypmc;
    }

    public String getYg() {
        return yg;
    }

    public void setYg(String yg) {
        this.yg = yg;
    }

    public String getRjbh() {
        return rjbh;
    }

    public void setRjbh(String rjbh) {
        this.rjbh = rjbh;
    }

    public String getSmd() {
        return smd;
    }

    public void setSmd(String smd) {
        this.smd = smd;
    }

    public String getYw() {
        return yw;
    }

    public void setYw(String yw) {
        this.yw = yw;
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
            countRecordBean.setRecordType(CountRecordBean.OIL_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        CLRID = this.loginId;
        boolean isExist = false;
        try {
            OilInfoBean carFixBean = LitePal.find(OilInfoBean.class,getId());
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
