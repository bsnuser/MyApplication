package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * 有休情報を格納するDTO
 */
public class YukyuInfoDTO implements Serializable {

    // 有休期間開始日
    private String yukyuDateFrom;

    // 有休期間終了日
    private String yukyuDateTo;

    // 有休区分
    private String yukyuDiv;

    // 申請理由選択
    private String yukyuReasonDiv;

    // 申請理由
    private String yukyuReason;

    // 申請区分
    private String applyDiv;

    // 申請日時
    private String applyDate;

    // ユーザーID
    private String userId;

    public String getYukyuDateFrom() {
        return yukyuDateFrom;
    }
    public void setYukyuDateFrom(String yukyuDateFrom) {
        this.yukyuDateFrom = yukyuDateFrom;
    }

    public String getYukyuDateTo() {
        return yukyuDateTo;
    }
    public void setYukyuDateTo(String yukyuDateTo) {
        this.yukyuDateTo = yukyuDateTo;
    }

    public String getYukyuDiv() {
        return yukyuDiv;
    }
    public void setYukyuDiv(String yukyuDiv) {
        this.yukyuDiv = yukyuDiv;
    }

    public String getYukyuReasonDiv() {
        return yukyuReasonDiv;
    }
    public void setYukyuReasonDiv(String yukyuReasonDiv) {
        this.yukyuReasonDiv = yukyuReasonDiv;
    }

    public String getYukyuReason() {
        return yukyuReason;
    }
    public void setYukyuReason(String yukyuReason) {
        this.yukyuReason = yukyuReason;
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
        this.yukyuDateFrom = null;
        this.yukyuDateTo = null;
        this.yukyuDiv = null;
        this.yukyuReasonDiv = null;
        this.yukyuReason = null;
        this.applyDiv = null;
        this.applyDate = null;
        this.userId = null;
    }

}
