package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * ユーザー情報を格納するDTO
 */
public class ZangyoInfoDTO implements Serializable {

    // 残業日
    private String zangyoDate;

    // 残業開始時刻
    private String zangyoTimeFrom;

    // 残業終了時刻
    private String zangyoTimeTo;

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
        this.applyDiv = null;
        this.applyDate = null;
        this.userId = null;
    }

}
