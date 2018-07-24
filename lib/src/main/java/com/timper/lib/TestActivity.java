package com.timper.lib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * User: tangpeng.yang
 * Date: 19/07/2018
 * Description:
 * FIXME
 */
//@RootView(R2.layout.act_test) public class TestActivity<V extends TestViewModel, T extends ActTestBinding>
//    extends AppActivity<V, T> {
//}
public class TestActivity extends AppCompatActivity {

  @BindView(R2.id.tv_content) TextView tvContent;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.act_test);
    ButterKnife.bind(this);
  }
}