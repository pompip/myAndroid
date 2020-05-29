package com.pompip.touchserver.wrappers;

import android.os.IInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PowerManager {
    private final Method isScreenOnMethod;
    private final IInterface manager;

    public PowerManager(IInterface iInterface) {
        manager = iInterface;
        try {
            isScreenOnMethod = iInterface.getClass().getMethod("isInteractive");
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isScreenOn() {
        try {
            return (Boolean) isScreenOnMethod.invoke(manager);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
