package com.pompip.screen;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pompip.R;
import com.pompip.view.FloatingManager;
import com.pompip.view.FloatingView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingView floatingView = new FloatingView(MainActivity.this);
                floatingView.show();
            }

        });

        executorService = Executors.newFixedThreadPool(4);

        startService(new Intent(this, ScreenService.class));


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ScreenService.class));
    }

}
