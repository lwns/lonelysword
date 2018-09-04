package com.timper.lonelysword.context;

import android.os.Handler;
import android.os.Looper;
import java.lang.reflect.Method;

/**
 * Created by op on 2017/4/25.
 */

@SuppressWarnings("WeakerAccess")
public class AndroidHacks {
    private static final String TAG = "Applications";
    private static Object sActivityThread;

    public static Object getActivityThread() {
        if (sActivityThread == null) {
            synchronized (AndroidHacks.class) {
                if (sActivityThread == null) {
                    sActivityThread = getActivityThreadFromUIThread();
                    if (sActivityThread != null) {
                        return sActivityThread;
                    }

                    if (Looper.getMainLooper() == Looper.myLooper()) {
                        sActivityThread = getActivityThreadFromUIThread();
                    } else {
                        Handler handler = new Handler(Looper.getMainLooper());
                        synchronized (AndroidHacks.class) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sActivityThread = getActivityThreadFromUIThread();
                                    synchronized (AndroidHacks.class) {
                                        AndroidHacks.class.notify();
                                    }
                                }
                            });
                            try {
                                AndroidHacks.class.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        }
        return sActivityThread;
    }

    private static Object getActivityThreadFromUIThread() {
        Object activityThread = null;
        try {
            Method method = Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
            method.setAccessible(true);
            activityThread = method.invoke(null);
        } catch (final Exception e) {
        }
        return activityThread;
    }
}
