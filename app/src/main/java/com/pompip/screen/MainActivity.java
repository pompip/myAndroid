package com.pompip.screen;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;

import com.example.bonree.myapplication.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    private MediaCodec mCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv);
        tv.setText(getText());

        startService(new Intent(this, ScreenService.class));
        TextureView texture_view = findViewById(R.id.texture_view);



        texture_view.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    String type = "video/avc";
                    mCodec = MediaCodec.createDecoderByType(type);
                    MediaFormat videoFormat = MediaFormat.createVideoFormat(type, 400, 600);
                    mCodec.configure(videoFormat,new Surface(surface),null,0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,ScreenService.class));
    }

    public String getText() {
        return "我没有被劫持,lala";
    }


    int mCount;
    int TIME_INTERNAL =30;

    public boolean onFrame(byte[] buf, int offset, int length) {
                 Log.e("Media", "onFrame start");
                 Log.e("Media", "onFrame Thread:" + Thread.currentThread().getId());
                 // Get input buffer index
                 ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
                 int inputBufferIndex = mCodec.dequeueInputBuffer(100);

                 Log.e("Media", "onFrame index:" + inputBufferIndex);
                 if (inputBufferIndex >= 0) {
                         ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                         inputBuffer.clear();inputBuffer.put(buf, offset, length);
                         mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                                         * TIME_INTERNAL, 0);
                         mCount++;
                     } else {
                         return false;
                     }

                 // Get output buffer index
                 MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                 int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 100);
                 while (outputBufferIndex >= 0) {
                         mCodec.releaseOutputBuffer(outputBufferIndex, true);
                         outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
                     }
                 Log.e("Media", "onFrame end");
                 return true;
             }


}
