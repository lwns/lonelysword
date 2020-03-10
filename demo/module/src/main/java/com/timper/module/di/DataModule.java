package com.timper.module.di;

import com.timper.lonelysword.data.executor.JobExecutor;
import com.timper.lonelysword.data.executor.PostExecutionThread;
import com.timper.lonelysword.data.executor.ThreadExecutor;
import com.timper.module.UiThread;
import com.timper.module.data.remote.MainService;
import com.timper.module.data.remote.ServiceFactor;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * User: tangpeng.yang
 * Date: 2019-07-30
 * Description:
 * FIXME
 */
@Module
public class DataModule {

    @Provides
    @Singleton
    public MainService bindMainRepository() {
        return ServiceFactor.Companion.getInstance().createService(MainService.class);
    }

    @Provides
    @Singleton
    public ThreadExecutor bindThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    public PostExecutionThread provideContext(UiThread uiThread) {
        return uiThread;
    }
}
