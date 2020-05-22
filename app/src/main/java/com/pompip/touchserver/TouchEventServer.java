package com.pompip.touchserver;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import com.pompip.touchserver.wrappers.ServiceManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TouchEventServer {
    public static final String HOST = "recordDisplay";
    private static int MODE = 0;
    private static final String TAG = "TouchEventServer";
    private static final byte TYPE_KEYCODE = 1;
    private static final byte TYPE_MOTION = 0;
    private final byte[] contentBytes = new byte[18];
    private final byte[] lenBytes = new byte[4];
    private EventInjector mEventInjector;
    private InputStream mInputStream;
    private ServiceManager mServiceManager;

    public static void main(String[] args) {
        Log.e(TAG, "start :"+ Arrays.toString(args));
        int width = 0;
        int bitrate = 1000000;
        if (args.length > 0) {
            if ("screenshot".equals(args[0])) {
                MODE = 1;
                Log.e(TAG,"enable screenshot");
            } else if ("h264".equals(args[0])) {
                MODE = 2;
                Log.e(TAG,"enable h264 ");
                if (args.length > 1) {
                    width = Integer.parseInt(args[1]);
                }
                if (args.length > 2) {
                    try {
                        bitrate = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        bitrate = 800000;
                    }
                }
            }
        }
        try {
            new TouchEventServer(width, bitrate).loop();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private TouchEventServer(int width, int bitrate) throws IOException {

        Log.e(TAG, "client bind start 1!");
        LocalSocket accept = new LocalSocket();
        accept.connect(new LocalSocketAddress(TouchEventServer.HOST));


        Log.e(TAG, "client bind SUCCESS 1!");
        mServiceManager = new ServiceManager();
        if (MODE == 1) {
            OutputStream outputStream = accept.getOutputStream();
            Screenshot mScreenshot = new Screenshot(mServiceManager, outputStream);
            Thread mThread = new Thread(mScreenshot, "Screenshot");
            mThread.start();
        } else if (MODE == 2) {
            FileDescriptor fileDescriptor = accept.getFileDescriptor();
            ScreenEncoder screenEncoder = new ScreenEncoder(width, bitrate, fileDescriptor);
            Thread mThread = new Thread(screenEncoder, "H264");
            mThread.start();
            Log.e(TAG,"start Screen encoder");
        } else {
            mInputStream = accept.getInputStream();
            accept.close();
        }


    }


    private ByteBuffer readByte(byte[] bArr, int i) {
        if (i > bArr.length) {
            bArr = new byte[i];
        }
        int i2 = 0;
        while (i2 < bArr.length && i2 != i) {
            try {
                if (this.mInputStream == null) {
                    continue;
                }
                int read = this.mInputStream.read(bArr, i2, i - i2);
                if (read <= -1) {
                    break;
                }
                i2 += read;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (i2 == i) {
            return ByteBuffer.wrap(bArr, 0, i2);
        }
        return null;
    }

    private void loop() {
        Log.e(TAG,"start Loop");
        this.mEventInjector = new EventInjector(this.mServiceManager.getInputManager(), this.mServiceManager.getPowerManager());
        this.mEventInjector.checkScreenOn();
        while (true) {
            ByteBuffer readByte = readByte(this.lenBytes, this.lenBytes.length);
            if (readByte != null) {
                int i = readByte.getInt();
                if (i > 0) {
                    ByteBuffer readByte2 = readByte(this.contentBytes, i);
                    if (readByte2 != null) {
                        try {
                            handleEvent(readByte2);
                        } catch (Exception e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    }
                }
            }
        }
    }

    private void handleEvent(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        if (b == 0) {
            byte b2 = byteBuffer.get();
            if (b2 != 8) {
                switch (b2) {
                    case 0:
                    case 2:
                        this.mEventInjector.injectInputEvent(b2, byteBuffer.getInt(), byteBuffer.getInt());
                        return;
                    case 1:
                        this.mEventInjector.injectInputEvent(b2, -1, -1);
                        return;
                    default:
                        return;
                }
            } else {
                this.mEventInjector.injectScroll(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getFloat(), byteBuffer.getFloat());
            }
        } else if (b == 1) {
            int i = byteBuffer.getInt();
            if (i <= 0) {
                this.mEventInjector.checkScreenOff();
            } else {
                this.mEventInjector.injectKeycode(i);
            }
        }
    }
}
