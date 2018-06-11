package com.timper.lonelysword.app.feature;

import android.databinding.ObservableField;
import com.timper.lonelysword.ActivityScope;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 05/06/2018
 * Description:
 * FIXME
 */
@ActivityScope
public class MainViewModel {

  public ObservableField<String> hellhellowow = new ObservableField<>("DFAFASDF");

  @Inject public MainViewModel() {
  }
}
