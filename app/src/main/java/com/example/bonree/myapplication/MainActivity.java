package com.example.bonree.myapplication;

import android.media.MediaPlayer;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hlq.touchserver.TouchEventServer;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.jaredrummler.android.shell.StreamGobbler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv);
        tv.setText(getText());
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String packageCodePath = getPackageCodePath();
                Log.e(TAG, packageCodePath);

                    String classPath = packageCodePath.replace("base.apk", "");
//                CommandResult run = Shell.SU.run("export CLASSPATH=" + classPath,
//                        "app_process " + classPath + " com.hlq.touchserver.TouchEventServer");
                String command1 = "export CLASSPATH=" + classPath;
                String command2 = "app_process " + classPath + " com.hlq.touchserver.TouchEventServer";

                CommandResult run = Shell.SU.run(command1 + " && " + command2);
                if (run.isSuccessful()){
                    Log.e(TAG, "run: "+run.getStdout() );
                }else {
                    Log.e(TAG, "run: "+run.getStderr() );
                }


//                TouchEventServer.main(new String[]{"h264","400"});
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

    private void connectSocket(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalSocket socket = new LocalSocket();
        try {
            socket.connect(new LocalSocketAddress("singleTouch"));
            Log.e(TAG,"bind success");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            byte[] bytes = new byte[2048];
            int l =0;
            while ((l = bufferedInputStream.read(bytes))!=-1){
                Log.e(TAG,l+"input");
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

    void createPlayer() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        VideoPlayerView ijkPlayer = findViewById(R.id.player);
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {

            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

            }
        });
        ijkPlayer.setVideoPath(path);
    }


    void distory() {
        IjkMediaPlayer.native_profileEnd();
    }


}
