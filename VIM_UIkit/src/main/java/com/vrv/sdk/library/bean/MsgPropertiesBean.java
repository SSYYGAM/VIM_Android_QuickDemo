package com.vrv.sdk.library.bean;

public class MsgPropertiesBean {

    /**
     * operType : 操作类型（暂时未用到）
     * operUser : 红包接收方
     * usersInfo : 红包发送方
     * time : 红包抢完时间
     */

    private String operType;
    private String operUser;
    private String usersInfo;
    private String time;

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperUser() {
        return operUser;
    }

    public void setOperUser(String operUser) {
        this.operUser = operUser;
    }

    public String getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(String usersInfo) {
        this.usersInfo = usersInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
