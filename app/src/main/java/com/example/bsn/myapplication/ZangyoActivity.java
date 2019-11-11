package com.example.bsn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.text.SimpleDateFormat;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import android.view.inputmethod.InputMethodManager;

public class ZangyoActivity extends AppCompatActivity {

    private String ZANGYO_BI ="";
    private String ZANGYO_KAISHI_JIKAN_JI="17";
    private String ZANGYO_KAISHI_JIKAN_HUN="00";
    private String ZANGYO_SHURYO_JIKAN_JI="17";
    private String ZANGYO_SHURYO_JIKAN_HUN="00";
    private String ZANGYO_SOU_JIKAN="0000";
    private String SHINSEI_RIYU="";
    private String KINMU_BASHO="0";
    private String KYUKEI1_KAISHI_JIKAN_JI="24";
    private String KYUKEI1_KAISHI_JIKAN_HUN="00";
    private String KYUKEI1_SHURYO_JIKAN_JI="24";
    private String KYUKEI1_SHURYO_JIKAN_HUN="00";
    private String KYUKEI2_KAISHI_JIKAN_JI="24";
    private String KYUKEI2_KAISHI_JIKAN_HUN="00";
    private String KYUKEI2_SHURYO_JIKAN_JI="24";
    private String KYUKEI2_SHURYO_JIKAN_HUN="00";
    private String KYUKEI3_KAISHI_JIKAN_JI="24";
    private String KYUKEI3_KAISHI_JIKAN_HUN="00";
    private String KYUKEI3_SHURYO_JIKAN_JI="24";
    private String KYUKEI3_SHURYO_JIKAN_HUN="00";
    private String KYUKEI_JIKAN_GOUKEI="0000";
    private String ZITSU_ZANGYO_JIKAN="0000";

    // 定数
    private final String KINMU_BASHO_SHANAI = "0";
    private final String KINMU_BASHO_SHAGAI = "1";
    private final String PUT_KBN_ZANGYO = "3";  // 申請csv出力区分

    @Override
    /**
     * 残業申請画面の初期処理を行う
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zangyo);
        setActionBar((Toolbar)findViewById(R.id.toolbar));

        // スピナー設定
        setSpinnerValue();

        //申請ボタンClickリスナー
        Button sendButton = findViewById(R.id.shinseiButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // チェック処理
                if(!check()){
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ZangyoActivity.this);
                    builder.setMessage("申請してよろしいですか？");
                    builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            Context context = getApplicationContext();
                            PutRecordRequest putrecordrequest = new PutRecordRequest(context, getIntent());
                            putrecordrequest.putRecord(PUT_KBN_ZANGYO, makeCSV());

                            Toast.makeText(ZangyoActivity.this, "申請しました", Toast.LENGTH_LONG).show();

                            // メニュー画面へ遷移
                            Intent intent = getIntent();
                            UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
                            intent = new Intent(getApplication(), MenuActivity.class);
                            intent.putExtra("userInfo", userInfo);
                            startActivity(intent);
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

        // 残業日付設定
        // 初期値は今日日付とする
        TextView textView = findViewById(R.id.zangyo_date);
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        textView.setText(sdf.format(cl.getTime()));

        // 「日付変更」ボタン動作設定
        // ダイアログクラスをインスタンス化し実行。.showのgetFagmentManager()は固定、tagは識別タグ
        Button dialogBtn = findViewById(R.id.calendardialogButton);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZangyoActivityCalendarDialog dialog = new ZangyoActivityCalendarDialog();
                dialog.show(getFragmentManager(), "dialog");
            }
        });

        // 勤務場所ラジオボタン動作設定
        // 「社内」を初期選択
        final RadioButton shanai = findViewById(R.id.shanai);
        final RadioButton shagai = findViewById(R.id.shagai);
        shanai.setChecked(true);
        shanai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shanai.setChecked(true);
                shagai.setChecked(false);
                KINMU_BASHO = KINMU_BASHO_SHANAI;
            }
        });
        shagai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shanai.setChecked(false);
                shagai.setChecked(true);
                KINMU_BASHO = KINMU_BASHO_SHAGAI;
            }
        });

        // 画面タッチでキーボードを非表示設定
        View touchView =  findViewById(R.id.touch_view);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button sendButton = findViewById(R.id.shinseiButton);
                sendButton.requestFocus();

                InputMethodManager inputMethodMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // レイヤ下のコンポーネントイベントを動作させるためfalseを返す
                return false;
            }
        });

        // 申請ボタンにフォーカスを設定し、初期表示時にキーボードが表示されないようにする
        sendButton.requestFocus();
    }

    /**
     * カレンダーダイアログで選択した内容を画面に反映する
     * @param value カレンダーダイアログ選択日付
     */
    public void setTextView(String value) {
        TextView textView = findViewById(R.id.zangyo_date);
        textView.setText(value);
        ZANGYO_BI = value;
    }

