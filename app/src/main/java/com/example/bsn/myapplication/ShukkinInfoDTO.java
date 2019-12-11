package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * 出勤情報を格納するDTO
 */
public class ShukkinInfoDTO implements Serializable {

    // 出勤時刻
    private String shukkinDate;

    // 現在地
    private String shukkinPlace;

    // 申請区分
    private String applyDiv;

    // 申請日時
    private String applyDate;

    // ユーザーID
    private String userId;

    public String getShukkinDate() {
        return shukkinDate;
    }
    public void setShukkinDate(String shukkinDate) {
        this.shukkinDate = shukkinDate;
    }

    public String getShukkinPlace() {
        return shukkinPlace;
    }
    public void setShukkinPlace(String shukkinPlace) {
        this.shukkinPlace = shukkinPlace;
    }

    public String getApplyDiv() {
        return applyDiv;
    }
    public void setApplyDiv(String applyDiv) {
        this.applyDiv = applyDiv;
    }

    public String getApplyDate() {
        return applyDate;
    }
    public void setApplyDate(String applyDate) { this.applyDate = applyDate; }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // ユーザー情報のクリア処理
    public void setClear(){
        this.shukkinDate = null;
        this.shukkinPlace = null;
        this.applyDiv = null;
        this.applyDate = null;
        this.userId = null;
    }

}
