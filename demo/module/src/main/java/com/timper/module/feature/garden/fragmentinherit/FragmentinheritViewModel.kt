package com.timper.module.feature.garden.fragmentinherit

import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import com.timper.module.feature.garden.basefragment.BasefragmentViewModel
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class FragmentinheritViewModel @Inject constructor() : BasefragmentViewModel() {

    override fun afterViews() {
        super.afterViews()
        content.set(content.get()+"\n"+"这是ViewModel子类的AfterViews方法调用")
    }
}