package com.timper.module.feature.garden.fragmentinherit

import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.BeforeViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.module.feature.garden.GardenActivity
import com.timper.module.feature.garden.basefragment.BasefragmentFragment

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@Dagger(GardenActivity::class)
class FragmentinheritFragment : BasefragmentFragment<FragmentinheritViewModel>() {

    companion object {
        fun instance(): FragmentinheritFragment {
            return FragmentinheritFragment()
        }
    }

    @AfterViews
    fun after() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是子类的AfterViews方法调用")
    }

    @BeforeViews
    fun befor() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是子类的BeforeViews方法调用")
    }
}
