package com.timper.module.feature.activityinherit

import com.timper.lonelysword.ActivityScope
import com.timper.module.feature.baseclass.BaseclassViewModel
import javax.inject.Inject


/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class ActivityinheritViewModel @Inject constructor() : BaseclassViewModel() {

    override fun afterViews() {
        super.afterViews()
        content.set(content.get()+"\n"+"这是ViewModel子类的AfterViews方法调用")
    }
}