    /**
     * 申請ファイルの内容を作成し返却する
     * @return 申請ファイル内容
     */
    // 内部ストレージ出力ファイル内容作成
    private String[] makeCSV(){

        List<String> valuelist = new ArrayList<String>();

        // ユーザID（DTOから取得）
        Intent intent = getIntent();
        UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
        valuelist.add(userInfo.getUserId());

        valuelist.add(ZANGYO_BI); // 残業日
        valuelist.add(ZANGYO_KAISHI_JIKAN_JI + ZANGYO_KAISHI_JIKAN_HUN); // 残業開始時間
        valuelist.add(ZANGYO_SHURYO_JIKAN_JI + ZANGYO_SHURYO_JIKAN_HUN); // 残業終了時間
        valuelist.add(ZANGYO_SOU_JIKAN); // 残業総時間
        valuelist.add(SHINSEI_RIYU); // 申請理由
        valuelist.add(KINMU_BASHO); // 勤務場所
        valuelist.add(KYUKEI1_KAISHI_JIKAN_JI +KYUKEI1_KAISHI_JIKAN_HUN); // 休憩1開始時間
        valuelist.add(KYUKEI1_SHURYO_JIKAN_JI + KYUKEI1_SHURYO_JIKAN_HUN); // 休憩1終了時間
        valuelist.add(KYUKEI2_KAISHI_JIKAN_JI + KYUKEI2_KAISHI_JIKAN_HUN); // 休憩2開始時間
        valuelist.add(KYUKEI2_SHURYO_JIKAN_JI + KYUKEI2_SHURYO_JIKAN_HUN); // 休憩2終了時間
        valuelist.add(KYUKEI3_KAISHI_JIKAN_JI + KYUKEI3_KAISHI_JIKAN_HUN); // 休憩3開始時間
        valuelist.add(KYUKEI3_SHURYO_JIKAN_JI + KYUKEI3_SHURYO_JIKAN_HUN); // 休憩3終了時間
        valuelist.add(KYUKEI_JIKAN_GOUKEI); // 休憩時間合計
        valuelist.add(ZITSU_ZANGYO_JIKAN); // 実残業時間

        String[] value = valuelist.toArray(new String[valuelist.size()]);
        return value;
    }

