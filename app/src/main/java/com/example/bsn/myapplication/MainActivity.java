package com.example.bsn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button sendButton = findViewById(R.id.button2);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userNum = (EditText) findViewById(R.id.usernum);
                SpannableStringBuilder sp1 = (SpannableStringBuilder) userNum.getText();

                EditText password = (EditText) findViewById(R.id.password);
                SpannableStringBuilder sp2 = (SpannableStringBuilder) password.getText();

                // 入力したログインID
                final String userId = sp1.toString();
                // 入力したパスワード
                final String pass = sp2.toString();

                // 未入力エラー
                if ("".equals(userId) || "".equals(pass)) {
                    toastDisp("社員番号とパスワードを入力してください");
                } else {
                    // インターフェースの実装
                    HttpTask httpTask = new HttpTask(new HttpTask.AsyncTaskCallBack() {
                        @Override
                        // 非同期処理の値を引き継ぐ
                        public void loginCallBack(List<UserInfoDTO> result) {
                            // ログイン成功判定
                            UserInfoDTO checkResult = checkLogin(userId, pass, result);
                            // ログイン情報を格納するDTO
                            UserInfoDTO userInfo = new UserInfoDTO();
                            userInfo.setClear();
                            // ログイン成功時
                            if (checkResult.getUserName() != null) {
                                // 画面遷移時にtoastをキャンセル
                                if (toast != null){
                                    toast.cancel();
                                }
                                // ログインボタンの非活性処理
                                sendButton.setEnabled(false);
                                // ログインユーザ情報の格納
                                userInfo.setUserId(userId);
                                userInfo.setUserName(checkResult.getUserName());
                                userInfo.setPassword(pass);
                                userInfo.setUserDeptId(checkResult.getUserDeptId());
                                userInfo.setDeleteFlg(checkResult.getDeleteFlg());
                                // Intentのインスタンス化
                                Intent intent = new Intent(getApplication(), MenuActivity.class);
                                // DTOの格納
                                intent.putExtra("userInfo", userInfo);
                                // メニュー画面へ遷移
                                startActivity(intent);
                            }
                            // 認証エラーだった場合
                            else {
                                toastDisp("ログイン失敗");
                            }
                        }
                    });
                    // 非同期処理の実行
                    httpTask.execute();
                }
            }
        });
    }

    /**
     * ログイン判定
     *
     * @return ログインできたアカウントの情報
     */
    private UserInfoDTO checkLogin(String userId, String password, List<UserInfoDTO> userInfo) {

        // ログインに成功したアカウントの情報を格納する
        UserInfoDTO checkResult = new UserInfoDTO();
        checkResult.setClear();
        for (int i = 0; i < userInfo.size(); i++) {
            // ログイン成功時
            if (userInfo.get(i).getUserId().equals(userId) && userInfo.get(i).getPassword().equals(password)) {
                checkResult.setUserId(userId);
                checkResult.setUserName(userInfo.get(i).getUserName());
                checkResult.setPassword(password);
                checkResult.setUserDeptId(userInfo.get(i).getUserDeptId());
                checkResult.setDeleteFlg(userInfo.get(i).getDeleteFlg());

                break;
            }
        }
        return checkResult;
    }

    /**
     * トーストの連続表示防止
     *
     * @param message 表示するメッセージ
     */
    public void toastDisp(String message){
        // トーストがすでに表示されていたら前のをキャンセルして新しいものを表示
        if (toast != null) {
            toast.cancel();
            toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        }
        // 表示されていなければそのまま表示
        else {
            toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /**
     * バックキーの制御　バックキーのイベントをキャンセルする
     *
     * @param keyCode ステータスバーの押されたキーコード
     * @param event   イベント
     * @return バックキーの有効・無効
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (toast != null){
            toast.cancel();
        }
        // 画面遷移時にtoastをキャンセル
        // キーコードがバックキー以外のときは通常の動作をする
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        // バックキーの時は無効にする（ホーム画面に戻るようになった？？？？）
        else {
            // ホーム画面へ遷移するインテントの発行
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            return super.onKeyDown(keyCode, event);
        }
    }
}