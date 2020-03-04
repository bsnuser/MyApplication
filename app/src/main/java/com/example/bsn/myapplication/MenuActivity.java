package com.example.bsn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    // 遷移先画面コード
    private final int SHUKKIN = 0;
    private final int YUKYU = 1;
    private final int ZANGYO = 2;
    private final int RIREKI = 3;
    private static Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //setActionBar((Toolbar) findViewById(R.id.toolbar));

        // ユーザー情報の受け取り
        Intent intent = getIntent();
        UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");

        // 受け取った情報を画面に表示する
        TextView userName = (TextView) findViewById(R.id.userText);
        userName.setText("ようこそ　" + userInfo.getUserName() + "　さん！");

        // 出勤確認のボタン
        Button shukkinBtn = findViewById(R.id.shukkin);
        shukkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画面遷移
                sendView(SHUKKIN);
            }
        });
        // 有休申請のボタン
        Button yukyuBtn = findViewById(R.id.yukyu);
        yukyuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画面遷移
                sendView(YUKYU);
            }
        });
        // 残業申請のボタン
        Button zangyoBtn = findViewById(R.id.zangyo);
        zangyoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画面遷移
                sendView(ZANGYO);
            }
        });
        // 申請履歴のボタン
        Button rirekiBtn = findViewById(R.id.rireki);
        rirekiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画面遷移
                sendView(RIREKI);
            }
        });
    }

    /**
     * 画面遷移の分岐を行う
     *
     * @param viewCode 画面コード
     */
    private void sendView(int viewCode) {

        // ユーザー情報の受け取り
        Intent intent = getIntent();
        UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");

        // 各画面に遷移するためのインスタンスを生成
        if (viewCode == 0) {
            intent = new Intent(getApplication(), ShukkinActivity.class);
            //intent = new Intent(getApplication(), BeaconActivity.class);
        } else if (viewCode == 1) {
            intent = new Intent(getApplication(), YukyuActivity.class);
        } else if (viewCode == 2){
            intent = new Intent(getApplication(), ZangyoActivity.class);
        }else {
            intent = new Intent(getApplication(), RirekiActivity.class);
        }

        // 画面遷移時にトーストをキャンセル
        if (toast != null){
            toast.cancel();
        }
        // DTOの格納
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    /**
     * メニューボタンの作成
     *
     * @param menu メニュー
     * @return 表示の有効無効
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    /**
     * メニューボタン押下時のプルダウンリストの設定
     *
     * @param item 表示アイテム
     * @return 表示の有効無効
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                // ログイン情報のクリア処理
                UserInfoDTO userInfo = new UserInfoDTO();
                userInfo.setClear();

                // ログイン画面へ遷移
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * バックキーの制御　バックキーのイベントをキャンセルする
     *
     * @param keyCode ステータスバーのキーコード
     * @param event   イベント
     * @return バックキーの有効無効
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        } else {
            // toastでバックキー使えないよって伝える
            toastDisp("右上のメニューバーからログアウトしてね！");
            return false;
        }
    }

    /**
     * toastの連続表示防止
     *
     * @param message 表示するメッセージ
     */
    public void toastDisp(String message) {
        if (toast != null) {
            toast.cancel();
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}