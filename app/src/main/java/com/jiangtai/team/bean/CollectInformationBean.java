package com.jiangtai.team.bean;


import java.util.List;

public class CollectInformationBean {

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
        private String users_uuid;
        private String users_login_uid;
        private String users_tenant_id;

        public String getUsers_uuid() {
            return users_uuid;
        }

        public void setUsers_uuid(String users_uuid) {
            this.users_uuid = users_uuid;
        }

        public String getUsers_login_uid() {
            return users_login_uid;
        }

        public void setUsers_login_uid(String users_login_uid) {
            this.users_login_uid = users_login_uid;
        }

        public String getUsers_tenant_id() {
            return users_tenant_id;
        }

        public void setUsers_tenant_id(String users_tenant_id) {
            this.users_tenant_id = users_tenant_id;
        }
    }
}
