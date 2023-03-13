package com.jiangtai.team.bean;

import com.jiangtai.team.util.CommonUtil;

import org.litepal.crud.LitePalSupport;

public class HelicopterOilInfoBean extends LitePalSupport {

    public HelicopterOilInfoBean(){
        String  recordTime = System.currentTimeMillis()+"";
        this.recordTime = recordTime;
        recordID = CountRecordBean.Companion.getCountId(recordTime);
    }

    //登录的人员ID(用于区分每个登录的不同用户的数据)
    private String loginId;


    //Y罐车号
    private String YGCH;
    //加油时间
    private String JYSJ;
    //加油类型
    private String JYLX;
    //加油量
    private String JYL;
    //Y量品牌号
    private String YPPPH;
    //Y品纯度
    private String YPCD;
    //Y品含水量
    private String YPHSL;
    //添加剂
    private String TJJ;
    //备注
    private String BZ;
    //加油员
    private String JYY;
    //机长
    private String JZ;
    //其他人员
    private String QTRY;





    private String recordID;
    private String recordTime;


    public String getYGCH() {
        return YGCH;
    }

    public void setYGCH(String YGCH) {
        this.YGCH = YGCH;
    }

    public String getJYSJ() {
        return JYSJ;
    }

    public void setJYSJ(String JYSJ) {
        this.JYSJ = JYSJ;
    }

    public String getJYLX() {
        return JYLX;
    }

    public void setJYLX(String JYLX) {
        this.JYLX = JYLX;
    }

    public String getJYL() {
        return JYL;
    }

    public void setJYL(String JYL) {
        this.JYL = JYL;
    }

    public String getYPPPH() {
        return YPPPH;
    }

    public void setYPPPH(String YPPPH) {
        this.YPPPH = YPPPH;
    }

    public String getYPCD() {
        return YPCD;
    }

    public void setYPCD(String YPCD) {
        this.YPCD = YPCD;
    }

    public String getYPHSL() {
        return YPHSL;
    }

    public void setYPHSL(String YPHSL) {
        this.YPHSL = YPHSL;
    }

    public String getTJJ() {
        return TJJ;
    }

    public void setTJJ(String TJJ) {
        this.TJJ = TJJ;
    }

    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getJYY() {
        return JYY;
    }

    public void setJYY(String JYY) {
        this.JYY = JYY;
    }

    public String getJZ() {
        return JZ;
    }

    public void setJZ(String JZ) {
        this.JZ = JZ;
    }

    public String getQTRY() {
        return QTRY;
    }

    public void setQTRY(String QTRY) {
        this.QTRY = QTRY;
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