    /**
     * 残業総時間・休憩合計時間・実残業時間を計算し、変数への格納、画面への反映を行う
     */
    private void setJikan(){

        // 時*60+分とし比較対象の時間を分に換算する
        // 換算した値を60で割ると商が時間（intの割り算の切り上げとかってどうなってるの？？？）、あまりが分
        // 計算部分メソッド化すべき…
        // ０埋めを行う

        // 残業総時間
        int zangyosoujikanminite =(((Integer.parseInt(ZANGYO_SHURYO_JIKAN_JI))*60)+(Integer.parseInt(ZANGYO_SHURYO_JIKAN_HUN)))-
                (((Integer.parseInt(ZANGYO_KAISHI_JIKAN_JI))*60)+(Integer.parseInt(ZANGYO_KAISHI_JIKAN_HUN)));
        String zangyosoujikanji =String.format("%02d",(zangyosoujikanminite / 60));
        String zangyosoujikanhun = String.format("%02d",(zangyosoujikanminite % 60));
        TextView zangyo_all_time = findViewById(R.id.zangyo_all_time);
        zangyo_all_time.setText("残業総時間："+ zangyosoujikanji + "時間" + zangyosoujikanhun + "分");
        ZANGYO_SOU_JIKAN = zangyosoujikanji + zangyosoujikanhun;

        // 休憩時間合計
        // １、２、３それぞれの差を出し合計したものを60で割る
        int kyuukei1jikanminite = (((Integer.parseInt(KYUKEI1_SHURYO_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI1_SHURYO_JIKAN_HUN)))-
                (((Integer.parseInt(KYUKEI1_KAISHI_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI1_KAISHI_JIKAN_HUN)));
        int kyuukei2jikanminite = (((Integer.parseInt(KYUKEI2_SHURYO_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI2_SHURYO_JIKAN_HUN)))-
                (((Integer.parseInt(KYUKEI2_KAISHI_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI2_KAISHI_JIKAN_HUN)));
        int kyuukei3jikanminite = (((Integer.parseInt(KYUKEI3_SHURYO_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI3_SHURYO_JIKAN_HUN)))-
                (((Integer.parseInt(KYUKEI3_KAISHI_JIKAN_JI))*60)+(Integer.parseInt(KYUKEI3_KAISHI_JIKAN_HUN)));
        int kyukeijikangoukeiminite = kyuukei1jikanminite + kyuukei2jikanminite + kyuukei3jikanminite;
        String kyukeigoukeiji = String.format("%02d",(kyukeijikangoukeiminite / 60));
        String kyukeigoukeihun = String.format("%02d",(kyukeijikangoukeiminite % 60));
        TextView kyukei_all_time = findViewById(R.id.kyukei_all_time);
        kyukei_all_time.setText("休憩時間合計："+ kyukeigoukeiji + "時間" + kyukeigoukeihun + "分");
        KYUKEI_JIKAN_GOUKEI = kyukeigoukeiji + kyukeigoukeihun;

        // 実残業時間
        // 残業総時間-休憩時間合計
        int zituszangyojikanminite = zangyosoujikanminite - kyukeijikangoukeiminite;
        String zitsuzangyojikanji = String.format("%02d",(zituszangyojikanminite / 60));
        String zitsuzangyojikanhun = String.format("%02d",(zituszangyojikanminite % 60));
        TextView zangyo_time = (TextView) findViewById(R.id.zangyo_time);
        zangyo_time.setText("実残業時間："+ zitsuzangyojikanji + "時間" + zitsuzangyojikanhun + "分");
        ZITSU_ZANGYO_JIKAN = zitsuzangyojikanji + zitsuzangyojikanhun;
    }

    /**
     * 入力内容のチェックを行い、エラーメッセージを画面へ表示する
     * @return true：正常／false：異常
     */
    private boolean check(){

        List<String> valuelist = new ArrayList<String>();

        // 各終了時間が各開始時間より後かチェック
        // 休憩時間が残業時間内かチェック
        // 休憩時間は未入力判定を行ったうえでチェックする
        if (!jikanZengoCheck((ZANGYO_KAISHI_JIKAN_JI+ZANGYO_KAISHI_JIKAN_HUN),(ZANGYO_SHURYO_JIKAN_JI+ZANGYO_SHURYO_JIKAN_HUN))){
            valuelist.add("残業終了時間は残業開始時間より遅い時間を設定してください");
        }

        String kyukeikaishijikan = KYUKEI1_KAISHI_JIKAN_JI+KYUKEI1_KAISHI_JIKAN_HUN;
        String kyukeishuryojikan = KYUKEI1_SHURYO_JIKAN_JI+KYUKEI1_SHURYO_JIKAN_HUN;
        if(!kyukeiJikanMinyuryokuCheck(kyukeikaishijikan,kyukeishuryojikan)) {
            if (!jikanZengoCheck(kyukeikaishijikan, kyukeishuryojikan)) {
                valuelist.add("休憩1終了時間は休憩1開始時間より遅い時間を設定してください");
            }
            if(!kyukeiJikanZangyoJikanNaiCheck(kyukeikaishijikan, kyukeishuryojikan)){
                valuelist.add("休憩1の時間が残業時間の範囲外です");
            }
        }

        kyukeikaishijikan = KYUKEI2_KAISHI_JIKAN_JI+KYUKEI2_KAISHI_JIKAN_HUN;
        kyukeishuryojikan = KYUKEI2_SHURYO_JIKAN_JI+KYUKEI2_SHURYO_JIKAN_HUN;
        if(!kyukeiJikanMinyuryokuCheck(kyukeikaishijikan,kyukeishuryojikan)) {
            if (!jikanZengoCheck(kyukeikaishijikan, kyukeishuryojikan)) {
                valuelist.add("休憩2終了時間は休憩2開始時間より遅い時間を設定してください");
            }
            if(!kyukeiJikanZangyoJikanNaiCheck(kyukeikaishijikan, kyukeishuryojikan)){
                valuelist.add("休憩2の時間が残業時間の範囲外です");
            }
        }

        kyukeikaishijikan = KYUKEI3_KAISHI_JIKAN_JI+KYUKEI3_KAISHI_JIKAN_HUN;
        kyukeishuryojikan = KYUKEI3_SHURYO_JIKAN_JI+KYUKEI3_SHURYO_JIKAN_HUN;
        if(!kyukeiJikanMinyuryokuCheck(kyukeikaishijikan,kyukeishuryojikan)) {
            if (!jikanZengoCheck(kyukeikaishijikan, kyukeishuryojikan)) {
                valuelist.add("休憩3終了時間は休憩3開始時間より遅い時間を設定してください");
            }
            if(!kyukeiJikanZangyoJikanNaiCheck(kyukeikaishijikan, kyukeishuryojikan)){
                valuelist.add("休憩3の時間が残業時間の範囲外です");
            }
        }

        // 申請理由入力チェック
        final EditText shinseiriyu = findViewById(R.id.shinsei_riyu);
        if(shinseiriyu.getText().toString().equals("")){
            valuelist.add("申請理由を入力してください");
        }else{
            SHINSEI_RIYU = shinseiriyu.getText().toString();
        }

        // エラーが1件でもある場合、エラーメッセージをアラートで表示する
        if(valuelist.size()!=0){

            String[] value = valuelist.toArray(new String[valuelist.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(ZangyoActivity.this);

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
     * 開始時間・終了時間の前後関係が正しいかチェックする
     * @param kaishijikan 開始時間
     * @param shuryojikan 終了時間
     * @return true：正常／false：異常
     */
    private boolean jikanZengoCheck(String kaishijikan,String shuryojikan){

        if(Integer.parseInt(kaishijikan)>=Integer.parseInt(shuryojikan)){
            return false;
        }
        return true;
    }

    /**
     * 休憩時間が未入力かチェックする
     * @param kyukeikaishijikan 休憩開始時間
     * @param kyukeishuryojikan 休憩終了時間
     * @return true：未入力／false：入力
     */
    private boolean kyukeiJikanMinyuryokuCheck(String kyukeikaishijikan,String kyukeishuryojikan){

        // 開始・終了がともに24:00の場合、未入力とする
        if(kyukeikaishijikan.equals("2400") && kyukeishuryojikan.equals("2400")){
            return true;
        }
        return false;
    }

    /**
     * 休憩時間が残業時間の範囲内かチェックする
     * @param kyukeikaishijikan 休憩開始時間
     * @param kyukeishuryojikan 休憩終了時間
     * @return true：正常／false：異常
     */
    private boolean kyukeiJikanZangyoJikanNaiCheck(String kyukeikaishijikan,String kyukeishuryojikan){

        // 残業開始時間<=休憩開始時間<=残業終了時間　かつ、残業開始時間<=休憩終了時間<=残業終了時間
        int zangyokaishijikan =Integer.parseInt(ZANGYO_KAISHI_JIKAN_JI + ZANGYO_KAISHI_JIKAN_HUN);
        int zangyoshuryojikan = Integer.parseInt(ZANGYO_SHURYO_JIKAN_JI + ZANGYO_SHURYO_JIKAN_HUN);
        int intkyukeikaishijikan = Integer.parseInt(kyukeikaishijikan);
        int intkyukeishuryojikan = Integer.parseInt(kyukeishuryojikan);

        if(zangyokaishijikan<=intkyukeikaishijikan && intkyukeikaishijikan<=zangyoshuryojikan){

            if(zangyokaishijikan<=intkyukeishuryojikan && intkyukeishuryojikan<=zangyoshuryojikan){
                return true;
            }
        }
        return false;
    }

    /**
     * Spinnerの内容を設定する
     */
    private void setSpinnerValue(){

        // adapterを作成
        String spinnerHourItems[] = {"17", "18", "19", "20","21","22","23","0","1","2","3","4","5"};
        String spinnerMiniteItems[] = {"00", "15", "30", "45"};

        ArrayAdapter<String> hourAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerHourItems);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> miniteAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerMiniteItems);
        miniteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // spinnerにadapterをセット
        Spinner zangyo_start_hour = findViewById(R.id.zangyo_start_hour);
        Spinner zangyo_start_minite = findViewById(R.id.zangyo_start_minite);
        Spinner zangyo_end_hour = findViewById(R.id.zangyo_end_hour);
        Spinner zangyo_end_minite = findViewById(R.id.zangyo_end_minite);
        Spinner kyukei1_start_hour = findViewById(R.id.kyukei1_start_hour);
        Spinner kyukei1_start_minite = findViewById(R.id.kyukei1_start_minite);
        Spinner kyukei1_end_hour = findViewById(R.id.kyukei1_end_hour);
        Spinner kyukei1_end_minite = findViewById(R.id.kyukei1_end_minite);
        Spinner kyukei2_start_hour = findViewById(R.id.kyukei2_start_hour);
        Spinner kyukei2_start_minite = findViewById(R.id.kyukei2_start_minite);
        Spinner kyukei2_end_hour = findViewById(R.id.kyukei2_end_hour);
        Spinner kyukei2_end_minite = findViewById(R.id.kyukei2_end_minite);
        Spinner kyukei3_start_hour = findViewById(R.id.kyukei3_start_hour);
        Spinner kyukei3_start_minite = findViewById(R.id.kyukei3_start_minite);
        Spinner kyukei3_end_hour = findViewById(R.id.kyukei3_end_hour);
        Spinner kyukei3_end_minite = findViewById(R.id.kyukei3_end_minite);

        zangyo_start_hour.setAdapter(hourAdapter);
        zangyo_start_minite.setAdapter(miniteAdapter);
        zangyo_end_hour.setAdapter(hourAdapter);
        zangyo_end_minite.setAdapter(miniteAdapter);
        kyukei1_start_hour.setAdapter(hourAdapter);
        kyukei1_start_minite.setAdapter(miniteAdapter);
        kyukei1_end_hour.setAdapter(hourAdapter);
        kyukei1_end_minite.setAdapter(miniteAdapter);
        kyukei2_start_hour.setAdapter(hourAdapter);
        kyukei2_start_minite.setAdapter(miniteAdapter);
        kyukei2_end_hour.setAdapter(hourAdapter);
        kyukei2_end_minite.setAdapter(miniteAdapter);
        kyukei3_start_hour.setAdapter(hourAdapter);
        kyukei3_start_minite.setAdapter(miniteAdapter);
        kyukei3_end_hour.setAdapter(hourAdapter);
        kyukei3_end_minite.setAdapter(miniteAdapter);

        // 残業時間に初期値として17時30分を設定
        zangyo_start_minite.setSelection(2);
        zangyo_end_minite.setSelection(2);

        // 休憩時間に初期値として0時を指定
        kyukei1_start_hour.setSelection(7);
        kyukei1_end_hour.setSelection(7);
        kyukei2_start_hour.setSelection(7);
        kyukei2_end_hour.setSelection(7);
        kyukei3_start_hour.setSelection(7);
        kyukei3_end_hour.setSelection(7);

        // リスナーを登録
        zangyo_start_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            //　アイテムが選択されたとき
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner zangyo_start_hour = (Spinner)parent;
                ZANGYO_KAISHI_JIKAN_JI = replaceHour((String)zangyo_start_hour.getSelectedItem());

                // 残業総時間などを計算する
                setJikan();
            }

            //　アイテムが選択されなかったとき
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        zangyo_start_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner zangyo_start_minite = (Spinner)parent;
                ZANGYO_KAISHI_JIKAN_HUN = (String)zangyo_start_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        zangyo_end_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner zangyo_end_hour = (Spinner)parent;
                ZANGYO_SHURYO_JIKAN_JI = replaceHour((String)zangyo_end_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        zangyo_end_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner zangyo_end_minite = (Spinner)parent;
                ZANGYO_SHURYO_JIKAN_HUN = (String)zangyo_end_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei1_start_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei1_start_hour = (Spinner)parent;
                KYUKEI1_KAISHI_JIKAN_JI = replaceHour((String)kyukei1_start_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kyukei1_start_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei1_start_minite = (Spinner)parent;
                KYUKEI1_KAISHI_JIKAN_HUN = (String)kyukei1_start_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei1_end_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei1_end_hour = (Spinner)parent;
                KYUKEI1_SHURYO_JIKAN_JI = replaceHour((String)kyukei1_end_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei1_end_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei1_end_minite = (Spinner)parent;
                KYUKEI1_SHURYO_JIKAN_HUN = (String)kyukei1_end_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei2_start_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei2_start_hour = (Spinner)parent;
                KYUKEI2_KAISHI_JIKAN_JI = replaceHour((String)kyukei2_start_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei2_start_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei2_start_minite = (Spinner)parent;
                KYUKEI2_KAISHI_JIKAN_HUN = (String)kyukei2_start_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei2_end_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei2_end_hour = (Spinner)parent;
                KYUKEI2_SHURYO_JIKAN_JI = replaceHour((String)kyukei2_end_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei2_end_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei2_end_minite = (Spinner)parent;
                KYUKEI2_SHURYO_JIKAN_HUN = (String)kyukei2_end_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei3_start_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei3_start_hour = (Spinner)parent;
                KYUKEI3_KAISHI_JIKAN_JI = replaceHour((String)kyukei3_start_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei3_start_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei3_start_minite = (Spinner)parent;
                KYUKEI3_KAISHI_JIKAN_HUN = (String)kyukei3_start_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei3_end_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei3_end_hour = (Spinner)parent;
                KYUKEI3_SHURYO_JIKAN_JI = replaceHour((String)kyukei3_end_hour.getSelectedItem());
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kyukei3_end_minite.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner kyukei3_end_minite = (Spinner)parent;
                KYUKEI3_SHURYO_JIKAN_HUN = (String)kyukei3_end_minite.getSelectedItem();
                setJikan();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 00時～05時を24時～29時に置き換える
     * @param hour 時間
     * @return 置き換え後時間
     */
    // 00時～05時を24時～29時に置き換える
    private String replaceHour(String hour){

        int inthour = Integer.parseInt(hour);

        if(inthour < 17){
            return String.valueOf(inthour + 24);
        }

        return String.valueOf(inthour);
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