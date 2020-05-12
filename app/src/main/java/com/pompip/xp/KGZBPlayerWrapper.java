package com.pompip.xp;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;

public class KGZBPlayerWrapper   {
    static KGZBPlayerWrapper wrapper = new KGZBPlayerWrapper();
    public static KGZBPlayerWrapper getInstance() {
        return wrapper;
    }


    public void hook(ClassLoader classLoader) {

    }
}
