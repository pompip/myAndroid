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

    public static ServiceManager getInstance() {
        return serviceManager;
    }

    private static ServiceManager serviceManager = new ServiceManager();

    private ServiceManager() {
        try {
            getServiceMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private IInterface getService(String serviceName, String serviceClassName) {
        try {
            IBinder iBinder = (IBinder) getServiceMethod.invoke(null, serviceName);
            String sb = serviceClassName +"$Stub";
            return (IInterface) Class.forName(sb).getMethod("asInterface", IBinder.class).invoke(null, iBinder);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public DisplayManager getDisplayManager() {
        if (displayManager == null) {
            displayManager = new DisplayManager(getService("display",
                    "android.hardware.display.IDisplayManager"));
        }
        return displayManager;
    }

    public InputManager getInputManager() {
        if (inputManager == null) {
            inputManager = new InputManager(getService("input",
                    "android.hardware.input.IInputManager"));
        }
        return inputManager;
    }

    public PowerManager getPowerManager() {
        if (powerManager == null) {
            powerManager = new PowerManager(getService("power",
                    "android.os.IPowerManager"));
        }
        return powerManager;
    }
}
