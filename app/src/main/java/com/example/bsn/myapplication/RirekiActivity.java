package com.example.bsn.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

public class RirekiActivity extends AppCompatActivity {

    @Override
    /**
     * 申請履歴画面の初期処理を行う
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rireki);
        setActionBar((Toolbar)findViewById(R.id.toolbar));

        //申請ボタンClickリスナー
        Button sendButton = findViewById(R.id.shukkinBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            // インターフェースの実装
                            HttpTaskRirekiGet httpTask = new HttpTaskRirekiGet(new HttpTaskRirekiGet.AsyncTaskCallBack() {
                                @Override
                                // 非同期処理の値を引き継ぐ
                                public void loginCallBack(List<RirekiInfoDTO> result) {

                                }

                            });
                            // 非同期処理の実行
                            Intent intent = getIntent();
                            UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
                            httpTask.execute(userInfo.getUserId());

                        }

                    });
                }
}
