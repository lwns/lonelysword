package com.timper.module.feature.baseclass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.timper.module.R2
import com.timper.module.databinding.ActBaseclassBinding
import com.timper.lonelysword.annotations.apt.AfterViews
import com.timper.lonelysword.annotations.apt.BeforeViews
import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.lonelysword.annotations.apt.RootView
import com.timper.lonelysword.base.AppActivity
import com.timper.module.feature.activityinherit.ActivityinheritActivity

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@RootView(R2.layout.act_baseclass)
open class BaseclassActivity<V : BaseclassViewModel> : AppActivity<V, ActBaseclassBinding>() {

    val TAG  = ActivityinheritActivity::class.java.simpleName

    companion object {
        @JvmOverloads
        fun instance(context: Context, bundle: Bundle? = null) {
            val intent = Intent(context, BaseclassActivity::class.java)
            if (bundle != null) {
                intent.putExtra("data", bundle)
            }
            context.startActivity(intent)
        }
    }

    @AfterViews
    fun afterViews() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是父类的AfterViews方法调用")
    }

    @BeforeViews
    fun beforViews() {
        viewModel.content.set(viewModel.content.get()+"\n"+"这是父类的BeforeViews方法调用")

    }
}

