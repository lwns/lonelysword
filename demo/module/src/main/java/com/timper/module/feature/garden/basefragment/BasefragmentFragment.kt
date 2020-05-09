package com.timper.module.feature.garden.basefragment

import com.timper.module.R2
import com.timper.module.databinding.FrgBasefragmentBinding
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
@RootView(R2.layout.frg_basefragment)
open class BasefragmentFragment<V : BasefragmentViewModel> : AppFragment<V, FrgBasefragmentBinding>() {


    @AfterViews
    fun afterViews() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是父类的AfterViews方法调用")
    }

    @BeforeViews
    fun beforViews() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是父类的BeforeViews方法调用")
    }
}
