package com.pompip;

import android.app.Instrumentation;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UiAutoTest  {
    Instrumentation instrumentation;
    UiDevice uiDevice;

    @Before
    public void init(){
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);
    }

    @Test
    public void first() throws RemoteException {
        uiDevice.findObject(By.text("1")).click();
        uiDevice.findObject(By.text("+")).click();
        uiDevice.findObject(By.text("2")).click();
        uiDevice.findObject(By.text("=")).click();
        uiDevice.pressRecentApps();


    }
}
