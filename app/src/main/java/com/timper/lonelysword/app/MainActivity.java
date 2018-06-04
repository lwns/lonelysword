package com.timper.lonelysword.app;

import android.widget.Toast;
import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.BeforeViews;
import com.timper.lonelysword.annotations.apt.DisableNetwork;
import com.timper.lonelysword.annotations.apt.EnableNetwork;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.annotations.aspectj.CheckLogin;
import com.timper.lonelysword.annotations.aspectj.SingleClick;
import com.timper.lonelysword.annotations.aspectj.Time;
import com.timper.lonelysword.app.databinding.ActMainBinding;
import com.timper.lonelysword.base.AppActivity;
import javax.inject.Inject;

@RootView(R.layout.act_main) public class MainActivity extends AppActivity<ActMainBinding> {

  @Inject MainModelAdapter adapter;

  @BeforeViews void beforViews() {
    Toast.makeText(this, "beforviews", Toast.LENGTH_LONG).show();
  }

  @AfterViews void AfterViews() {
    addFragment(R.id.fl_content, new MainFragment());
    Toast.makeText(this, "AfterViews", Toast.LENGTH_LONG).show();
  }

  @DisableNetwork void disable() {
    Toast.makeText(this, "disable", Toast.LENGTH_LONG).show();
  }

  @EnableNetwork @CheckLogin void enable() {
    Toast.makeText(this, "enable", Toast.LENGTH_LONG).show();
  }

  @SingleClick @Time void click() {
    Toast.makeText(this, "SingleClick", Toast.LENGTH_SHORT).show();
  }
}
