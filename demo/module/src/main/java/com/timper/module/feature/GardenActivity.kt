package com.timper.module.feature

import android.content.Context
import android.content.Intent
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppActivity
import com.timper.module.R
import com.timper.module.R2
import com.timper.module.databinding.ActGardenBinding
import com.timper.module.feature.list.ListFragment
import com.timper.module.feature.main.MainFragment


/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
@Dagger
@RootView(R2.layout.act_garden)
class GardenActivity : AppActivity<GardenViewModel, ActGardenBinding>() {
    @AfterViews
    internal fun view() {
        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.garden_fragment -> {
                    addFragment(R.id.fl_content, MainFragment.instance())
                }
                R.id.plant_list_fragment -> {
                    addFragment(R.id.fl_content, ListFragment.instance())
                }
            }
            true
        }

        //这是带Home旋转开关按钮
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

        addFragment(R.id.fl_content, MainFragment.instance())
    }

    companion object {
        fun instance(context: Context) {
            val intent = Intent(context, GardenActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}
