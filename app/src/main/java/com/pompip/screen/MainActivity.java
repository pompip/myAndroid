package com.pompip.screen;

import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.bonree.myapplication.R;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv);
        tv.setText(getText());
        final ExecutorService executorService = Executors.newFixedThreadPool(4);
        Intent service = new Intent(this,ScreenService.class);
        startService(service);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                connectSocket();
            }
        });

    }


    public String getText() {
        return "我没有被劫持,lala";
    }

    private void connectSocket() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalSocket socket = new LocalSocket();
        try {
            socket.connect(new LocalSocketAddress("singleTouch"));
            Log.e(TAG, "bind success");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            byte[] bytes = new byte[2048];
            int l = 0;
            while ((l = bufferedInputStream.read(bytes)) != -1) {
                Log.e(TAG, l + "input");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
