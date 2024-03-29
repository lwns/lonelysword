package com.timper.module.feature.garden.detail

import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppFragment
import com.timper.module.R2
import com.timper.module.databinding.FrgDetailBinding
import com.timper.module.feature.garden.GardenActivity

@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_detail)
class DetailFragment : AppFragment<DetailViewModel,FrgDetailBinding>() {


    @AfterViews
    internal fun afterViews() {
    }

    companion object {
        fun instance(): DetailFragment {
            return DetailFragment()
        }
    }
}
