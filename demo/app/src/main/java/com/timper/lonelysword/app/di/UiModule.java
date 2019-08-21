package com.timper.lonelysword.app.di;

import com.timper.lonelysword.app.UiThread;
import com.timper.lonelysword.data.executor.PostExecutionThread;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * User: tangpeng.yang
 * Date: 20/03/2018
 * Description:
 * FIXME
 */
@Module @Singleton public class UiModule {

  @Provides @Singleton public PostExecutionThread provideContext(UiThread uiThread) {
    return uiThread;
  }
}
