package com.timper.module.feature.garden.list

import androidx.databinding.ObservableArrayList
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.support.base.loadmore.LoadMoreViewModel
import com.timper.module.data.bean.Article
import com.timper.module.data.remote.GetTopsUseCase
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class ListViewModel @Inject
constructor(var getTopsUseCase: GetTopsUseCase) : LoadMoreViewModel<Article>(){


    override fun refreshData(): Flowable<MutableList<Article>> {
        return getTopsUseCase.execute(null).flatMap { Flowable.just(it.datas) }
    }

    override fun loadMoreData(): Flowable<MutableList<Article>> {
        return getTopsUseCase.execute(null).flatMap { Flowable.just(it.datas) }
    }
}