package com.timper.lonelysword.app.di;

import com.timper.lonelysword.app.data.MainRepository;
import com.timper.lonelysword.app.data.repository.MainRepositoryImp;
import com.timper.lonelysword.data.executor.JobExecutor;
import com.timper.lonelysword.data.executor.ThreadExecutor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;


/**
 * User: tangpeng.yang
 * Date: 20/03/2018
 * Description:
 * FIXME
 */
@Module public class DataModule {

  @Provides @Singleton public MainRepository bindMainRepository(MainRepositoryImp mainRepository) {
    return mainRepository;
  }

  @Provides @Singleton public ThreadExecutor bindThreadExecutor(JobExecutor jobExecutor) {
    return jobExecutor;
  }
}

