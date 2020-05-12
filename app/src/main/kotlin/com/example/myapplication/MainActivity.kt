package com.example.myapplication

import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.hlq.touchserver.wrappers.InputManager
//import com.hlq.touchserver.wrappers.InputManager
import com.hlq.touchserver.wrappers.ServiceManager
import com.pompip.testHttp.NetManager

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val ins = Instrumentation()
    lateinit var im: InputManager; ;
    var num = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test_button.setOnClickListener {
            num++
            test_edit.append("" + num)
            Executors.newSingleThreadExecutor().execute { NetManager.getWeixinURL(null) }
        }
//        im = ServiceManager().inputManager
        getSystemService(Context.INPUT_SERVICE);
        im = ServiceManager().inputManager



        getDPI()


    }

    val TAG = "MotionEvent";

    fun getDPI() {
        val displayMetrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(displayMetrics)
        drag_time.append("density:" + displayMetrics)
        drag_time.append("\n")
        drag_time.append("density:" + displayMetrics.density)
        drag_time.append("\n")
        drag_time.append("densityDpi:" + displayMetrics.densityDpi)
        drag_time.append("\n")
        drag_time.append("xdpi:" + displayMetrics.xdpi)
        drag_time.append("\n")
        drag_time.append("ydpi:" + displayMetrics.ydpi)
    }

    override fun onResume() {
        super.onResume()
        num = 0
//        val timerTask = object : TimerTask() {
//            override fun run() {
//                test()
//                 testClick()
//                testKeyCode()
//            }
//
//        }
//        Timer().schedule(timerTask, 2000)

//        val timer = object :CountDownTimer(60*1000,1000){
//            override fun onFinish() {
//
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                Log.e(TAG,"剩余："+millisUntilFinished)
//                if (millisUntilFinished<55*1000){
//                    val inputConnection = getActiveInputConnection()
//                    inputConnection.commitText("hello123中国",0)
//
//
//                }
//            }
//
//        }
//
//        timer.start()

    }
    val pointerProperties = Array<MotionEvent.PointerProperties>(1){MotionEvent.PointerProperties()};
    val pointerCoords =  arrayOf(MotionEvent.PointerCoords())
    fun injectEvent(event:MotionEvent){
//        val method = InputManager::class.java.getMethod(
//            "injectInputEvent",
//            InputEvent::class.java,
//            Int::class.java
//        )
        Log.e("chong",event.toString())

        pointerProperties[0].id = 0
        pointerProperties[0].toolType = 1


        pointerCoords[0].x=event.x;
        pointerCoords[0].y = event.y
        pointerCoords[0].orientation = 0.0f
        pointerCoords[0].pressure = 1.0f
        pointerCoords[0].size = 1.0f
        val obtain = MotionEvent.obtain(
            event.downTime,
            event.eventTime,
            event.action,
            1,
            pointerProperties,
            pointerCoords,
            0,
            1,
            1.0f,
            1.0f,
            0,
            0,
            4098,
            0
        )

//        method.invoke(im,event,0)
        im.injectInputEvent(obtain,InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)

    }

    fun test() {
        val timeList = ArrayList<Long>()
//        Instrumentation.newApplication(this,this)

        val list = ArrayList<Pair<Int, Int>>()

        for (angle in 1..360) {
            val pointX = 500 + 300 * Math.cos(Math.toRadians(angle.toDouble()))
            val pointY = 700 + 300 * Math.sin(Math.toRadians(angle.toDouble()))
            list.add(Pair(pointX.toInt(), pointY.toInt()))
        }


//        for ( i in 200..900 step 10){
//            list.add(Pair(i,i*1920/1080))
//        }
        while (list.size > 80) {
            val d = (Math.random() * (list.size - 2)).toInt()
            if (d == 0 || d >= list.size - 1) {
                continue
            }
            list.removeAt(d)
        }

        var down = SystemClock.uptimeMillis();
        var eventTime = SystemClock.uptimeMillis();
        var eventType = MotionEvent.ACTION_MOVE;
        for (i in 0 until list.size) {
            if (i == 0) {
                down = SystemClock.uptimeMillis();
                eventType = MotionEvent.ACTION_DOWN
            } else if (i == list.size - 1) {
//                eventTime += 17L;
                eventType = MotionEvent.ACTION_UP;
            } else {
//                eventTime += 17L;
                eventType = MotionEvent.ACTION_MOVE;
            }
            Thread.sleep(17)
            var eventTime = SystemClock.uptimeMillis();
            val x = list.get(i).first
            val y = list.get(i).second
            val obtain = MotionEvent.obtain(
                down,
                eventTime,
                eventType,
                x.toFloat(),
                y.toFloat(),
                0
            )
            val startTimeMillis = SystemClock.uptimeMillis();
//            MotionEvent.obtain(obtain)
            injectEvent(obtain)
//            brush_view.onTouchEvent(obtain)

//            Log.e("chong",injectInputEvent.toString())
//            obtain.recycle()
            val endTimeMillis = SystemClock.uptimeMillis();
            timeList.add(endTimeMillis - startTimeMillis)

        }


        runOnUiThread {
            drag_time.text = "moveEvent:"
            var total = 0L;
            for (times in timeList) {
                drag_time.append(" " + times)
                total += times
            }
            drag_time.append(" total:" + total)
            drag_time.append(" avg:" + total / timeList.size)

        }

    }

    fun testClick() {
        val location = IntArray(2)
        test_button.getLocationOnScreen(location)
        val cX = (location[0] + 50).toFloat()
        val cY = (location[1] + 50).toFloat()
        Log.e(TAG, "x:" + cX + " y:" + cY)
        val down = SystemClock.uptimeMillis();


        val list = ArrayList<Long>()
        for (i in 1..10) {
            Thread.sleep(1000)
            val obtainDown = MotionEvent.obtain(
                down,
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                cX,
                cY,
                0
            )
            val obtainUp = MotionEvent.obtain(
                down,
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP,
                cX,
                cY,
                0
            )
            val startTime = SystemClock.uptimeMillis();
//            ins.sendPointerSync(obtainDown)
            injectEvent(obtainDown)
//            ins.sendPointerSync(obtainUp)
//            im.injectInputEvent(obtainUp,InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
            injectEvent(obtainUp)
            val endTime = SystemClock.uptimeMillis();
            list.add(endTime - startTime)
            obtainDown.recycle()
            obtainUp.recycle()
        }

        runOnUiThread {
            drag_time.append("\n clickEvent:")
            var total = 0L;
            for (times in list) {
                drag_time.append(" " + times)
                total += times
            }
            drag_time.append(" avg:" + total / list.size)

        }

    }

    fun testKeyCode() {
        val list = ArrayList<Long>()
        for (i in 1..10) {
            val startTime = SystemClock.uptimeMillis();
            ins.sendStringSync("helloworld")
            val endTime = SystemClock.uptimeMillis();

            val time1 = endTime - startTime;
            list.add(time1)
            Thread.sleep(1000)
        }
        runOnUiThread {
            drag_time.append("\n")
            drag_time.append("字母：")
            var total = 0L;
            for (times in list) {
                drag_time.append(" " + times)
                total += times

            }
            drag_time.append(" avg:" + total / list.size)
        }
    }


    private fun getTextInputManager(): InputMethodManager {
        val mInputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return mInputMethodManager
    }

    fun getActiveInputConnection(): InputConnection {
        val mInputMethodManager = getTextInputManager()
        var currentInputConnection: InputConnection


        try {
            val inputMethodField =
                mInputMethodManager.javaClass.getDeclaredField("mServedInputConnection")
            inputMethodField.isAccessible = true
            currentInputConnection =
                inputMethodField.get(mInputMethodManager) as InputConnection
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "文字输入,采用兼容7.0")
            currentInputConnection = getActiveInputConnection2()
        }
        return currentInputConnection;
    }

    fun getActiveInputConnection2(): InputConnection {
        val mInputMethodManager = getTextInputManager()
        val inputMethodField =
            InputMethodManager::class.java.getDeclaredField("mServedInputConnectionWrapper")
        inputMethodField.isAccessible = true
        val mServedInputConnectionWrapper = inputMethodField.get(mInputMethodManager)
        val method = mServedInputConnectionWrapper.javaClass.getMethod("getInputConnection")
        val currentInputConnection =
            method.invoke(mServedInputConnectionWrapper) as InputConnection


        return currentInputConnection
    }

}
