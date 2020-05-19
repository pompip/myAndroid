package com.pompip.touchserver.wrappers;

import android.os.IInterface;

public final class DisplayManager {
    private final IInterface manager;

    public DisplayManager(IInterface iInterface) {
        this.manager = iInterface;
    }

    public int[] getDisplayInfo() {
        try {
            Object invoke = this.manager.getClass().getMethod("getDisplayInfo", Integer.TYPE)
                    .invoke(this.manager, 0);
            Class cls = invoke.getClass();
            int logicalWidth = cls.getDeclaredField("logicalWidth").getInt(invoke);
            int logicalHeight = cls.getDeclaredField("logicalHeight").getInt(invoke);
            int rotation = cls.getDeclaredField("rotation").getInt(invoke);
            return new int[]{logicalWidth,logicalHeight, rotation};
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
