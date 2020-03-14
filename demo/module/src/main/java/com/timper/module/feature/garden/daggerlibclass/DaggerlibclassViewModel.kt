package com.timper.module.feature.garden.daggerlibclass

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
class DaggerlibclassViewModel @Inject constructor(var daggerLibraryClass: DaggerLibraryClass) : AppViewModel() {

    val content = ObservableField<String>("")
    override fun afterViews() {
        super.afterViews()

        content.set(daggerLibraryClass.content)
    }
}