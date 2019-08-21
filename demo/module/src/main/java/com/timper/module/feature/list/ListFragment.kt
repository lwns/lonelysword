package com.timper.module.feature.list

import com.timper.bindingadapter.collection.itembindings.OnItemBindClass
import com.timper.bindingadapter.collection.itembindings.OnItemBindEvent
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppFragment
import com.timper.module.BR
import com.timper.module.R
import com.timper.module.R2
import com.timper.module.data.bean.Article
import com.timper.module.databinding.FrgListBinding
import com.timper.module.feature.GardenActivity


@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_list)
class ListFragment : AppFragment<ListViewModel,FrgListBinding>() {

    /**
     * drawerBinding
     */
    var itemBinding: OnItemBindClass<Article> =
        OnItemBindClass<Article>().map(Article::class.java, OnItemBindEvent<Article>(BR.viewModel, R.layout.item_main))

    @AfterViews
    internal fun afterViews() {
    }

    companion object {
        fun instance(): ListFragment {
            return ListFragment()
        }
    }
}
