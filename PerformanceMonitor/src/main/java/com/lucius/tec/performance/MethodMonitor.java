package com.lucius.tec.performance;

import android.os.Looper;
import android.util.Log;

public class MethodMonitor {

    public static String methodName() {
        return Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static void trackMethodPerformance(String methodName, long startTime) {
        long cost = System.currentTimeMillis() - startTime;
        Log.e("integer", "method: " + methodName + " cost " + cost);
    }

    boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

}
