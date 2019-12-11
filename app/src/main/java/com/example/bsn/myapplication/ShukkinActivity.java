package com.example.bsn.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShukkinActivity extends AppCompatActivity implements LocationListener {

    private final String DATE_PATTERN_SLASH = "yyyy/MM/dd HH:mm:ss";
    private final String DATE_PATTERN_KANJI = "yyyy年MM月dd日 HH時mm分ss秒";
    //private final String TIMEZONE_TOKYO = "Asia/Tokyo";

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shukkin);

        setActionBar((Toolbar)findViewById(R.id.toolbar));

        // 申請ボタン操作不能化
        Button shukkinBtn = (Button)findViewById(R.id.shukkinBtn);
        shukkinBtn.setEnabled(false);

        // 現在時刻取得
        Date nowDate = new Date();
        //TimeZone timeZone = TimeZone.getTimeZone(TIMEZONE_TOKYO);
//        SimpleDateFormat formatSlash = new SimpleDateFormat(DATE_PATTERN_SLASH);
//        formatSlash.setTimeZone(timeZone);
//        String nowDateSlash = formatSlash.format(nowDate);

        SimpleDateFormat formatKanji = new SimpleDateFormat(DATE_PATTERN_KANJI);
        //formatKanji.setTimeZone(timeZone);
        String nowDateKanji = formatKanji.format(nowDate);

        TextView textViewNowDate = (TextView)findViewById(R.id.nowDate);
        textViewNowDate.setText(nowDateKanji);
        TextView textViewNowPosition = (TextView)findViewById(R.id.nowPosition);
        textViewNowPosition.setText("（現在地取得中）");


        // 現在位置取得
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
        }

        // 此処での設定はやめる。現在地取得時に設定する。
        //String nowPosition = "どこ";
        //TextView textViewNowPosition = (TextView)findViewById(R.id.nowPosition);
        //textViewNowPosition.setText(nowPosition);



        // 申請ボタン押下
        shukkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ShukkinActivity.this);
                builder.setMessage("出勤を申請しますか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // インターフェースの実装
                                HttpTaskShukkin httpTask = new HttpTaskShukkin(new HttpTaskShukkin.AsyncTaskCallBack() {
                                    @Override
                                    // 非同期処理の値を引き継ぐ
                                    public void loginCallBack(String st) {
                                        /**
                                        try {

                                            TextView textViewNowDate = (TextView) findViewById(R.id.nowDate);
                                            String nowDate = (String) textViewNowDate.getText();
                                            // 漢字フォーマットをスラッシュフォーマットに変換
                                            SimpleDateFormat formatKanji = new SimpleDateFormat(DATE_PATTERN_KANJI);
                                            Date nowDateKnji = formatKanji.parse(nowDate);
                                            SimpleDateFormat formatSlash = new SimpleDateFormat(DATE_PATTERN_SLASH);
                                            nowDate = formatSlash.format(nowDateKnji);

                                            TextView textViewNowPosition = (TextView) findViewById(R.id.nowPosition);
                                            String nowPosition = (String) textViewNowPosition.getText();

                                            sendShukinSinsei(nowDate, nowPosition);
                                             */

                                            Toast.makeText(ShukkinActivity.this, "申請しました", Toast.LENGTH_LONG).show();

                                            // メニュー画面へ遷移
                                            Intent intent = getIntent();
                                            UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
                                            intent = new Intent(getApplication(), MenuActivity.class);
                                            intent.putExtra("userInfo", userInfo);
                                            startActivity(intent);

                                        /**
                                        } catch (Exception e) {
                                            // 変換にエラーがでたらエラーダイアログ
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ShukkinActivity.this);
                                            builder.setMessage("エラーが発生しました")
                                                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // 何もしない
                                                        }
                                                    });
                                            builder.show();
                                        }
                                         */
                                    }
                                });
                                // 非同期処理の実行
                                httpTask.execute(makeDTO());
                            }
                        });

                builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 何もしないでダイアログを閉じて、出勤確認画面へ戻る。
                    }
                });
                builder.show();
            }
        });
    }

    /**
     * 申請内容を作成し返却する
     * @return 申請内容
     */
    // 内部ストレージ出力ファイル内容作成
    private ShukkinInfoDTO makeDTO(){

        ShukkinInfoDTO shukkinInfo = new ShukkinInfoDTO();

        try {
            TextView textViewNowDate = (TextView) findViewById(R.id.nowDate);
            String nowDate = (String) textViewNowDate.getText();
            // 漢字フォーマットをスラッシュフォーマットに変換
            SimpleDateFormat formatKanji = new SimpleDateFormat(DATE_PATTERN_KANJI);
            Date nowDateKnji = formatKanji.parse(nowDate);
            SimpleDateFormat formatSlash = new SimpleDateFormat(DATE_PATTERN_SLASH);
            nowDate = formatSlash.format(nowDateKnji);

            TextView textViewNowPosition = (TextView) findViewById(R.id.nowPosition);
            String nowPosition = (String) textViewNowPosition.getText();

            shukkinInfo.setShukkinDate(nowDate); // 出勤時刻
            shukkinInfo.setShukkinPlace(nowPosition); // 現在地

            shukkinInfo.setApplyDiv("1"); //申請区分

            Date date = new Date();
            String dateStr = new SimpleDateFormat("yyyyMMddhhmmss").format(date);
            shukkinInfo.setApplyDate(dateStr); //申請日時

            // ユーザID（DTOから取得）
            Intent intent = getIntent();
            UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
            shukkinInfo.setUserId(userInfo.getUserId());

        } catch (Exception e) {
            // 変換にエラーがでたらエラーダイアログ
            AlertDialog.Builder builder = new AlertDialog.Builder(ShukkinActivity.this);
            builder.setMessage("エラーが発生しました")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // 何もしない
                        }
                    });
            builder.show();
        }

        return shukkinInfo;
    }

    private void sendShukinSinsei(String nowDate, String nowPositon) {

        try {
            // 出勤申請を登録
            Context context = getApplicationContext();
            PutRecordRequest putRecordRequest = new PutRecordRequest(context, getIntent());

            String[] request = new String[2];
            request[0] = nowDate;
            request[1] = nowPositon;

            boolean rtnFlg = putRecordRequest.putRecord(PutRecordRequest.PUT_KBN_SHUKKIN, request);
            if (rtnFlg) {
                // なにもしない
                ;
            } else {
                throw new Exception();
            }

            // 登録できたら確認ダイアログ

            Toast.makeText(ShukkinActivity.this, "申請しました", Toast.LENGTH_LONG).show();
            // メニュー画面へ遷移
            Intent intent = getIntent();
            UserInfoDTO userInfo = (UserInfoDTO) intent.getSerializableExtra("userInfo");
            intent = new Intent(getApplication(), MenuActivity.class);
            intent.putExtra("userInfo", userInfo);
            startActivity(intent);

//            AlertDialog.Builder builder = new AlertDialog.Builder(ShukkinActivity.this);
//            builder.setMessage(nowDate + "\n" + nowPositon + "\n" + "出勤を確認しました")
//                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // メニュー画面に戻る
//                            Intent intent = new Intent(getApplication(), MenuActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//            builder.show();
        } catch (Exception e) {
            // 登録にエラーがでたらエラーダイアログ
            AlertDialog.Builder builder = new AlertDialog.Builder(ShukkinActivity.this);
            builder.setMessage("エラーが発生しました")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // 何もしない
                        }
                    });
            builder.show();
        }

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

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enable");
        } else {
            // GPS を設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
    }

    /**
     * 結果の受け取り
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissins, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
            } else {
                // それでも拒否されたときの対応
                Toast toast = Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        String positionAddr = "";


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        StringBuffer strAddr = new StringBuffer();
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        //Geocoder gcoder = new Geocoder(this, Locale.JAPAN);
        try {
            List<Address> lstAddrs = gcoder.getFromLocation(latitude, longitude, 2); // なぜか２以上でないと住所が取れない
            if (!lstAddrs.isEmpty()) {

                Address addr = lstAddrs.get(0);

                if (addr.getMaxAddressLineIndex() == 0) {

                    if ("日本、".equals(addr.getAddressLine(0).substring(0,3))) {
                        positionAddr = addr.getAddressLine(0).substring(3, addr.getAddressLine(0).length());
                    } else {
                        positionAddr = addr.getAddressLine(0);
                    }

                } else {

                    int idx = addr.getMaxAddressLineIndex();
                    for (int i = 1; i <= idx; i++) {
                        positionAddr = positionAddr + addr.getAddressLine(i);
                    }
                }
            } else {
                positionAddr = "取得不可";
            }

        } catch (IOException e) {
            positionAddr = "取得不可";

        } catch (Exception e) {
            positionAddr = "取得不可";

        }

        TextView textViewNowPosition = (TextView)findViewById(R.id.nowPosition);
        textViewNowPosition.setText(positionAddr);

        // 申請ボタン操作可能化
        Button shukkinBtn = (Button)findViewById(R.id.shukkinBtn);
        shukkinBtn.setEnabled(true);
    }

    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

}
