package com.pompip;

import android.app.Application;
import android.app.Instrumentation;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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
