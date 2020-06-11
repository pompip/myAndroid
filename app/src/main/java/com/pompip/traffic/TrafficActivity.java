package com.pompip.traffic;

import android.net.TrafficStats;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pompip.R;

import hugo.weaving.DebugLog;


public class TrafficActivity extends AppCompatActivity {

    private TextView tv_download;
    private TextView tv_upload;
    private TextView tv_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        tv_download = findViewById(R.id.tv_download);
        tv_upload = findViewById(R.id.tv_upload);
        tv_total = findViewById(R.id.tv_total);
        CountDownTimer countDownTimer = new CountDownTimer(1000 * 60 * 5, 1000*5) {
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        genTraffic();
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }


    long currentRT =0;
    long currentTT = 0;

    @DebugLog
    private void genTraffic() {
        long rt = TrafficStats.getTotalRxBytes();
        long tt = TrafficStats.getTotalTxBytes();

        if (currentRT!=0&&currentTT!=0){
            float rtSpeed = (rt-currentRT)*1.0f/1024/5;
            float ttSpeed = (tt-currentTT)*1.0f/1024/5;
            tv_download.setText(rtSpeed + "kb/s");
            tv_upload.setText(ttSpeed + "kb/s");
            tv_total.setText(ttSpeed+rtSpeed + "kb/s");
        }
        currentRT = rt;
        currentTT = tt;

    }
}
