package com.pompip;


import com.pompip.screen.WebSocketUtil;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.WebSocket;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testPo() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        modifyPoint(p1, p2);
        System.out.println("[" + p1.x + "," + p1.y + "],[" + p2.x + "," + p2.y + "]");
    }


    private static void modifyPoint(Point p1, Point p2) {
        Point tmpPoint = p1;
        p1 = p2;
        p2 = tmpPoint;
        p1.setLocation(5, 5);
//        p2 = new Point(5, 5);
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void setLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Test
    public void testOkio() {
        WebSocket webSocket = WebSocketUtil.create();

        try {
            Source source = Okio.source(new File("C:\\Users\\bonree\\Desktop\\18410271_2980438239_26414.mp4"));
            Buffer buffer = new Buffer();
            long read;
            while ((read = source.read(buffer, 1024)) > 0) {
                System.out.println(read);
                byte[] array = buffer.readByteArray(buffer.size());
                boolean send = webSocket.send(new ByteString(array));
                System.out.println(send);
                Thread.sleep(100);
                buffer.close();
            }

            source.close();
            Thread thread = new Thread();
            thread.start();
            thread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testArr() {
//        System.out.println(findLCS("BDCABA","ABCBDAB"));;
//        System.out.println(1.0-0.9);
//        int i = "ABCDE".indexOf("F");
//        System.out.println(i);
//        String s = "ABCDE".substring(i+1);
//        System.out.println(s);
        int[] arr = {0, 1, -1, -1, 1, 1, 0, 0, -1, 0};
        int total = 0;

        label1:
        for (int i = arr.length; i > 0; i--) {
            for (int x = 0; x < arr.length - i + 1; x++) {
                total = add(x, i, arr);
                if (total == 0) {
                    System.out.println(i);
                    return;
                }
                total = 0;
            }
        }
    }

    private int add(int start, int len, int[] arr) {
        int total = 0;
        for (int j = start; j < start + len; j++) {
            total += arr[j];
        }
        return total;
    }

    private void arr(String A, String B) {

        for (int i = 0; i < A.length(); i++) {
            char[] aArr = A.substring(i).toCharArray();
            String sub = B;
            StringBuilder builder = new StringBuilder();
            for (char a : aArr) {
                sub = isIn(a, sub);
                if ("".equals(sub)) {
                    continue;
                }
                builder.append(a);

            }
            System.out.println(builder);
        }


    }

    private String isIn(char a, String b) {

        int i = b.indexOf(a);
        if (i == -1) {
            return "";
        }
        return b.substring(i + 1);
    }


    public static int findLCS(String A, String B) {
        int n = A.length();
        int m = B.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                dp[i][j] = 0;
            }
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (A.charAt(i - 1) == B.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = dp[i - 1][j] > dp[i][j - 1] ? dp[i - 1][j] : dp[i][j - 1];
                }
            }
        }
        return dp[n][m];
    }



    @Test
    public void testWord(){



        String scn = "i am   a student";
        ArrayList<String> wordList = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\w+").matcher(scn);
        while (matcher.find()){
            wordList.add(matcher.group());
        };
        Collections.reverse(wordList);
        StringBuilder builder = new StringBuilder();
        for(String s:wordList){
            builder.append(s).append(" ");
        }
        System.out.println(builder.toString().trim());








        ;
    }
}

