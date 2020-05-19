package com.pompip.touchserver.wrappers;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;
import java.lang.reflect.Method;

@SuppressLint({"PrivateApi"})
public final class ServiceManager {
    private DisplayManager displayManager;
    private final Method getServiceMethod;
    private InputManager inputManager;
    private PowerManager powerManager;

    public ServiceManager() {
        try {
            this.getServiceMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private IInterface getService(String str, String str2) {
        try {
            IBinder iBinder = (IBinder) this.getServiceMethod.invoke(null, new Object[]{str});
            String sb = str2 +"$Stub";
            return (IInterface) Class.forName(sb).getMethod("asInterface", IBinder.class).invoke(null, iBinder);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public DisplayManager getDisplayManager() {
        if (this.displayManager == null) {
            this.displayManager = new DisplayManager(getService("display", "android.hardware.display.IDisplayManager"));
        }
        return this.displayManager;
    }

    public InputManager getInputManager() {
        if (this.inputManager == null) {
            this.inputManager = new InputManager(getService("input", "android.hardware.input.IInputManager"));
        }
        return this.inputManager;
    }

    public PowerManager getPowerManager() {
        if (this.powerManager == null) {
            this.powerManager = new PowerManager(getService("power", "android.os.IPowerManager"));
        }
        return this.powerManager;
    }
}
