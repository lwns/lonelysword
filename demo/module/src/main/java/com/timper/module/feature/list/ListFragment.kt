package com.timper.module.feature.list

import com.timper.lonelysword.base.AppFragment
import com.timper.module.databinding.FrgListBinding
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.annotations.apt.ViewModel
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.module.R2
import com.timper.module.feature.GardenActivity


@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_list)
class ListFragment : AppFragment<ListViewModel,FrgListBinding>() {


    @AfterViews
    internal fun afterViews() {
    }

    companion object {
        fun instance(): ListFragment {
            return ListFragment()
        }
    }
}
