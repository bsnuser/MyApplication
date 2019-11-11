package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * ユーザー情報を格納するDTO
 */
public class UserInfoDTO implements Serializable {

    // ユーザーID
    private String userId;

    // ユーザー名
    private String userName;

    // パスワード
    private String password;

    // 部署コード
    private String userDeptId;

    // デリートフラグ
    private String deleteFlg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserDeptId() {
        return userDeptId;
    }

    public void setUserDeptId(String userDeptId) {
        this.userDeptId = userDeptId;
    }

    public String getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(String deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    // ユーザー情報のクリア処理
    public void setClear(){
        this.userId = null;
        this.userName = null;
        this.password = null;
        this.userDeptId = null;
        this.deleteFlg = null;
    }

}
