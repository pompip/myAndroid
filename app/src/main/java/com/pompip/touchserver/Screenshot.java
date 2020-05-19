package com.pompip.touchserver;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.Log;
import com.pompip.touchserver.wrappers.ServiceManager;
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
        int width = displayInfo[0];
        int height = displayInfo[1];
        int rotation = displayInfo[2];
        String sb2 = "width = " +width +"，height = " +height +"，rotation = " +rotation;
        Rect rect = new Rect(0, 0, width, height);
        try {
            writeBytes(sb2.getBytes());
            height = (int) (((float) height) / ((((float) width) * 1.0f) / 360.0f));

            System.out.println("screen cap width = 360, height = " +height);
            while (!this.mStopped) {
                try {
                    Bitmap screenBitmap = getScreenBitmap(rect, WIDTH, height);
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
        Log.i(TAG, "writeBytes: bytes = " +bArr.length);
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
                return (Bitmap) this.mScreenshotMethod.invoke(null, rect, i, i2, 0);
            }
            return (Bitmap) this.mScreenshotMethod.invoke(null, i, i2);
        } catch (Exception e) {
            Log.e(TAG, "getScreenBitmap: ", e);
            throw new RuntimeException(e);
        }
    }

    private Method getScreenshotMethod() throws Exception {
        if (VERSION.SDK_INT >= 28) {
            return Class.forName("android.view.SurfaceControl").getMethod("screenshot", Rect.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
        } else if (VERSION.SDK_INT >= 18) {
            return Class.forName("android.view.SurfaceControl").getMethod("screenshot", Integer.TYPE, Integer.TYPE);
        } else {
            return Class.forName("android.view.Surface").getMethod("screenshot", Integer.TYPE, Integer.TYPE);
        }
    }
}
