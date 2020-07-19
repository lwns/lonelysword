package com.timper.lonelysword.app.feature.splash;

import androidx.databinding.ObservableField;

import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.app.data.GetUserUseCase;
import com.timper.lonelysword.base.AppViewModel;
import com.timper.lonelysword.support.base.BaseViewModel;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope public class SplashViewModel extends BaseViewModel {

  public ObservableField<String> content = new ObservableField<>("欢迎来到android快速开发框架Lonelysword");

  @Inject public SplashViewModel() {
  }
}
