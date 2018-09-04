package com.timper.lonelysword.context;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created by op on 2017/4/25.
 */
@SuppressWarnings("WeakerAccess")
public class App {

    /**
     * Access a global {@link Application} context from anywhere, such as getting a context in a Library
     * module without attaching it from BaseApplication module.
     * <p>
     * Note that this method may return null in some cases, such as working with a hotfix framework
     * or access when the BaseApplication is terminated.
     */
    public static Application context() {
        return CURRENT;
    }

    @SuppressLint("StaticFieldLeak")
    private static Application CURRENT;

    public static void setApplication(Application context){
        CURRENT = context;
    }

    //static {
    //    try {
    //        Object activityThread = AndroidHacks.getActivityThread();
    //        Object app = activityThread.getClass().getMethod("getApplication").invoke(activityThread);
    //        CURRENT = (Application) app;
    //    } catch (Throwable e) {
    //        throw new IllegalStateException("Can not access Application context by magic code, boom!", e);
    //    }
    //}
}