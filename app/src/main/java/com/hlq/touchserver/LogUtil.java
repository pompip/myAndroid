package com.hlq.touchserver;

import android.util.Log;
import java.io.PrintStream;

public class LogUtil {
    private static final String TAG = "TouchServer";

    public static void d(String str, String str2) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("TouchServer_");
        sb.append(str);
        sb.append(" : ");
        sb.append(str2);
        printStream.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("TouchServer_");
        sb2.append(str);
        Log.w(sb2.toString(), str2);
    }

    public static void d(String str) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("TouchServer : ");
        sb.append(str);
        printStream.println(sb.toString());
        Log.w(TAG, str);
    }
}
