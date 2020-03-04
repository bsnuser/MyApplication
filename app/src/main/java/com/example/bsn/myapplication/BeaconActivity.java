package com.example.bsn.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer {

    /**************************************************
     * 変数定義
     **************************************************/


    // BeaconManager型変数の宣言
    BeaconManager beaconManager;

    // uuidの指定
    private final String uuidString = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    Identifier uuid = Identifier.parse(uuidString);

    // ビーコンのフォーマット設定
    private final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";


    /**************************************************
     * AppCompatActivity内のメソッドをoverride
     **************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        final PackageManager packageManager = getPackageManager();

        // デバイスのBLE対応チェック
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            // 未対応の場合、Toast表示
            showToast("このデバイスはBLE未対応です", Toast.LENGTH_LONG);
        }
        // API 23以上かのチェック
        if (Build.VERSION.SDK_INT >= 23) {
            // パーミッションの要求
            checkPermission();
        }

        // ビーコンマネージャのインスタンスを生成
        beaconManager = BeaconManager.getInstanceForApplication(this);

        // BeaconManagerの設定
        beaconManager.getBeaconParsers().add((new BeaconParser()).setBeaconLayout(IBEACON_FORMAT));

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.beaconManager.bind(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.beaconManager.unbind(this);
    }

    /**************************************************
     * BeaconConsumer内のメソッドをoverride
     **************************************************/
    @Override
    public void onBeaconServiceConnect(){
        try {
            // モニタリング開始
            beaconManager.startMonitoringBeaconsInRegion(new Region("MyBeacon", uuid, null, null));
        } catch(RemoteException e){
            e.printStackTrace();
        }
        beaconManager.addMonitorNotifier((MonitorNotifier)(new MonitorNotifier() {

            // 領域内に侵入した時に呼ばれる
            @Override
            public void didEnterRegion(Region region) {
                try{
                    // レンジングの開始
                    beaconManager.startRangingBeaconsInRegion(region);
                    TextView statusTextView = findViewById(R.id.status2);
                    statusTextView.setText("出社しました");
                } catch(RemoteException e){
                    e.printStackTrace();
                }
            }

            // 領域外に退出した時に呼ばれる
            @Override
            public void didExitRegion(Region region){
                try {
                    // レンジングの停止
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                    TextView statusTextView = findViewById(R.id.status2);
                    statusTextView.setText("退社しました");
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }

            // 領域への侵入/退出のステータスが変化した時に呼ばれる
            @Override
            public void didDetermineStateForRegion(int i, Region region){
                // なにもしない
            }
        }));

        // レンジングの通知受け取り処理
        // とりあえず処理なし

    }

    /**************************************************
     * メソッド
     **************************************************/
    // トースト表示のメソッド
    private void showToast(String text, int length) {

        // トーストの生成と表示
        Toast toast = Toast.makeText(this, text, length);
        toast.show();
    }

    // パーミッションの許可チェック
    @RequiresApi(Build.VERSION_CODES.M)
    private void checkPermission() {

        // パーミッション未許可の時
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // パーミッションの許可ダイアログの表示
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }




}
