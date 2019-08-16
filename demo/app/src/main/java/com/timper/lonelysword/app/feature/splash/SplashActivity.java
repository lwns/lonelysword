package com.timper.lonelysword.app.feature.splash;

import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.app.databinding.ActSplashBinding;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.feature.splash.test.TestFragment;
import com.timper.lonelysword.base.AppActivity;
import com.timper.module.feature.GardenActivity;
import io.reactivex.Flowable;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@Dagger
@RootView(R.layout.act_splash)
public class SplashActivity extends AppActivity<SplashViewModel, ActSplashBinding> {


  @AfterViews
  void init(){
    Flowable.timer(2000, TimeUnit.MICROSECONDS).subscribe(it->{
      GardenActivity.Companion.instance(this);
    });
  }

}


