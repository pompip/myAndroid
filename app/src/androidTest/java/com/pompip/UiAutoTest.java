package com.pompip;

import android.app.Instrumentation;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UiAutoTest extends UiAutomatorTestCase {

    @Test
    public void first(){


        UiDevice instance = UiDevice.getInstance();
        System.out.println(instance);
    }
}
