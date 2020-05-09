package com.timper.module.feature.activityinherit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.timper.module.R2
import com.timper.module.databinding.ActActivityinheritBinding
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.BeforeViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppActivity
import com.timper.module.feature.baseclass.BaseclassActivity

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@Dagger
@RootView(R2.layout.act_baseclass)
class ActivityinheritActivity : BaseclassActivity<ActivityinheritViewModel>() {


    companion object {
        @JvmOverloads
        fun instance(context: Context, bundle: Bundle? = null) {
            val intent = Intent(context, ActivityinheritActivity::class.java)
            if (bundle != null) {
                intent.putExtra("data", bundle)
            }
            context.startActivity(intent)
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

