package com.hlq.touchserver;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import com.hlq.touchserver.wrappers.InputManager;
import com.hlq.touchserver.wrappers.PowerManager;

public class EventInjector {
    private InputManager mInputManager;
    private long mLastTime;
    private PowerManager mPowerManager;
    private final PointerCoords[] pointerCoords = {new PointerCoords()};
    private final PointerProperties[] pointerProperties = {new PointerProperties()};

    public EventInjector(InputManager inputManager, PowerManager powerManager) {
        this.mInputManager = inputManager;
        this.mPowerManager = powerManager;
        PointerProperties pointerProperties2 = this.pointerProperties[0];
        pointerProperties2.id = 0;
        pointerProperties2.toolType = 1;
        PointerCoords pointerCoords2 = this.pointerCoords[0];
        pointerCoords2.orientation = 0.0f;
        pointerCoords2.pressure = 1.0f;
        pointerCoords2.size = 1.0f;
    }

    private void setPointerCoords(int i, int i2) {
        PointerCoords pointerCoords2 = this.pointerCoords[0];
        pointerCoords2.x = (float) i;
        pointerCoords2.y = (float) i2;
    }

    /* access modifiers changed from: 0000 */
    public void injectInputEvent(int i, int i2, int i3) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (i == 0) {
            this.mLastTime = uptimeMillis;
        }
        if (i2 > 0) {
            setPointerCoords(i2, i3);
        }
        MotionEvent obtain = MotionEvent.obtain(this.mLastTime, uptimeMillis, i, 1, this.pointerProperties, this.pointerCoords, 0, 1, 1.0f, 1.0f, 0, 0, 4098, 0);
        this.mInputManager.injectInputEvent(obtain, 0);
    }

    /* access modifiers changed from: 0000 */
    public boolean injectKeycode(int i) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (!injectKeyEvent(uptimeMillis, 0, i) || !injectKeyEvent(uptimeMillis, 1, i)) {
            return false;
        }
        return true;
    }

    private boolean injectKeyEvent(long j, int i, int i2) {
        KeyEvent keyEvent = new KeyEvent(j, i == 0 ? j : SystemClock.uptimeMillis(), i, i2, 0, 0, -1, 0, 0, 257);
        return this.mInputManager.injectInputEvent(keyEvent, 0);
    }

    private void setScroll(float f, float f2) {
        PointerCoords pointerCoords2 = this.pointerCoords[0];
        pointerCoords2.setAxisValue(10, f);
        pointerCoords2.setAxisValue(9, f2);
    }

    /* access modifiers changed from: 0000 */
    public boolean injectScroll(int i, int i2, float f, float f2) {
        long uptimeMillis = SystemClock.uptimeMillis();
        setPointerCoords(i, i2);
        setScroll(f, f2);
        return this.mInputManager.injectInputEvent(MotionEvent.obtain(this.mLastTime, uptimeMillis, 8, 1, this.pointerProperties, this.pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 8194, 0), 0);
    }

    public void checkScreenOn() {
        if (!this.mPowerManager.isScreenOn()) {
            injectKeycode(26);
        }
    }

    public void checkScreenOff() {
        if (this.mPowerManager.isScreenOn()) {
            injectKeycode(26);
        }
    }
}
