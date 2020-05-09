package com.timper.lonelysword.support.base.tab;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import com.timper.lonelysword.base.AppFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class TabFragment<V extends TabViewModel, T extends ViewDataBinding> extends AppFragment<V, T> {

  public final List<Fragment> fragments = new ArrayList<>();
}
