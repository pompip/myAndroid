package com.pompip.touchserver.wrappers;

import android.os.IInterface;
import android.view.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class InputManager {
    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    private final Method injectInputEventMethod;
    private final IInterface manager;

    public InputManager(IInterface iInterface) {
        manager = iInterface;
        try {
            injectInputEventMethod = iInterface.getClass().getMethod("injectInputEvent", InputEvent.class, Integer.TYPE);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public boolean injectInputEvent(InputEvent inputEvent, int i) {
        try {
            return (Boolean) injectInputEventMethod.invoke(manager, new Object[]{inputEvent, i});
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
