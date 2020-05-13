package com.pompip.touchserver;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import com.pompip.touchserver.wrappers.ServiceManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TouchEventServer {
    private static final String HOST = "singleTouch";
    private static int MODE = 0;
    private static final String TAG = "TouchEventServer";
    private static final byte TYPE_KEYCODE = 1;
    private static final byte TYPE_MOTION = 0;
    private final byte[] contentBytes = new byte[18];
    private final byte[] lenBytes = new byte[4];
    private EventInjector mEventInjector;
    private  InputStream mInputStream;
    private  ServiceManager mServiceManager;

    public static void main(String[] strArr) {
        Log.e(TAG,"start");
        int i = 0;
        int i2 = 1000000;
        if (strArr.length > 0) {
            if ("screenshot".equals(strArr[0])) {
                MODE = 1;
                System.out.println("enable screenshot");
            } else if ("h264".equals(strArr[0])) {
                MODE = 2;
                System.out.println("enable h264 ");
                if (strArr.length > 1) {
                    i = Integer.parseInt(strArr[1]);
                }
                if (strArr.length > 2) {
                    try {
                        i2 = Integer.parseInt(strArr[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        i2 = 800000;
                    }
                }
            }
        }
        try {
            new TouchEventServer(i, i2).loop();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private  TouchEventServer(int r6,int r7) throws IOException{

        LogUtil.d("client bind start !");
            LocalServerSocket r1 = new LocalServerSocket(HOST);
            LocalSocket accept = r1.accept();
            LogUtil.d("client bind SUCCESS !");
            mServiceManager = new ServiceManager();
            if (MODE == 1){

                OutputStream outputStream = accept.getOutputStream();
                Screenshot mScreenshot = new Screenshot(mServiceManager,outputStream);
                Thread mThread = new Thread(mScreenshot,"Screenshot");
                mThread.start();
            }else if (MODE ==2){
                FileDescriptor fileDescriptor = accept.getFileDescriptor();
                ScreenEncoder screenEncoder = new ScreenEncoder(r6,r7,fileDescriptor);
                Thread mThread = new Thread(screenEncoder,"H264");
                mThread.start();
            }else {
                mInputStream = accept.getInputStream();
                r1.close();
            }


    }


    private ByteBuffer readByte(byte[] bArr, int i) {
        if (i > bArr.length) {
            bArr = new byte[i];
        }
        int i2 = 0;
        while (i2 < bArr.length && i2 != i) {
            try {
                if (this.mInputStream==null){
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
                            LogUtil.d(Log.getStackTraceString(e));
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
