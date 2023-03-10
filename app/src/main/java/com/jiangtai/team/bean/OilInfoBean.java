package com.jiangtai.team.bean;

import com.jiangtai.team.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class OilInfoBean  extends LitePalSupport {



    public OilInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;


    //罐号
    private String GH;
    //油品名称
    private String YPMC;
    //油高
    private String YG;
    //容积表号
    private String RJBH;
    //视密度
    private String SMD;
    //油温
    private String YW;
    //测量时间
    private String SJSJ;
    //备注
    private String bz;

    private String recordID;
    private String recordTime;


    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getGH() {
        return GH;
    }

    public void setGH(String GH) {
        this.GH = GH;
    }

    public String getYPMC() {
        return YPMC;
    }

    public void setYPMC(String YPMC) {
        this.YPMC = YPMC;
    }

    public String getYG() {
        return YG;
    }

    public void setYG(String YG) {
        this.YG = YG;
    }

    public String getRJBH() {
        return RJBH;
    }

    public void setRJBH(String RJBH) {
        this.RJBH = RJBH;
    }

    public String getSMD() {
        return SMD;
    }

    public void setSMD(String SMD) {
        this.SMD = SMD;
    }

    public String getYW() {
        return YW;
    }

    public void setYW(String YW) {
        this.YW = YW;
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


    @Override
    public boolean save() {
        CountRecordBean countRecordBean = new CountRecordBean();
        countRecordBean.setRecordTime(this.recordTime);
        countRecordBean.setRecordType(CountRecordBean.OIL_TYPE);
        countRecordBean.setRecordID(this.recordID);
        countRecordBean.save();
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        return super.save();
    }
}
