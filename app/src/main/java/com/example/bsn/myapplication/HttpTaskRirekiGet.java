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


public class HttpTaskRirekiGet extends AsyncTask<String, Void, List<RirekiInfoDTO>> {
    /**
     * コールバックインターフェース
     * メインスレッドに処理結果を引き継ぐyo
     */
    public interface AsyncTaskCallBack {
        void loginCallBack(List<RirekiInfoDTO> result);
    }

    private AsyncTaskCallBack callBack = null;

    public HttpTaskRirekiGet(AsyncTaskCallBack _asyncTaskCallBack){
        callBack = _asyncTaskCallBack;
    }

    @Override
    protected List<RirekiInfoDTO> doInBackground(String... params) {

        String userId = params[0];
        //String password = params[1];
        String textUrl;

        // 取得結果を全件入れておくリスト
        List<RirekiInfoDTO> result = new ArrayList<RirekiInfoDTO>();
        try {
            final int CONNECTION_TIMEOUT = 30 * 1000;
            final int READ_TIMEOUT = 30 * 1000;

            // 接続先URL
            textUrl = "https://bsnkintaiapp.mybluemix.net/api/applyinfo?userId="+ userId;
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
                            // 情報を取得してDTOにつめつめ
                            JSONObject json = jsonArray.getJSONObject(i);

                            RirekiInfoDTO rirekiInfo = new RirekiInfoDTO();

                            switch (json.getString("apply_div")) {
                                case "1": //出勤申請
                                    rirekiInfo.setDate(json.getString("shukkin_date"));
                                    break;
                                case "2": //有休申請
                                    rirekiInfo.setDate(json.getString("yukyu_date_from"));
                                    break;
                                case "3": //残業申請
                                    rirekiInfo.setDate(json.getString("zangyo_date"));
                                    break;
                            }

                            rirekiInfo.setId(json.getString("_id"));

                            result.add(rirekiInfo);

                            //Log.d("userId", userInfo.getUserId());
                            //Log.d("userName", userInfo.getUserName());
                        }
                    } catch (JSONException e){
                        Log.e("エラー", e.getMessage());
                    }
                }
                // 使ったものたちの片づけ
                bufferedReader.close();
                inReader.close();
                in.close();
                conn.disconnect();
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
    protected void onPostExecute(List<RirekiInfoDTO> result) {
        super.onPostExecute(result);
        this.callBack.loginCallBack(result);
    }
}
