package com.example.bsn.myapplication;

import java.io.Serializable;

/**
 * 申請履歴情報を格納するDTO
 */
public class RirekiInfoDTO implements Serializable {

    // 区分
    private String div;

    // 日（出勤申請：出勤日、有休申請：有休期間開始日、残業申請：残業日）
    private String date;

    // データID
    private String id;

    public String getDiv() {
        return div;
    }
    public void setDiv(String div) {
        this.div = div;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // ユーザー情報のクリア処理
    public void setClear(){
        this.div = null;
        this.date = null;
        this.id = null;
    }

}
