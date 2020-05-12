package com.hlq.touchserver.wrappers;

import android.os.Build.VERSION;
import android.os.IInterface;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PowerManager {
    private final Method isScreenOnMethod;
    private final IInterface manager;

    public PowerManager(IInterface iInterface) {
        this.manager = iInterface;
        try {
            this.isScreenOnMethod = iInterface.getClass().getMethod(VERSION.SDK_INT >= 20 ? "isInteractive" : "isScreenOn", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isScreenOn() {
        try {
            return ((Boolean) this.isScreenOnMethod.invoke(this.manager, new Object[0])).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
