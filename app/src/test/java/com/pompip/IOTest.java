package com.pompip;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IOTest {
    @Test
    public void testNIO() throws IOException {
        RandomAccessFile rFile = new RandomAccessFile("C:\\Users\\bonreeKC\\Desktop\\hell.log","rw");
        FileChannel channel = rFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(52);
        int read ;
        while ((read = channel.read(byteBuffer))!=-1){
            System.out.println(read);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                System.out.println("remaining:"+byteBuffer.remaining());
                System.out.println("position:"+byteBuffer.position());
                System.out.println("limit:"+byteBuffer.limit());
                byte[] dst = new byte[byteBuffer.remaining()];
                ByteBuffer x = byteBuffer.get(dst);
                System.out.println(new String(dst));

            }
            byteBuffer.compact();
        }
    }

    @Test
    public void testByteBuffer(){
        // 表示给底层的字节数组来指定大小
        // 缓冲区在给定之后，长度就不能改变了
        ByteBuffer buffer =
                ByteBuffer.allocate(10);
        // 添加数据
        buffer.put("abc".getBytes());
        buffer.put((byte) 0);
        buffer.put("def".getBytes());
        // 将position挪动
        // buffer.position(0);
        // 获取元素
        // 获取的是一个字节
        // byte b = buffer.get();
        // System.out.println(b);
        // byte b2 = buffer.get();
        // System.out.println(b2);

        // 遍历
        // 将limit挪到position的位置上
        // 将position归零
        // buffer.limit(buffer.position());
        // buffer.position(0);
        // 上述两部操作称之为翻转缓冲区
        // 等价于
        buffer.flip();
        // while(buffer.position() < buffer.limit()){
        // 等价
        while(buffer.hasRemaining()){
            byte b = buffer.get();
            System.out.println(b);
        }
    }

    @Test
    public void testByteBuffer2(){
        // 适合于数据未知的场景
        // ByteBuffer buffer = ByteBuffer.allocate();
        // 数据已知
        // ByteBuffer buffer =
        //         ByteBuffer.wrap("hello big2002".getBytes());
        // System.out.println(buffer.position());

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("hello".getBytes());

        // 获取缓冲区底层的字节数组
        byte[] data = buffer.array();
        // System.out.println(new String(data, 0,
        //         buffer.position()));
        buffer.flip();
        System.out.println(new String(data, 0,
                buffer.limit()));
    }
}
