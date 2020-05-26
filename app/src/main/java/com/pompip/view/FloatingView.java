package com.pompip.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.bonree.myapplication.R;
import com.pompip.screen.DataListener;
import com.pompip.screen.ScreenService;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 悬浮窗view
 */
public class FloatingView extends FrameLayout {
    private Context mContext;
    private View mView;

    private int mTouchStartX, mTouchStartY;//手指按下时坐标
    private WindowManager.LayoutParams mParams;
    private FloatingManager mWindowManager;

    public FloatingView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.floating_view, null);
        genTexureView(mView);
        mWindowManager = FloatingManager.getInstance(mContext);
    }

    public void show() {
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = 0;
        mParams.y = 100;
        //总是出现在应用程序窗口之上
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        mParams.width = 600;
        mParams.height = 800;

        mWindowManager.addView(mView, mParams);

        ScreenService.setListener(dataListener);
    }

    public void hide() {
        mWindowManager.removeView(mView);
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mParams.x += (int) event.getRawX() - mTouchStartX;
                    mParams.y += (int) event.getRawY() - mTouchStartY;//相对于屏幕左上角的位置
                    mWindowManager.updateView(mView, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
    private MediaCodec mCodec;

    DataListener dataListener = new DataListener() {
        @Override
        public void sendBytes(byte[] byteString) {

            onFrame(byteString,0,byteString.length);
        }
    };

    void genTexureView(View view){
        TextureView texture_view = view.findViewById(R.id.texture_view);


        texture_view.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    String type = "video/avc";
                    mCodec = MediaCodec.createDecoderByType(type);
                    MediaFormat videoFormat = MediaFormat.createVideoFormat(type, 400, 600);
                    mCodec.configure(videoFormat,new Surface(surface),null,0);
                    mCodec.start();
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


    int mCount;
    int TIME_INTERNAL =30;

    public boolean onFrame(byte[] buf, int offset, int length) {
        Log.e("Media", "onFrame Thread:" + Thread.currentThread().getId());
        // Get input buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        int inputBufferIndex = mCodec.dequeueInputBuffer(100);

        Log.e("Media", "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
                         ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//            ByteBuffer inputBuffer = mCodec.getInputBuffer(inputBufferIndex);
//                         inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
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
