package com.timper.lonelysword.app.di.module;

import com.timper.lonelysword.app.feature.main.MainFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 15/03/2018
 * Description:
 * FIXME
 */
@Module public abstract class MainSubModule {

  @ContributesAndroidInjector abstract MainFragment bindTabFragment();
}
