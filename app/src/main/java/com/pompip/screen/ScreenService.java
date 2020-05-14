package com.pompip.screen;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

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

public class ScreenService extends Service {
    private static final String TAG = "ScreenService";
    public ScreenService() {
    }
    ExecutorService executorService ;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private void killScreen(){
        CommandResult result = Shell.SU.run("ps -ef |grep TouchEventServer");
        String stdout = result.getStdout();
        Log.e(TAG,"ps:"+stdout);
        Matcher matcher = Pattern.compile("\\d+").matcher(stdout);
        if (matcher.find()){
            String pid = matcher.group();
            Shell.SU.run("kill "+pid);
        };
    }

    private void startScreen(){
        String packageCodePath = getPackageCodePath();
        Log.e(TAG, packageCodePath);
        final String command1 = "export CLASSPATH=" + packageCodePath ;
        final String command2 = "app_process /system/bin com.pompip.touchserver.TouchEventServer h264 336";

        CommandResult run = Shell.SU.run(command1, command2);
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        killScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killScreen();
    }
}
