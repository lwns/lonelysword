package com.timper.lonelysword.app.feature;

import android.widget.Toast;
import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.BeforeViews;
import com.timper.lonelysword.annotations.apt.DisableNetwork;
import com.timper.lonelysword.annotations.apt.EnableNetwork;
import com.timper.lonelysword.annotations.apt.ModelAdapter;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.annotations.apt.ViewModel;
import com.timper.lonelysword.annotations.aspectj.CheckLogin;
import com.timper.lonelysword.annotations.aspectj.SingleClick;
import com.timper.lonelysword.annotations.aspectj.Time;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.databinding.ActMainBinding;
import com.timper.lonelysword.app.feature.main.MainFragment;
import com.timper.lonelysword.base.AppActivity;
import com.timper.lonelysword.base.ModelAdapterFactor;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 08/06/2018
 * Description:
 * FIXME
 */
public class MainActivity extends BaseMainActivity {

  //@Inject ModelAdapterFactor<MainModelAdapter> factor;
  //@ModelAdapter("factor") MainModelAdapter adapter;
  //@Inject @ViewModel MainViewModel viewModel;

  @BeforeViews void beforViews() {
    Toast.makeText(this, "beforviews", Toast.LENGTH_SHORT).show();
  }

  @AfterViews void AfterViews() {
    //addFragment(R.id.fl_content, new MainFragment());
    Toast.makeText(this, "AfterViews", Toast.LENGTH_SHORT).show();
  }

  @DisableNetwork void disable() {
    Toast.makeText(this, "disable", Toast.LENGTH_SHORT).show();
  }

  @EnableNetwork @CheckLogin void enable() {
    Toast.makeText(this, "enable", Toast.LENGTH_SHORT).show();
  }

  @SingleClick @Time void click() {
    Toast.makeText(this, "SingleClick", Toast.LENGTH_SHORT).show();
  }
}
