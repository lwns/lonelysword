package com.timper.module.feature.main

import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.annotations.apt.ViewModel
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.base.AppFragment
import com.timper.module.databinding.FrgMainBinding
import com.timper.module.R2
import com.timper.module.feature.GardenActivity

import javax.inject.Inject

@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_main)
class MainFragment : AppFragment<MainViewModel,FrgMainBinding>() {

    @AfterViews
    internal fun afterViews() {
    }

    companion object {
        fun instance(): MainFragment {
            return MainFragment()
        }
    }
}
