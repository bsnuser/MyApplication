package com.example.bsn.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PutRecordRequest {

    private Context context;
    private Intent intent;

    private final String DATE_PATTERN_HAIHUN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String PUT_KBN_SHUKKIN = "1";
    public static final String PUT_KBN_YUKYU = "2";
    public static final String PUT_KBN_ZANGYO = "3";

    private final String fileNameShukkin = "requestShukkin.txt";
    private final String fileNameYukyu = "requestYukyu.txt";
    private final String fileNameZangyo = "requestZangyo.txt";

    PutRecordRequest(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }


    /**
     * 申請の内容を内部ストレージにCSV形式で出力する
     *
     * @param putKbn 申請区分（1:出勤申請／2：有休申請／3：残業申請）
     * @param request 申請内容
     * @return true：正常終了／false：エラー
     */
    public boolean putRecord(String putKbn, String[] request) {

        // ユーザー情報の受け取り
        UserInfoDTO userInfo = (UserInfoDTO) this.intent.getSerializableExtra("userInfo");
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();

        String record = "";
        record = record + userId;
        record = record + "," + userName;
        for(int i = 0; i < request.length; i++) {
            record = record + "," + request[i];
        }

        // 現在時刻取得
        Date nowDate = new Date();
        SimpleDateFormat formatHaihun = new SimpleDateFormat(DATE_PATTERN_HAIHUN);
        String nowDateHaihun = formatHaihun.format(nowDate);
        record = record + "," + nowDateHaihun;

        record = record + "\n";


        String fileName = "";
        if (PUT_KBN_SHUKKIN.equals(putKbn)) {
            fileName = fileNameShukkin;
        } else if (PUT_KBN_YUKYU.equals(putKbn)) {
            fileName = fileNameYukyu;
        } else if (PUT_KBN_ZANGYO.equals(putKbn)) {
            fileName = fileNameZangyo;
        } else {
            return false;
        }

        try (FileOutputStream fileOutputStream = this.context.openFileOutput(fileName, Context.MODE_APPEND);) {
            fileOutputStream.write(record.getBytes());
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
