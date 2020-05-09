package com.timper.module.feature.garden.main

import androidx.databinding.ObservableArrayList
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import com.timper.module.data.bean.Article
import com.timper.module.data.remote.GetArticlesUseCase
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class MainViewModel @Inject
constructor(var getArticlesUseCase: GetArticlesUseCase) : AppViewModel() {

    var datas: ObservableArrayList<Article> = ObservableArrayList()

    override fun afterViews() {
        getArticlesUseCase.execute(null).subscribe({
            it.datas?.let {
                datas.clear()
                datas.addAll(it)
            }
        },{

        },{

        })
    }
}