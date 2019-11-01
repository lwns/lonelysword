package com.timper.lonelysword.support.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.timper.lonelysword.base.AbsDialog;
import com.timper.lonelysword.support.R;

/**
 * User: tangpeng.yang
 * Date: 2019-09-18
 * Description:
 * FIXME
 */
public class LoadingDialog extends AbsDialog {

    @Override
    public void initViews(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.lonelysword_support_dlg_loading, container, true);
    }

    @Override
    public DialogBuilder initBuilder() {
        return AbsDialog.DialogBuilder.newDialog().setFullScreen(true).setCancelable(true).setGravity(Gravity.CENTER);
    }
}
