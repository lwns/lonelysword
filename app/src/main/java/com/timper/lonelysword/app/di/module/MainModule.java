package com.timper.lonelysword.app.di.module;

import com.timper.lonelysword.app.feature.MainActivity;
import com.timper.lonelysword.base.AppActivity;
import dagger.Binds;
import dagger.Module;

/**
 * User: tangpeng.yang
 * Date: 14/03/2018
 * Description:
 * FIXME
 */
@Module public abstract class MainModule {

  @Binds public abstract AppActivity provideMainView(MainActivity mainActivity);
}
