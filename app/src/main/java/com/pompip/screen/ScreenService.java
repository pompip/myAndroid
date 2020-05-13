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

public class ScreenService extends Service {
    private static final String TAG = "ScreenService";
    public ScreenService() {
    }
    ExecutorService executorService ;
    Process process ;
    private ActivityManager mActivityManager;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mActivityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info:runningAppProcesses){
            Log.e(TAG, "onStartCommand: "+info.processName +" pid:"+info.pid );
        }
        executorService =Executors.newFixedThreadPool(4);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String packageCodePath = getPackageCodePath();
                Log.e(TAG, packageCodePath);

                final String command1 = "export CLASSPATH=" + packageCodePath ;
                final String command2 = "app_process /system/bin com.pompip.touchserver.TouchEventServer h264 336";

//                CommandResult run = Shell.SU.run(command1, command2);


                try {
                    process =Runtime.getRuntime().exec("su");

                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                                String line;
                                while ((line = errorReader.readLine()) != null) {
                                    Log.e(TAG, line);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = errorReader.readLine()) != null) {
                                    Log.e(TAG, line);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try (BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                                outputStream.write(command1);
                                outputStream.write("\n");
                                outputStream.flush();
                                outputStream.write(command2);
                                outputStream.write("\n");
                                outputStream.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved: close" );
        process.destroy();

        executorService.shutdown();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (process!=null){
            process.destroy();
        }
    }
}
