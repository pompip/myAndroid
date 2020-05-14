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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;


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
                connect();
            }
        });
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


            BufferedSource buffer = Okio.buffer(Okio.source(socket.getInputStream()));

            while (true){
                byte[] byteString = buffer.readByteArray();
                if (webSocket!=null){
                    Log.e(TAG,"to:"+byteString.length);
                    webSocket.send(new ByteString(byteString));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    WebSocket webSocket;
    void connect(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("http://192.168.2.200:5000/echo").build();
        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d(TAG, "onClosing() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.d(TAG, "onFailure() called with: webSocket = [" + webSocket + "], t = [" + t + "], response = [" + response + "]");
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], text = [" + text + "]");
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], bytes = [" + bytes + "]");
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen() called with: webSocket = [" + webSocket + "], response = [" + response + "]");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
