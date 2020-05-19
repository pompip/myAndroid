package com.pompip.screen;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public class ScreenService extends Service {
    private static final String TAG = "ScreenService";
    public ScreenService() {
    }
    ExecutorService executorService ;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        executorService =Executors.newFixedThreadPool(4);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                killScreen();
                startScreen();
                connect();
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private void killScreen(){
        CommandResult result = Shell.SU.run("ps -ef |grep TouchEventServer");
        String stdout = result.getStdout();

        Matcher matcher = Pattern.compile("\\d+").matcher(stdout);
        if (matcher.find()){
            String pid = matcher.group();
            CommandResult run = Shell.SU.run("kill " + pid);
            Log.e(TAG, "killScreen: " +run.getStdout());
        };



    }

    private void startScreen(){
        String packageCodePath = getPackageCodePath();
        Log.e(TAG, packageCodePath);
        final String command1 = "export CLASSPATH=" + packageCodePath ;
        final String command2 = "app_process /system/bin com.pompip.touchserver.TouchEventServer h264 400 1000";

        CommandResult run = Shell.SU.run(command1, command2);
        Log.e(TAG, "startScreen: "+run.getStdout() );

    }
    private void connectSocket(WebSocket webSocket) {
        LocalSocket socket = new LocalSocket();
        BufferedSource buffer;
        try{

            socket.connect(new LocalSocketAddress("singleTouch"));
            Log.e(TAG, "connect success :"+socket.isConnected());
            buffer = Okio.buffer(Okio.source(socket.getInputStream()));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }


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
        Request request = new Request.Builder().url("http://192.168.2.200:5000/echo").build();
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
}
