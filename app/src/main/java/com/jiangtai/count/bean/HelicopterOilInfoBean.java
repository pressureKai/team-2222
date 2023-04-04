package com.jiangtai.count.bean;

import com.jiangtai.count.util.CommonUtil;

import org.litepal.LitePal;
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
    private String fjbh;
    //加油时间
    private String jysj;
    //加油类型
    private String jylx;
    //加油量
    private String jyl;
    //Y量品牌号
    private String ypmc;
    //Y品纯度
    private String ypcd;
    //Y品含水量
    private String yphsl;
    //添加剂
    private String tjj;
    //备注
    private String bz;
    //加油员
    private String jyy;
    //机长
    private String jz;
    //其他人员
    private String qtry;


    //加油人ID
    private String jyrid;

    //剩余油量
    private String syyl;



    public String getSyyl() {
        return syyl;
    }

    public void setSyyl(String syyl) {
        this.syyl = syyl;
    }



    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJyrid() {
        return jyrid;
    }

    public void setJyrid(String jyrid) {
        this.jyrid = jyrid;
    }

    private String recordID;
    private String recordTime;


    public String getFjbh() {
        return fjbh;
    }

    public void setFjbh(String fjbh) {
        this.fjbh = fjbh;
    }

    public String getYGCH() {
        return fjbh;
    }

    public void setYGCH(String YGCH) {
        this.fjbh = YGCH;
    }

    public String getJysj() {
        return jysj;
    }

    public void setJysj(String jysj) {
        this.jysj = jysj;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx;
    }

    public String getJyl() {
        return jyl;
    }

    public void setJyl(String jyl) {
        this.jyl = jyl;
    }

    public String getYpmc() {
        return ypmc;
    }

    public void setYpmc(String ypmc) {
        this.ypmc = ypmc;
    }

    public String getYpcd() {
        return ypcd;
    }

    public void setYpcd(String ypcd) {
        this.ypcd = ypcd;
    }

    public String getYphsl() {
        return yphsl;
    }

    public void setYphsl(String yphsl) {
        this.yphsl = yphsl;
    }

    public String getTjj() {
        return tjj;
    }

    public void setTjj(String tjj) {
        this.tjj = tjj;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getJyy() {
        return jyy;
    }

    public void setJyy(String jyy) {
        this.jyy = jyy;
    }

    public String getJz() {
        return jz;
    }

    public void setJz(String jz) {
        this.jz = jz;
    }

    public String getQtry() {
        return qtry;
    }

    public void setQtry(String qtry) {
        this.qtry = qtry;
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
            countRecordBean.setRecordType(CountRecordBean.HELICOPTER_OIL_TYPE);
            countRecordBean.setRecordID(this.recordID);
            countRecordBean.save();
        }
        this.loginId = CommonUtil.INSTANCE.getLoginUserId();
        jyrid = this.loginId;
        boolean isExist = false;
        try {
            HelicopterOilInfoBean carFixBean = LitePal.find(HelicopterOilInfoBean.class, getId());
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
