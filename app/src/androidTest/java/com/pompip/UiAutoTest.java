package com.pompip;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.RemoteException;
import android.os.Trace;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.EventCondition;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiWatcher;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class UiAutoTest  {
    static Instrumentation instrumentation;
    static UiDevice uiDevice;

    @BeforeClass
    public static void init(){
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);

    }

    @Test
    public void launchApp() throws RemoteException, IOException {
        Trace.beginSection("hello");
        if (!uiDevice.isScreenOn()){
            uiDevice.wakeUp();
        }
        Context context = instrumentation.getContext();

        uiDevice.registerWatcher("hello", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                Log.e("chong", "checkForCondition: "+uiDevice.getLauncherPackageName() );

                return false;
            }
        });
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("com.tencent.news");
        context.startActivity(launchIntentForPackage);
        UiObject2 wait = uiDevice.wait(Until.findObject(By.text("活动")), 15000);

        Log.e("chong",wait.getApplicationPackage());
        wait.click();
//        uiDevice.executeShellCommand("am force-stop com.tencent.news");
        uiDevice.waitForWindowUpdate(null,15000);
//        uiDevice.sleep();
        Trace.endSection();
    }

//    @Test
    public void first() throws RemoteException, InterruptedException {
        uiDevice.pressRecentApps();
        uiDevice.waitForIdle();
        uiDevice.pressBack();
        uiDevice.findObject(By.text("1")).click();
        uiDevice.findObject(By.text("+")).click();
        uiDevice.findObject(By.text("2")).click();
        uiDevice.findObject(By.text("=")).click();
        uiDevice.pressRecentApps();
    }

//    @Test
    public void swipeTest(){
        Point[] points = {new Point(100,500),new Point(200,500),new Point(300,500)};
        uiDevice.swipe(points,points.length);
        uiDevice.waitForIdle(2000);
        uiDevice.swipe(points,points.length);
        uiDevice.waitForIdle(2000);
        uiDevice.swipe(points,points.length);
        uiDevice.waitForIdle(2000);




    }

//    @Test
    public void testAction(){
        EventCondition<Boolean> booleanEventCondition = Until.newWindow();
        uiDevice.performActionAndWait(new Runnable() {
            @Override
            public void run() {

            }
        }, booleanEventCondition,5000);
    }
}
