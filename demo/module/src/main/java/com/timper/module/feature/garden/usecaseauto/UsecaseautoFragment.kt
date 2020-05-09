package com.timper.module.feature.garden.usecaseauto

import com.timper.module.R2
import com.timper.module.databinding.FrgUsecaseautoBinding
import com.timper.module.feature.garden.GardenActivity
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.BeforeViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppFragment

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_usecaseauto)
class UsecaseautoFragment : AppFragment<UsecaseautoViewModel, FrgUsecaseautoBinding>() {

    companion object {
        fun instance(): UsecaseautoFragment {
            return UsecaseautoFragment()
        }
    }

    @AfterViews
    fun afterViews() {

    }

    @BeforeViews
    fun beforViews() {

    }
}
