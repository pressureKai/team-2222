package com.jiangtai.count.bean;

import java.util.List;

public class TaskListBean {

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
        private String plan_code;
        private String plan_name;
        private String task_code;
        private String task_name;
        private String issue_user;
        private String issue_userid;
        private String s_time;
        private String e_time;
        private String address;
        private String remark;

        public String getPlan_code() {
            return plan_code;
        }

        public void setPlan_code(String plan_code) {
            this.plan_code = plan_code;
        }

        public String getPlan_name() {
            return plan_name;
        }

        public void setPlan_name(String plan_name) {
            this.plan_name = plan_name;
        }

        public String getTask_code() {
            return task_code;
        }

        public void setTask_code(String task_code) {
            this.task_code = task_code;
        }

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public String getIssue_user() {
            return issue_user;
        }

        public void setIssue_user(String issue_user) {
            this.issue_user = issue_user;
        }

        public String getIssue_userid() {
            return issue_userid;
        }

        public void setIssue_userid(String issue_userid) {
            this.issue_userid = issue_userid;
        }

        public String getS_time() {
            return s_time;
        }

        public void setS_time(String s_time) {
            this.s_time = s_time;
        }

        public String getE_time() {
            return e_time;
        }

        public void setE_time(String e_time) {
            this.e_time = e_time;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
