package com.pompip.screen;

import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.bonree.myapplication.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv);
        tv.setText(getText());

        Intent service = new Intent(this, ScreenService.class);
        startService(service);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });


    }


    public String getText() {
        return "我没有被劫持,lala";
    }

    private void connectSocket(WebSocket webSocket) {
        LocalSocket socket = new LocalSocket();
        BufferedSource buffer;
        try{
            socket.connect(new LocalSocketAddress("singleTouch"));
            buffer = Okio.buffer(Okio.source(socket.getInputStream()));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        Log.e(TAG, "bind success");
        while (true) {

            try {
                byte[] byteString = buffer.readByteArray(18);
                if (webSocket != null) {
                    Log.e(TAG, "to:" + byteString.length);
                    webSocket.send(new ByteString(byteString));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    void connect() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("http://192.168.1.133:5000/echo").build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                Log.e(TAG, "onClosed() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                Log.e(TAG, "onClosing() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "onFailure() called with: webSocket = [" + webSocket + "], t = [" + t + "], response = [" + response + "]");
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.e(TAG, "onMessage() called with: webSocket = [" + webSocket + "], text = [" + text + "]");
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.e(TAG, "onMessage() called with: webSocket = [" + webSocket + "], bytes = [" + bytes + "]");
            }

            @Override
            public void onOpen(@NotNull final WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                Log.e(TAG, "onOpen() called with: webSocket = [" + webSocket + "], response = [" + response + "]");

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        connectSocket(webSocket);
                    }
                });
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
