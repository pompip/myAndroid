package com.pompip.screen;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.pompip.touchserver.TouchEventServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
    private WebSocket webSocket;
    private LocalServerSocket localServerSocket;

    public ScreenService() {
    }

    private ExecutorService executorService;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            localServerSocket = new LocalServerSocket(TouchEventServer.HOST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(8);
        killScreen();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                startScreenService();
                createLocalSocket();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        killScreen();
//        if (webSocket!=null){
//            webSocket.close(1,"close");
//        }

    }

    private void killScreen() {
        Log.e(TAG, "killScreen start " );
        CommandResult result = Shell.SU.run("ps -ef |grep TouchEventServer");

        List<String> stdoutList = result.stdout;
        for (String stdout:stdoutList){
            Log.e(TAG,stdout);
            Matcher matcher = Pattern.compile("\\d+").matcher(stdout);
            if (matcher.find()) {
                String pid = matcher.group();
                CommandResult run = Shell.SU.run("kill " + pid);
                Log.e(TAG, "killScreen: " + run.getStdout());
            }
        }



    }

    private void startScreenService() {
        String packageCodePath = getPackageCodePath();
        Log.e(TAG, packageCodePath);
        final String command1 = "export CLASSPATH=" + packageCodePath;
        final String command2 = "app_process /system/bin com.pompip.touchserver.TouchEventServer h264 400 1000000";

        CommandResult run = Shell.SU.run(command1, command2);
        Log.e(TAG, "startScreenService: " + run.getStdout());

    }

    private void createLocalSocket() {
        BufferedSource buffer;
        LocalSocket socket;
        try {
            Log.e(TAG, "connect waiting :" );
            socket = localServerSocket.accept();

            Log.e(TAG, "connect success :" + socket.isConnected());
            buffer = Okio.buffer(Okio.source(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        while (socket.isConnected()) {
            try {
                int len = buffer.readInt();
                Log.e(TAG, "to:" + len);
                final byte[] byteString = buffer.readByteArray(len);
                if (webSocket != null) {
                    webSocket.send(ByteString.of(byteString));
                }
                if (dataListener!=null){
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            dataListener.sendBytes(byteString);
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    void connect() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("http://java.asuscomm.com:6001/echo").build();

        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
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


            }
        });
    }


    static DataListener dataListener;
    public static void setListener(DataListener dataListener){
        ScreenService.dataListener = dataListener;
    }
}
