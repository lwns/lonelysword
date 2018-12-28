package com.timper.lonelysword.app.di;

import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.app.feature.main.dialog.MainDialog;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 2018/12/26
 * Description:
 * FIXME
 */
@Module
public abstract class DialogModule {

  @ActivityScope
  @ContributesAndroidInjector(modules = {
    //MainDialogModule.class,
  })
  abstract MainDialog bindMainDialog();
}
