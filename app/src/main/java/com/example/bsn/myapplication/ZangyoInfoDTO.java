package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * 残業情報を格納するDTO
 */
public class ZangyoInfoDTO implements Serializable {

    // 残業日
    private String zangyoDate;

    // 残業開始時間
    private String zangyoTimeFrom;

    // 残業終了時間
    private String zangyoTimeTo;

    // 実残業時間
    private String zangyoTime;

    // 申請理由
    private String zangyoReason;

    // 勤務場所
    private String zangyoPlace;

    // 申請区分
    private String applyDiv;

    // 申請日時
    private String applyDate;

    // ユーザーID
    private String userId;

    public String getZangyoDate() {
        return zangyoDate;
    }
    public void setZangyoDate(String zangyoDate) {
        this.zangyoDate = zangyoDate;
    }

    public String getZangyoTimeFrom() {
        return zangyoTimeFrom;
    }
    public void setZangyoTimeFrom(String zangyoTimeFrom) {
        this.zangyoTimeFrom = zangyoTimeFrom;
    }

    public String getZangyoTimeTo() {
        return zangyoTimeTo;
    }
    public void setZangyoTimeTo(String zangyoTimeTo) {
        this.zangyoTimeTo = zangyoTimeTo;
    }

    public String getZangyoTime() {
        return zangyoTime;
    }
    public void setZangyoTime(String zangyoTime) {
        this.zangyoTime = zangyoTime;
    }

    public String getZangyoReason() {
        return zangyoReason;
    }
    public void setZangyoReason(String zangyoReason) {
        this.zangyoReason = zangyoReason;
    }

    public String getZangyoPlace() {
        return zangyoPlace;
    }
    public void setZangyoPlace(String zangyoPlace) {
        this.zangyoPlace = zangyoPlace;
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
        this.zangyoDate = null;
        this.zangyoTimeFrom = null;
        this.zangyoTimeTo = null;
        this.zangyoTime = null;
        this.zangyoReason = null;
        this.zangyoPlace = null;
        this.applyDiv = null;
        this.applyDate = null;
        this.userId = null;
    }

}
