package com.example.bsn.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import java.util.Calendar;
import android.app.DialogFragment;

public class ZangyoActivityCalendarDialog extends DialogFragment {

    // ダイアログが生成された時に呼ばれるメソッド ※必須
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 今日の日付のカレンダーインスタンスを取
        final Calendar calendar = Calendar.getInstance();

        // ダイアログ生成  DatePickerDialogのBuilderクラスを指定してインスタンス化します
        DatePickerDialog dateBuilder =
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                // 選択された年・月・日を整形 ※月は0-11なので+1している
                                String dateStr = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;

                                // ZangyoActivityのインスタンスを取得し適宜のメソッドを実行
                                ZangyoActivity zangyoActivity = (ZangyoActivity) getActivity();
                                zangyoActivity.setTextView(dateStr);
                            }
                        },
                        calendar.get(Calendar.YEAR), // 初期選択年
                        calendar.get(Calendar.MONTH), // 初期選択月
                        calendar.get(Calendar.DAY_OF_MONTH) // 初期選択日
                );

        // dateBulderを返す
        return dateBuilder;
    }
}
