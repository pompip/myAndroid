package com.pompip.touchserver;

import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.os.IBinder;
import android.os.IInterface;
import android.system.Os;
import android.util.Log;


import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenEncoder implements Runnable {
    private static final int DEFAULT_WIDTH = 360;
    private static final String TAG = "RecordEncoder";
    private MediaCodec mCodec;
    private final int[] mDisplaySize;
    private final FileDescriptor mFd;
    private ByteBuffer mLenBuffer = ByteBuffer.allocate(4);
    private boolean mRecording;
    private VirtualDisplay mVirtualDisplay;

    ScreenEncoder(int width, int bitrate, FileDescriptor fileDescriptor) throws IOException {
        this.mFd = fileDescriptor;
        this.mDisplaySize = getDisplaySize();
        if (this.mDisplaySize != null) {
            if (width == 0 || width > this.mDisplaySize[0]) {
                width = DEFAULT_WIDTH;
            }
            int round = Math.round((((float) this.mDisplaySize[1]) * ((float) width)) / ((float) this.mDisplaySize[0]));
            if (round % 2 == 1) {
                round++;
            }
            String sb = "out video width = " +width +",height = " +round +",bitrate = " +bitrate;
            Log.e(TAG,sb);
            configure(width, round, bitrate, this.mDisplaySize);
        }
    }

    private void configure(int width, int height, int bitrate, int[] iArr) throws IOException {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", "video/avc");
        mediaFormat.setInteger("bitrate", bitrate);
        mediaFormat.setInteger("frame-rate", 30);
        mediaFormat.setInteger("color-format", 2130708361);
        mediaFormat.setInteger("i-frame-interval", 10);
        mediaFormat.setInteger("profile", 1);
        mediaFormat.setInteger("level", 256);
        mediaFormat.setLong("repeat-previous-frame-after", 200000);
        mediaFormat.setInteger("width", width);
        mediaFormat.setInteger("height", height);
        this.mCodec = MediaCodec.createEncoderByType("video/avc");
        this.mCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mVirtualDisplay = VirtualDisplay.createVirtualDisplay("recordScreen", this.mCodec.createInputSurface(),
                new Rect(0, 0, iArr[0], iArr[1]), new Rect(0, 0, width, height));
    }

    public synchronized void stopRecord() {
        this.mRecording = false;
    }


    @Override
    public void run() {
        Log.w(TAG, "start record !");
        if (this.mDisplaySize != null) {
            String info = "width = " +
                    this.mDisplaySize[0] +
                    "，height = " +
                    this.mDisplaySize[1];
            Log.e(TAG,"info = " +info);
            writeFd(ByteBuffer.wrap(info.getBytes()));
        }
        this.mCodec.start();
        this.mRecording = true;
        try {
            encode();
        } finally {
            stopRecord();
            this.mCodec.stop();
            this.mCodec.release();
            this.mVirtualDisplay.destroyDisplay();
            this.mCodec = null;
            this.mVirtualDisplay = null;
        }
    }


    private void encode() {
        BufferInfo bufferInfo = new BufferInfo();
        ByteBuffer[] byteBufferArr = null;
        do {
            int dequeueOutputBuffer = this.mCodec.dequeueOutputBuffer(bufferInfo, -1);
            if (dequeueOutputBuffer >= 0) {
                if (byteBufferArr == null) {
                    byteBufferArr = this.mCodec.getOutputBuffers();
                }
                boolean writeFd = writeFd(byteBufferArr[dequeueOutputBuffer]);
                this.mCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                if (!writeFd) {
                    return;
                }
            } else if (dequeueOutputBuffer == -3) {
                Log.w(TAG, "MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED");
                byteBufferArr = null;
            } else if (dequeueOutputBuffer == -2) {
                Log.w(TAG, "MediaCodec.INFO_OUTPUT_FORMAT_CHANGED");
                MediaFormat outputFormat = this.mCodec.getOutputFormat();
                String sb = "output width: " +
                        outputFormat.getInteger("width") +
                        " height : " +
                        outputFormat.getInteger("height");
                Log.w(TAG, sb);
            }
            if (!this.mRecording) {
                return;
            }
        } while ((bufferInfo.flags & 4) == 0);
    }


    private boolean writeFd(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        this.mLenBuffer.clear();
        this.mLenBuffer.putInt(remaining);
        this.mLenBuffer.flip();
        try {
            Os.write(this.mFd, this.mLenBuffer);
            while (remaining > 0) {
                try {
                    remaining -= Os.write(this.mFd, byteBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private int[] getDisplaySize() {
        try {
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager")
                    .getDeclaredMethod("getService", String.class).invoke(null, "display");
            IInterface iInterface = (IInterface) Class.forName("android.hardware.display.IDisplayManager$Stub")
                    .getMethod("asInterface", IBinder.class).invoke(null, iBinder);
            Object invoke = iInterface.getClass().getMethod("getDisplayInfo", Integer.TYPE).invoke(iInterface, 0);
            Class cls = invoke.getClass();
            int logicalWidth = cls.getDeclaredField("logicalWidth").getInt(invoke);
            int logicalHeight = cls.getDeclaredField("logicalHeight").getInt(invoke);
            return new int[]{logicalWidth, logicalHeight};
        } catch (Exception e) {
            Log.e(TAG, "getDisplaySize: ", e);
            return null;
        }
    }
}
