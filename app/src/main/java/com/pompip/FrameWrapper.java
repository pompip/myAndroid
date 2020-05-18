//package com.pompip;
//
//import android.net.LocalSocket;
//import android.net.LocalSocketAddress;
//import android.util.Log;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.Pipe;
//import java.nio.channels.SocketChannel;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import okhttp3.WebSocket;
//import okio.ByteString;
//
//public class FrameWrapper {
//    private void log(String msg){
//        Log.e("FrameWrapper", "log: "+msg );
//    }
//    SocketChannel mChannel;
//    protected void createSocket(LocalSocket localSocket,WebSocket webSocket) {
//        FileOutputStream os = null;
//        try {
//
//            mChannel.connect(localSocket.getRemoteSocketAddress().)
//            this.mChannel.socket().setKeepAlive(true);
//            this.mChannel.socket().setTcpNoDelay(true);
//            if (this.mChannel.finishConnect())
//                readDataFromServer(webSocket);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (os != null)
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//    }
//
//    private void readDataFromServer(WebSocket webSocket) throws IOException {
//        log("start receive frame ");
//        ByteBuffer lenBuffer = ByteBuffer.allocate(4);
////        getScreenInfo(lenBuffer);
//        while (true) {//todo
//            lenBuffer.clear();
//            fillBuffer(lenBuffer, this.mChannel);
//            if (!lenBuffer.hasRemaining()) {
//                lenBuffer.flip();
//                int len = lenBuffer.getInt();
//                if (len > 0) {
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(len);
//                    fillBuffer(byteBuffer, this.mChannel);
//                    byteBuffer.flip();
//                    if (webSocket != null)
//                        webSocket.send(new ByteString(byteBuffer.array()));
//                }
//            }
//        }
//    }
//
////    private void getScreenInfo(ByteBuffer frameLen) throws IOException {
////        fillBuffer(frameLen, this.mChannel);
////        if (!frameLen.hasRemaining()) {
////            frameLen.flip();
////            int len = frameLen.getInt();
////            if (len > 0) {
////                ByteBuffer infoBuffer = ByteBuffer.allocate(len);
////                fillBuffer(infoBuffer, this.mChannel);
////                if (!infoBuffer.hasRemaining()) {
////                    infoBuffer.flip();
////                    String info = new String(infoBuffer.array(), "utf-8");
////                    log(" info = " + info);
////                    Pattern pattern = Pattern.compile("width = (\\d+)= (\\d+)");
////                    Matcher matcher = pattern.matcher(info);
////                    if (matcher.find()) {
////                        int width = Integer.parseInt(matcher.group(1));
////                        int height = Integer.parseInt(matcher.group(2));
////                        if (this.mCallback != null)
////                            this.mCallback.onDisplayInfo(width, height);
////                    }
////                }
////            }
////        }
////        frameLen.clear();
////    }
//
//    public static void fillBuffer(ByteBuffer buffer, SocketChannel channel) throws IOException {
//        int len;
//        do {
//            len = channel.read(buffer);
//
//        } while (buffer.hasRemaining() && len != -1);
//    }
//}
