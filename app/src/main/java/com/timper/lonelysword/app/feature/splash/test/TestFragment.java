package com.timper.lonelysword.app.feature.splash.test;

import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.app.feature.splash.SplashActivity;
import com.timper.lonelysword.app.feature.splash.test2.Test2Fragment;
import com.timper.lonelysword.app.feature.splash.test2.Test2ViewModel;

/**
 * User: tangpeng.yang
 * Date: 06/07/2018
 * Description:
 * FIXME
 */
@Dagger
public class TestFragment extends Test2Fragment<Test2ViewModel> {

  @AfterViews void afterView() {

  }
}
