package com.timper.module.feature

import android.content.Context
import android.content.Intent
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppActivity
import com.timper.module.R
import com.timper.module.R2
import com.timper.module.databinding.ActGardenBinding

/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
@Dagger
@RootView(R2.layout.act_garden)
class GardenActivity : AppActivity<GardenViewModel, ActGardenBinding>() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    @AfterViews
    internal fun view() {
        navController = findNavController(this,R.id.garden_nav_fragment)
        // Set up ActionBar
        setSupportActionBar(binding.toolbar)

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
