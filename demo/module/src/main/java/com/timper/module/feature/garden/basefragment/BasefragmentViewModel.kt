package com.timper.module.feature.garden.basefragment

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
open class BasefragmentViewModel @Inject constructor() : AppViewModel() {

    val  content  = ObservableField<String>("")
    override fun afterViews() {
        super.afterViews()
        content.set(content.get()+"\n"+"这是ViewModel父类的AfterViews方法调用")
    }
}