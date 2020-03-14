package com.timper.module.feature.garden.usecaseauto

import androidx.databinding.ObservableField
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import com.timper.module.data.remote.GetArticlesUseCase
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class UsecaseautoViewModel @Inject constructor(val getArticlesUseCase: GetArticlesUseCase) : AppViewModel() {

    val content = ObservableField<String>("")
    override fun afterViews() {
        super.afterViews()
        getArticlesUseCase.execute(null).subscribe{
            content.set(it.toString())
        }
    }
}