package com.pompip;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameWrapper {
    private void log(String msg){
        Log.e("FrameWrapper", "log: "+msg );
    }
    SocketChannel mChannel;
    protected void createSocket() {
        FileOutputStream os = null;
        try {
            this.mChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", this.mPort));
            this.mChannel.socket().setKeepAlive(true);
            this.mChannel.socket().setTcpNoDelay(true);
            if (this.mChannel.finishConnect())
                readDataFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void readDataFromServer() throws IOException {
        log("start receive frame ");
        ByteBuffer lenBuffer = ByteBuffer.allocate(4);
        getScreenInfo(lenBuffer);
        while (!this.mStopped) {
            lenBuffer.clear();
            fillBuffer(lenBuffer, this.mChannel);
            if (!lenBuffer.hasRemaining()) {
                lenBuffer.flip();
                int len = lenBuffer.getInt();
                if (len > 0) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(len);
                    fillBuffer(byteBuffer, this.mChannel);
                    byteBuffer.flip();
                    if (this.mCallback != null)
                        this.mCallback.updateFrame(byteBuffer.array());
                }
            }
        }
    }

    private void getScreenInfo(ByteBuffer frameLen) throws IOException {
        fillBuffer(frameLen, this.mChannel);
        if (!frameLen.hasRemaining()) {
            frameLen.flip();
            int len = frameLen.getInt();
            if (len > 0) {
                ByteBuffer infoBuffer = ByteBuffer.allocate(len);
                fillBuffer(infoBuffer, this.mChannel);
                if (!infoBuffer.hasRemaining()) {
                    infoBuffer.flip();
                    String info = new String(infoBuffer.array(), "utf-8");
                    log(" info = " + info);
                    Pattern pattern = Pattern.compile("width = (\\d+)= (\\d+)");
                    Matcher matcher = pattern.matcher(info);
                    if (matcher.find()) {
                        int width = Integer.parseInt(matcher.group(1));
                        int height = Integer.parseInt(matcher.group(2));
                        if (this.mCallback != null)
                            this.mCallback.onDisplayInfo(width, height);
                    }
                }
            }
        }
        frameLen.clear();
    }

    public static void fillBuffer(ByteBuffer buffer, SocketChannel channel) throws IOException {
        int len;
        do {
            len = channel.read(buffer);
        } while (buffer.hasRemaining() && len != -1);
    }
}
