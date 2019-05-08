package com.timper.lib.di.feature;

import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.base.AppViewModel;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
@ActivityScope
public class LibViewModel extends AppViewModel {
    @Inject
    public LibViewModel() {
    }
}
