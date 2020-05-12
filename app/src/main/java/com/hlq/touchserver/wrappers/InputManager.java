package com.hlq.touchserver.wrappers;

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
        this.manager = iInterface;
        try {
            this.injectInputEventMethod = iInterface.getClass().getMethod("injectInputEvent", new Class[]{InputEvent.class, Integer.TYPE});
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public boolean injectInputEvent(InputEvent inputEvent, int i) {
        try {
            return ((Boolean) this.injectInputEventMethod.invoke(this.manager, new Object[]{inputEvent, Integer.valueOf(i)})).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
