package com.urbantech.item;

import java.io.Serializable;

public class ItemUser implements Serializable {

    private String id, name, email, mobile, dp;
    private String authID;
    private String loginType;
    private Boolean isReporter = false;

    public ItemUser(String id, String name, String email, String mobile, String dp, String authID, String loginType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dp = dp;
        this.authID = authID;
        this.loginType = loginType;
    }

    public ItemUser(String id, String name, String email, String mobile, String dp, String authID, String loginType, Boolean isReporter) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dp = dp;
        this.authID = authID;
        this.loginType = loginType;
        this.isReporter = isReporter;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
        }

        public String getLoginType() {
            return loginType;
        }

        public String getAuthID() {
            return authID;
        }

    public Boolean getIsReporter() {
        return isReporter;
    }
}
