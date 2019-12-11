package com.example.bsn.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;


public class YukyuActivity extends AppCompatActivity {

    int index = 0;
    String name = "";
    private String HIDUKE_START    = "";
    private String HIDUKE_END      = "";
    private String SHINSEI_SENTAKU = "";
    private String SHINSEI_REASON  = "";
    private String Yukyu_KBN  = "0";
    private final String Yukyu_KBN_TSUJO = "0";
    private final String Yukyu_KBN_GOZEN = "1";
    private final String Yukyu_KBN_GOGO  = "2";

    private final String PUT_KBN_YUKYU = "2";  // 申請csv出力区分

    private View mFocusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yukyu);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        //"reason_text"を非表示(非表示にしたスペースを詰める)にする
        findViewById(R.id.reason_text).setVisibility(View.GONE);

        //申請ボタンClickリスナー
        Button sendButton = findViewById(R.id.yukyushinseiBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // チェック処理
                if(!check()){
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(YukyuActivity.this);
                    builder.setMessage("申請してよろしいですか？");
                    builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            // インターフェースの実装
                            HttpTaskYukyu httpTask = new HttpTaskYukyu(new HttpTaskYukyu.AsyncTaskCallBack() {
                                @Override
                                // 非同期処理の値を引き継ぐ
                                public void loginCallBack(String st) {


                                    Context context = getApplicationContext();
                                    PutRecordRequest putrecordrequest = new PutRecordRequest(context, getIntent());
                                    putrecordrequest.putRecord(PUT_KBN_YUKYU, makeCSV());


                                    Toast.makeText(YukyuActivity.this, "申請しました", Toast.LENGTH_LONG).show();

                                    // メニュー画面へ遷移
                                    Intent intent = getIntent();
                                    UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
                                    intent = new Intent(getApplication(), MenuActivity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }

                    });
                            // 非同期処理の実行
                            httpTask.execute(makeDTO());

                        }

                    });

                    builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    builder.show();
                }
            }
        });

        // 有給申請区分ラジオボタン動作設定
        // 「通常有給」を初期選択
        final RadioButton tsujo = findViewById(R.id.tsujo);
        final RadioButton gozen = findViewById(R.id.gozen);
        final RadioButton gogo  = findViewById(R.id.gogo);
        tsujo.setChecked(true);
        tsujo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsujo.setChecked(true);
                gozen.setChecked(false);
                gogo.setChecked(false);
                Yukyu_KBN = Yukyu_KBN_TSUJO;
            }
        });
        gozen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsujo.setChecked(false);
                gozen.setChecked(true);
                gogo.setChecked(false);
                Yukyu_KBN = Yukyu_KBN_GOZEN;
            }
        });
        gogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsujo.setChecked(false);
                gozen.setChecked(false);
                gogo.setChecked(true);
                Yukyu_KBN = Yukyu_KBN_GOGO;
            }
        });


        // 有休日付設定(開始)
        // 初期値は今日日付とする
        TextView textView = findViewById(R.id.textView17);
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        textView.setText(sdf.format(cl.getTime()));
        HIDUKE_START = sdf.format(cl.getTime());

        // 「日付変更」（開始）ボタン動作設定
        // ダイアログクラスをインスタンス化し実行。.showのgetFagmentManager()は固定、tagは識別タグ
        Button dialogBtnStart = findViewById(R.id.calenderstart);
        dialogBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YukyuActivityCalendarDialog dialog = new YukyuActivityCalendarDialog();

                Bundle args = new Bundle();
                args.putString("Value", "Start");
                dialog.setArguments(args);

                dialog.show(getFragmentManager(), "dialog");
            }
        });


        // 「日付変更」（終了）ボタン動作設定
        Button dialogBtnEnd = findViewById(R.id.calenderend);
        dialogBtnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YukyuActivityCalendarDialog dialog = new YukyuActivityCalendarDialog();

                Bundle args = new Bundle();
                args.putString("Value", "End");
                dialog.setArguments(args);

                dialog.show(getFragmentManager(), "dialog");
            }
        });


        // Adapterの作成
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adapterにアイテムを追加
        adapter.add("私用の為");
        adapter.add("休養の為");
        adapter.add("療養(疾病)の為");
        adapter.add("療養(負傷)の為");
        adapter.add("家族看護の為");
        adapter.add("育児の為");
        adapter.add("介護の為");
        adapter.add("リフレッシュ休暇");
        adapter.add("その他");

        Spinner the_reason = (Spinner) findViewById(R.id.the_reason);

        // SpinnerにAdapterを設定
        the_reason.setAdapter(adapter);

        the_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            //何も選択されなかった時の動作
            @Override
            public void onNothingSelected(AdapterView adapterView) {
                //何もしない
            }

            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                //選択されたアイテム名と位置（index)を内部変数へ保存
                name = parent.getSelectedItem().toString();
                index = position;

                SHINSEI_SENTAKU = name;

                //位置(index)が8番目("その他"が選択された)の時のみ"reason_text"を表示する
                if (index == 8) {
                    findViewById(R.id.reason_text).setVisibility(View.VISIBLE);
                    //位置(index)が8番目以外の時、"reason_text"を非表示(非表示にしたスペースを詰める。)にする
                } else {
                    findViewById(R.id.reason_text).setVisibility(View.GONE);
                }
            }
        });

        //「キーボードが表示している場合」のみの処理に出来ればいけるかも。。。
        mFocusView = findViewById(R.id.yukyu_layout);
        mFocusView.requestFocus();

        EditText editText = (EditText) findViewById(R.id.reason_text);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    // フォーカスが外れた場合キーボードを非表示にする
                    InputMethodManager inputMethodMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });
    }

    /**
     * カレンダーダイアログで選択した内容を画面（開始日）に反映する
     * @param value カレンダーダイアログ選択日付
     */
    public void setTextViewStart(String value){
        TextView textView = findViewById(R.id.textView17);
        textView.setText(value);
        HIDUKE_START = value;
    }

    /**
     * カレンダーダイアログで選択した内容を画面（終了日）に反映する
     * @param value カレンダーダイアログ選択日付
     */
    public void setTextViewEnd(String value){
        TextView textView = findViewById(R.id.textView33);
        textView.setText(value);
        HIDUKE_END = value;
    }

    // 内部ストレージ出力ファイル内容作成
    private String[] makeCSV(){

        List<String> valuelist = new ArrayList<String>();

        // ユーザID（DTOから取得）
        Intent intent = getIntent();
        UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
        valuelist.add(userInfo.getUserId());

        valuelist.add(HIDUKE_START); // 有給申請期間始め
        valuelist.add(HIDUKE_END); // 有給申請期間終わり
        valuelist.add(Yukyu_KBN); // 有給申請区分
        valuelist.add(SHINSEI_SENTAKU); // 申請理由選択
        valuelist.add(SHINSEI_REASON); // 申請理由

        String[] value = valuelist.toArray(new String[valuelist.size()]);
        return value;
    }

    public boolean hikaku() {

        boolean hikaku = true;

        String[] arySmall = HIDUKE_START.split("/");
        int KaishiYear = Integer.parseInt(arySmall[0]);
        int KaishiMonth = Integer.parseInt(arySmall[1]);
        int KaishiDate = Integer.parseInt(arySmall[2]);

        String[] aryLarge = HIDUKE_END.split("/");
        int ShuryoYear = Integer.parseInt(aryLarge[0]);
        int ShuryoMonth = Integer.parseInt(aryLarge[1]);
        int ShuryoDate = Integer.parseInt(aryLarge[2]);

        if(KaishiYear >= ShuryoYear){
            if(KaishiMonth >= ShuryoMonth){
                if(KaishiDate > ShuryoDate){
                    hikaku = false;
                }
            }
        }

        return hikaku;

    }


    private boolean check(){

        List<String> valuelist = new ArrayList<String>();

        // 申請日付入力チェック
        final TextView textView33 = findViewById(R.id.textView33);
        final TextView textView17 = findViewById(R.id.textView17);

        String kaishi = textView17.getText().toString();
        String shuryo = textView33.getText().toString();

        if("".equals(shuryo)){
            valuelist.add("申請日付を選択してください");
        }else{
            if(!hikaku()){
                valuelist.add("入力した日付が不正です");
            }
        }

        // 申請理由入力チェック
        if(index == 8){
            final EditText reason_text = findViewById(R.id.reason_text);
            if("".equals(reason_text.getText().toString())){
                valuelist.add("申請理由を入力してください");
            }else{
                SHINSEI_REASON = reason_text.getText().toString();
            }
        }

        // エラーが1件でもある場合、エラーメッセージをアラートで表示する
        if(valuelist.size()!=0){

            String[] value = valuelist.toArray(new String[valuelist.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(YukyuActivity.this);

            String errormessage = "入力内容が不正のため、申請できません。"+"\n\n\n\n";
            for(int i = 0; i < value.length; i++) {
                errormessage = errormessage + value[i];
                errormessage = errormessage + "\n\n";
            }
            builder.setMessage(errormessage);
            builder.setPositiveButton("OK",null);
            builder.show();
            return false;
        }
        return true;
    }

    /**
     * 申請内容を作成し返却する
     * @return 申請内容
     */
    // 内部ストレージ出力ファイル内容作成
    private YukyuInfoDTO makeDTO(){

        YukyuInfoDTO yukyuInfo = new YukyuInfoDTO();

        yukyuInfo.setYukyuDateFrom(HIDUKE_START); // 有休期間開始日
        yukyuInfo.setYukyuDateTo(HIDUKE_END); // 有休期間終了日
        yukyuInfo.setYukyuDiv(Yukyu_KBN); // 有休区分
        yukyuInfo.setYukyuReasonDiv(SHINSEI_SENTAKU); // 申請理由選択
        yukyuInfo.setYukyuReason(SHINSEI_REASON);//申請理由

        yukyuInfo.setApplyDiv("2"); //申請区分

        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMddhhmmss").format(date);
        yukyuInfo.setApplyDate(dateStr); //申請日時

        // ユーザID（DTOから取得）
        Intent intent = getIntent();
        UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
        yukyuInfo.setUserId(userInfo.getUserId());

        return yukyuInfo;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mFocusView.requestFocus();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.logout:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
