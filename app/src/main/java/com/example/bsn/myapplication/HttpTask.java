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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class HttpTask extends AsyncTask<String, Void, List<UserInfoDTO>> {
    /**
     * コールバックインターフェース
     * メインスレッドに処理結果を引き継ぐyo
     */
    public interface AsyncTaskCallBack {
        void loginCallBack(List<UserInfoDTO> result);
    }

    private AsyncTaskCallBack callBack = null;

    public HttpTask(AsyncTaskCallBack _asyncTaskCallBack){
        callBack = _asyncTaskCallBack;
    }

    @Override
    protected List<UserInfoDTO> doInBackground(String... params) {

        String userId = params[0];
        String password = params[1];
        String textUrl;

        // 取得結果を全件入れておくリスト
        List<UserInfoDTO> result = new ArrayList<UserInfoDTO>();
        try {
            final int CONNECTION_TIMEOUT = 30 * 1000;
            final int READ_TIMEOUT = 30 * 1000;

            // 接続先URL
            textUrl = "https://bsnkintaiapp.mybluemix.net/api/user?userId="+ userId + "&password=" + password;
            URL url = new URL(textUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            conn.setRequestMethod("GET");
            conn.connect();
            int statusCode = conn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                //responseの読み込み
                final InputStream in = conn.getInputStream();
                final InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                final BufferedReader bufferedReader = new BufferedReader(inReader);
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(line);
                        for(int i=0; i<jsonArray.length(); i++){
                            // ユーザー情報を取得してDTOにつめつめ
                            JSONObject json = jsonArray.getJSONObject(i);

                            UserInfoDTO userInfo = new UserInfoDTO();
                            userInfo.setUserId(json.getString("user_id"));
                            userInfo.setUserName(json.getString("user_name"));
                            userInfo.setPassword(json.getString("password"));
                            userInfo.setUserDeptId(json.getString("user_dept_id"));
                            userInfo.setDeleteFlg(json.getString("delete_flg"));

                            result.add(userInfo);

                            Log.d("userId", userInfo.getUserId());
                            Log.d("userName", userInfo.getUserName());
                        }
                    } catch (JSONException e){
                        Log.e("エラー", e.getMessage());
                    }
                }
                // 使ったものたちの片づけ
                bufferedReader.close();
                inReader.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * メインスレッドに処理結果を返却する
     * @param result 取得したユーザー情報
     */
    @Override
    protected void onPostExecute(List<UserInfoDTO> result) {
        super.onPostExecute(result);
        this.callBack.loginCallBack(result);
    }
}
