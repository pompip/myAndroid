package com.hlq.touchserver;

import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.system.Os;
import android.util.Log;


import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintStream;
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

    ScreenEncoder(int i, int i2, FileDescriptor fileDescriptor) throws IOException {
        this.mFd = fileDescriptor;
        this.mDisplaySize = getDisplaySize();
        if (this.mDisplaySize != null) {
            if (i == 0 || i > this.mDisplaySize[0]) {
                i = DEFAULT_WIDTH;
            }
            int round = Math.round((((float) this.mDisplaySize[1]) * ((float) i)) / ((float) this.mDisplaySize[0]));
            if (round % 2 == 1) {
                round++;
            }
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("out video width = ");
            sb.append(i);
            sb.append(",height = ");
            sb.append(round);
            sb.append(",bitrate = ");
            sb.append(i2);
            printStream.println(sb.toString());
            configure(i, round, i2, this.mDisplaySize);
        }
    }

    private void configure(int i, int i2, int i3, int[] iArr) throws IOException {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", "video/avc");
        mediaFormat.setInteger("bitrate", i3);
        mediaFormat.setInteger("frame-rate", 30);
        mediaFormat.setInteger("color-format", 2130708361);
        mediaFormat.setInteger("i-frame-interval", 10);
        mediaFormat.setInteger("profile", 1);
        mediaFormat.setInteger("level", 256);
        mediaFormat.setLong("repeat-previous-frame-after", 200000);
        mediaFormat.setInteger("width", i);
        mediaFormat.setInteger("height", i2);
        this.mCodec = MediaCodec.createEncoderByType("video/avc");
        this.mCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mVirtualDisplay = VirtualDisplay.createVirtualDisplay("recordScreen", this.mCodec.createInputSurface(), new Rect(0, 0, iArr[0], iArr[1]), new Rect(0, 0, i, i2));
    }

    public synchronized void stopRecord() {
        this.mRecording = false;
    }


    public void run() {
        Log.w(TAG, "start record !");
        if (this.mDisplaySize != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("width = ");
            sb.append(this.mDisplaySize[0]);
            sb.append("，height = ");
            sb.append(this.mDisplaySize[1]);
            String sb2 = sb.toString();
            PrintStream printStream = System.out;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("info = ");
            sb3.append(sb2);
            printStream.println(sb3.toString());
            writeFd(ByteBuffer.wrap(sb2.getBytes()));
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
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("output width: ");
                sb.append(outputFormat.getInteger("width"));
                sb.append(" height : ");
                sb.append(outputFormat.getInteger("height"));
                Log.w(str, sb.toString());
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
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", new Class[]{String.class}).invoke(null, new Object[]{"display"});
            IInterface iInterface = (IInterface) Class.forName("android.hardware.display.IDisplayManager$Stub").getMethod("asInterface", new Class[]{IBinder.class}).invoke(null, new Object[]{iBinder});
            Object invoke = iInterface.getClass().getMethod("getDisplayInfo", new Class[]{Integer.TYPE}).invoke(iInterface, new Object[]{Integer.valueOf(0)});
            Class cls = invoke.getClass();
            return new int[]{cls.getDeclaredField("logicalWidth").getInt(invoke), cls.getDeclaredField("logicalHeight").getInt(invoke)};
        } catch (Exception e) {
            Log.e(TAG, "getDisplaySize: ", e);
            return null;
        }
    }
}
