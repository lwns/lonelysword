package com.timper.lonelysword.support.base.tab;

import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import com.timper.lonelysword.base.AppFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class TabFragment<V extends TabViewModel, T extends ViewDataBinding> extends AppFragment<V, T> {

  public final List<Fragment> fragments = new ArrayList<>();
}
