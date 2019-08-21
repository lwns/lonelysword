package com.timper.module.feature.list

import android.databinding.ObservableArrayList
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import com.timper.module.data.bean.Article
import com.timper.module.data.remote.GetTopsUseCase
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class ListViewModel @Inject
constructor(var getTopsUseCase: GetTopsUseCase) : AppViewModel(){
    var datas: ObservableArrayList<Article> = ObservableArrayList()

    override fun afterViews() {
        getTopsUseCase.execute(null).subscribe({
            it.datas?.let {
                datas.clear()
                datas.addAll(it)
            }
        },{
        },{
        })
    }
}