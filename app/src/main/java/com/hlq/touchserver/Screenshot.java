package com.hlq.touchserver;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.Log;
import com.hlq.touchserver.wrappers.ServiceManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class Screenshot implements Runnable {
    private static final String TAG = "Screenshot";
    private static final int WIDTH = 360;
    private OutputStream mOutputStream;
    private Method mScreenshotMethod;
    private ServiceManager mServiceManager;
    private boolean mStopped = false;

    Screenshot(ServiceManager serviceManager, OutputStream outputStream) {
        this.mServiceManager = serviceManager;
        this.mOutputStream = outputStream;
    }

    public void run() {
        int[] displayInfo = this.mServiceManager.getDisplayManager().getDisplayInfo();
        int i = displayInfo[0];
        int i2 = displayInfo[1];
        int i3 = displayInfo[2];
        StringBuilder sb = new StringBuilder();
        sb.append("width = ");
        sb.append(i);
        sb.append("，height = ");
        sb.append(i2);
        sb.append("，rotation = ");
        sb.append(i3);
        String sb2 = sb.toString();
        Rect rect = new Rect(0, 0, i, i2);
        try {
            writeBytes(sb2.getBytes());
            int i4 = (int) (((float) i2) / ((((float) i) * 1.0f) / 360.0f));
            PrintStream printStream = System.out;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("screen cap width = 360, height = ");
            sb3.append(i4);
            printStream.println(sb3.toString());
            while (!this.mStopped) {
                try {
                    Bitmap screenBitmap = getScreenBitmap(rect, WIDTH, i4);
                    if (screenBitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        screenBitmap.compress(CompressFormat.JPEG, 80, byteArrayOutputStream);
                        writeBytes(byteArrayOutputStream.toByteArray());
                        SystemClock.sleep(20);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void writeBytes(byte[] bArr) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(bArr.length + 4);
        allocate.putInt(bArr.length);
        allocate.put(bArr);
        allocate.flip();
        this.mOutputStream.write(allocate.array());
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("writeBytes: bytes = ");
        sb.append(bArr.length);
        Log.i(str, sb.toString());
    }

    private void stop() {
        this.mStopped = true;
    }

    private Bitmap getScreenBitmap(Rect rect, int i, int i2) throws RuntimeException {
        try {
            if (this.mScreenshotMethod == null) {
                this.mScreenshotMethod = getScreenshotMethod();
            }
            if (VERSION.SDK_INT >= 28) {
                return (Bitmap) this.mScreenshotMethod.invoke(null, new Object[]{rect, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(0)});
            }
            return (Bitmap) this.mScreenshotMethod.invoke(null, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } catch (Exception e) {
            Log.e(TAG, "getScreenBitmap: ", e);
            throw new RuntimeException(e);
        }
    }

    private Method getScreenshotMethod() throws Exception {
        if (VERSION.SDK_INT >= 28) {
            return Class.forName("android.view.SurfaceControl").getMethod("screenshot", new Class[]{Rect.class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
        } else if (VERSION.SDK_INT >= 18) {
            return Class.forName("android.view.SurfaceControl").getMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE});
        } else {
            return Class.forName("android.view.Surface").getMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE});
        }
    }
}
