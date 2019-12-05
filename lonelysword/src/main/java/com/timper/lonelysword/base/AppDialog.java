package com.timper.lonelysword.base;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2018/12/4
 * Description:
 * FIXME
 */
public abstract class AppDialog<V extends AppViewModel, T extends ViewDataBinding> extends AbsDialog implements HasSupportFragmentInjector {

    public T binding;
    public V viewModel;
    @Inject
    public ViewModelFactor<V> factor;

    private Unbinder unbinder;
    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    public AppDialog() {
        super();
        unbinder = Lonelysword.bind(this);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public void initViews(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        unbinder.initViews(container);
    }
}
