package com.timper.lonelysword.base;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
public abstract class AppFragment<V extends AppViewModel, T extends ViewDataBinding> extends DaggerFragment {
    public T binding;
    public V viewModel;
    @Inject
    public ViewModelFactor<V> factor;

    public View view;

    private Unbinder unbinder;

    public FragmentManager fragmentManager;

    public AppFragment() {
        unbinder = Lonelysword.bind(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentManager = getChildFragmentManager();
        return unbinder.initViews(container);
    }
}
