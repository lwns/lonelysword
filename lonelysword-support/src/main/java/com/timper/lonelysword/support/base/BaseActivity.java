package com.timper.lonelysword.support.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.timper.lonelysword.base.AppActivity;
import com.timper.lonelysword.base.AppViewModel;
import com.timper.lonelysword.support.utils.DensityUtil;
import com.timper.lonelysword.support.utils.StatusBarUtil;
import com.timper.lonelysword.support.view.LoadingDialog;

/**
 * User: tangpeng.yang
 * Date: 18/07/2018
 * Description:
 * FIXME
 */
public class BaseActivity<V extends AppViewModel, T extends ViewDataBinding> extends AppActivity<V, T> {
    private Fragment mFragment;

    LoadingDialog mLoadingDialog;

    protected boolean needRefresh = false;//当前页面结束时，是否需要刷新前一个页面
    protected int REQUEST_CODE = 0x0011;
    protected String REFRESH_PREVIOUS = "refresh_previous";//key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DensityUtil.setDefault(this);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        mLoadingDialog.show(fragmentManager);
    }

    public void dismissLoading() {
        if (mLoadingDialog == null) {
            return;
        }
        mLoadingDialog.dismiss();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DensityUtil.setDefault(this);
    }

    @Override
    public void addFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (mFragment != null) {
                    transaction.hide(mFragment)
                            .show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment)
                            .add(frameLayoutId, fragment);
                } else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
        }
    }

    @SuppressWarnings("unused")
    protected void replaceFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayoutId, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            needRefresh = data.getBooleanExtra(REFRESH_PREVIOUS, false);
            if (needRefresh) {
                refreshCurrentData();
            }
        }
    }

    /**
     * 刷新当前页面
     */
    protected void refreshCurrentData() {

    }

    @Override
    public void finish() {
        if (needRefresh) {
            Intent intentFinish = new Intent();
            intentFinish.putExtra(REFRESH_PREVIOUS, true);
            setResult(Activity.RESULT_OK, intentFinish);
        }
        super.finish();
    }
}
