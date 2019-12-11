package com.example.bsn.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HttpTaskZangyo extends AsyncTask<ZangyoInfoDTO, Void,String> {
    /**
     * コールバックインターフェース
     * メインスレッドに処理結果を引き継ぐyo
     */
    public interface AsyncTaskCallBack {
        void loginCallBack(String st);
    }

    private AsyncTaskCallBack callBack = null;

    public HttpTaskZangyo(AsyncTaskCallBack _asyncTaskCallBack){
        callBack = _asyncTaskCallBack;
    }

    @Override
    protected String doInBackground(ZangyoInfoDTO... params) {

        String zangyoDate = params[0].getZangyoDate();
        String zangyoTimeFrom = params[0].getZangyoTimeFrom();
        String zangyoTimeTo = params[0].getZangyoTimeTo();
        String zangyoTime = params[0].getZangyoTime();
        String zangyoReason = params[0].getZangyoReason();
        String zangyoPlace = params[0].getZangyoPlace();
        String applyDiv = params[0].getApplyDiv();
        String applyDate = params[0].getApplyDate();
        String userId = params[0].getUserId();

        String textUrl;

        try {
            final int CONNECTION_TIMEOUT = 30 * 1000;
            final int READ_TIMEOUT = 30 * 1000;

            // 接続先URL
            textUrl = "https://bsnkintaiapp.mybluemix.net/api/zangyo";
            URL url = new URL(textUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Android");
            conn.setRequestProperty("Accept-Language", Locale.getDefault().toString());
            //conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            conn.connect();

            OutputStream os = conn.getOutputStream();
            //PrintStream ps = new PrintStream(os);

            String param =
               "zangyoDate=" + zangyoDate +
               "&zangyoTimeFrom=" + zangyoTimeFrom +
               "&zangyoTimeTo=" + zangyoTimeTo +
               "&zangyoTime=" + zangyoTime +
               "&zangyoReason=" + zangyoReason +
               "&zangyoPlace=" + zangyoPlace +
               "&applyDiv=" + applyDiv +
               "&applyDate=" + applyDate +
               "&userId=" + userId;

            //os.write("userId=test".getBytes());
            os.write(param.getBytes());
            //out.write("userId=test");
            conn.connect();

            os.close();
            int statusCode = conn.getResponseCode();

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

        return "ok";
    }

    /**
     * メインスレッドに処理結果を返却する
     */
    @Override
    protected void onPostExecute(String st) {
        super.onPostExecute(st);
        this.callBack.loginCallBack(st);
    }
}
