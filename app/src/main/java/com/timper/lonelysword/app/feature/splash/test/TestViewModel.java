package com.timper.lonelysword.app.feature.splash.test;

import android.databinding.ObservableField;
import android.support.v7.app.AppCompatActivity;
import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.base.AppActivity;
import com.timper.lonelysword.base.AppViewModel;
import com.timper.lonelysword.context.App;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope public class TestViewModel extends AppViewModel {

  public interface Navigation {
    void gotoMain();
  }

  public ObservableField<String> hellow = new ObservableField<>("sdfadf");

  @Inject public TestViewModel() {
  }
}
