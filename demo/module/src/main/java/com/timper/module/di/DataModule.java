package com.timper.module.di;

import com.timper.module.UiThread;
import com.timper.module.data.remote.TestRepository;
import com.timper.module.data.TestRepositoryImp;
import com.timper.lonelysword.data.executor.JobExecutor;
import com.timper.lonelysword.data.executor.PostExecutionThread;
import com.timper.lonelysword.data.executor.ThreadExecutor;
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
@Singleton
public class DataModule {

    @Provides
    @Singleton public TestRepository bindMainRepository(TestRepositoryImp mainRepository) {
        return mainRepository;
    }

    @Provides @Singleton public ThreadExecutor bindThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides @Singleton public PostExecutionThread provideContext(UiThread uiThread) {
        return uiThread;
    }
}
