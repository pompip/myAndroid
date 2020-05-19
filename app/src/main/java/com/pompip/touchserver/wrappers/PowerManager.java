package com.pompip.touchserver.wrappers;

import android.os.IInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PowerManager {
    private final Method isScreenOnMethod;
    private final IInterface manager;

    public PowerManager(IInterface iInterface) {
        this.manager = iInterface;
        try {
            this.isScreenOnMethod = iInterface.getClass().getMethod("isInteractive");
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isScreenOn() {
        try {
            return (Boolean) this.isScreenOnMethod.invoke(this.manager);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
