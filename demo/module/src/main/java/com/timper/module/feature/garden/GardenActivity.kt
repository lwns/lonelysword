package com.timper.module.feature.garden

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppActivity
import com.timper.module.R
import com.timper.module.R2.layout
import com.timper.module.databinding.ActGardenBinding
import com.timper.module.feature.activityinherit.ActivityinheritActivity
import com.timper.module.feature.garden.daggerlibclass.DaggerlibclassFragment
import com.timper.module.feature.garden.fragmentinherit.FragmentinheritFragment
import com.timper.module.feature.garden.list.ListFragment
import com.timper.module.feature.garden.main.MainFragment
import com.timper.module.feature.garden.usecaseauto.UsecaseautoFragment


/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
@Dagger
@RootView(layout.act_garden)
class GardenActivity : AppActivity<GardenViewModel, ActGardenBinding>() {

    companion object {
        fun instance(context: Context) {
            val intent = Intent(context, GardenActivity::class.java)
            context.startActivity(intent)
        }
    }

    @AfterViews
    internal fun view() {

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.garden_activity_integration -> {
                    //启动activity继承demo
                    ActivityinheritActivity.instance(this)
                }
                R.id.garden_fragment_integration -> {
                    addFragment(R.id.fl_content, FragmentinheritFragment.instance())
                }
                R.id.garden_support_loadmore -> {
                    addFragment(R.id.fl_content, ListFragment.instance())
                }

                R.id.garden_dagger_add_library_class -> {
                    addFragment(R.id.fl_content, DaggerlibclassFragment.instance())
                }

                R.id.garden_usecase_auto -> {
                    addFragment(R.id.fl_content, UsecaseautoFragment.instance())
                }
            }
            true
        }

        //这是带Home旋转开关按钮
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            binding.toolbar,
            R.string.garden_drawer_open, R.string.garden_drawer_open
        )
        binding.drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

        addFragment(R.id.fl_content, MainFragment.instance())
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}
