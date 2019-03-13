package com.timper.lonelysword.app.feature.splash;

import androidx.databinding.ObservableField;
import com.timper.lonelysword.ActivityScope;
//import com.timper.lonelysword.app.data.GetUserUseCase;
import com.timper.lonelysword.base.AppViewModel;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope public class SplashViewModel extends AppViewModel {
  //GetUserUseCase userUseCase;

  public interface Navigation {
    void gotoMain();
  }

  public ObservableField<String> hellow = new ObservableField<>("sdfadf");

  @Inject public SplashViewModel() {
    //this.userUseCase = userUseCase;
  }
}
