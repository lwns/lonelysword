package com.timper.lonelysword.app.feature.main;

import android.databinding.ObservableField;
import android.support.v7.app.AppCompatActivity;
import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.app.databinding.ActMainBinding;
import com.timper.lonelysword.base.AppActivity;
import com.timper.lonelysword.base.AppViewModel;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope public class MainViewModel extends AppViewModel<ActMainBinding> {

  public interface Navigation {
    void gotoMain();
  }

  public ObservableField<String> hellow = new ObservableField<>("sdfadf");

  @Inject public MainViewModel(AppActivity activity) {
    super(activity);
  }
}
