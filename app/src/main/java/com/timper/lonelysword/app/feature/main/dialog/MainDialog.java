package com.timper.lonelysword.app.feature.main.dialog;

import android.view.Gravity;
import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.databinding.DlgMainBinding;
import com.timper.lonelysword.base.dialog.AppDialog;
import com.timper.lonelysword.base.dialog.DialogBuilder;

/**
 * User: tangpeng.yang
 * Date: 2018/12/4
 * Description:
 * FIXME
 */
@Dagger
@RootView(R.layout.dlg_main)
public class MainDialog extends AppDialog<DlgViewModel, DlgMainBinding> {

  public final static synchronized MainDialog instance() {
    return new MainDialog();
  }

  @Override
  public DialogBuilder initBuilder() {
    return DialogBuilder.newDialog()
                        .setGravity(Gravity.BOTTOM)
                        .setCancelable(true);
  }
}
