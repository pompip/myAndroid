package com.pompip.screen;

import android.app.Application;
import android.app.Instrumentation;
import android.util.Log;



public class Main {
    private static final String TAG = "Main";
    public static void main(String[] args){
        String msg = "Void";
        if (args.length>0){
            msg = args[0];
        }
        Log.d(TAG, "main: "+msg);
        System.out.println(msg);
    }


}
