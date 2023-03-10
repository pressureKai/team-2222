package com.jiangtai.team.bean;

import java.util.List;

public class ReturnIsBean {

    private String code;
    private List<DataDTO> data;
    private String info;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static class DataDTO {
        private int 受影响行数;

        public int get受影响行数() {
            return 受影响行数;
        }

        public void set受影响行数(int 受影响行数) {
            this.受影响行数 = 受影响行数;
        }
    }
}
