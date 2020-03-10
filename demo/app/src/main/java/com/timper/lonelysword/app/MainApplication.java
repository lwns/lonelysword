package com.timper.lonelysword.app;

import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.facebook.stetho.Stetho;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.app.di.DaggerAppComponent;
import com.timper.lonelysword.aspectj.LoginBinder;
import com.timper.lonelysword.aspectj.LonelyswordAspect;
import com.timper.lonelysword.dagger.DaggerApplication;

import dagger.android.AndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate();
        Lonelysword.setDebug(true);

        Lonelysword.init(this);
        ARouter.init(this);

        Stetho.initializeWithDefaults(this);
        LonelyswordAspect.setLoginBinder(new LoginBinder() {
            @Override
            public boolean checkLogin() {
                Toast.makeText(MainApplication.this, "go to login", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
