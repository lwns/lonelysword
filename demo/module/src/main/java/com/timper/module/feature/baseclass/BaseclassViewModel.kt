package com.timper.module.feature.baseclass

import androidx.databinding.ObservableField
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import javax.inject.Inject


/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
open class BaseclassViewModel @Inject constructor() : AppViewModel() {

    open val content : ObservableField<String>  = ObservableField("")
    override fun afterViews() {
        super.afterViews()

        content.set(content.get()+"\n"+"这是ViewModel父类的AfterViews方法调用")
    }
}

