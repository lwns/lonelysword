//package com.timper.lib.di.feature
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import com.timper.lib.R
//import com.timper.lib.R2
//import com.timper.lib.databinding.ActLibBinding
//import com.timper.lonelysword.annotations.apt.AfterViews
//import com.timper.lonelysword.annotations.apt.BeforeViews
//import com.timper.lonelysword.annotations.apt.Dagger
//import com.timper.lonelysword.annotations.apt.RootView
//import com.timper.lonelysword.base.AppActivity
//
///**
// * User:
// * Date:
// * Description:
// * FIXME
// */
//@Dagger
//@RootView(R2.layout.act_lib)
//class LibActivity : AppActivity<LibViewModel, ActLibBinding>() {
//
//    companion object {
//        @JvmOverloads
//        fun instance(context: Context, bundle: Bundle? = null) {
//            val intent = Intent(context, LibActivity::class.java)
//            if (bundle != null) {
//                intent.putExtra("data", bundle)
//            }
//            context.startActivity(intent)
//        }
//    }
//
//    @AfterViews
//    fun afterViews() {
//    }
//
//    @BeforeViews
//    fun beforViews() {
//    }
//}
//
