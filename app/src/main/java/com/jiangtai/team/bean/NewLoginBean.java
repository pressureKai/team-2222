package com.jiangtai.team.bean;

public class NewLoginBean {
    private String name;
    private String userId;

    //角色类型
    private String jxlx;
    //所属部门
    private String ssbm;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJxlx() {
        return jxlx;
    }

    public void setJxlx(String jxlx) {
        this.jxlx = jxlx;
    }

    public String getSsbm() {
        return ssbm;
    }

    public void setSsbm(String ssbm) {
        this.ssbm = ssbm;
    }
}
