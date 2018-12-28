package com.timper.lonelysword.app.feature.main

import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.app.databinding.ActMainBinding
import com.timper.lonelysword.base.AppActivity
import com.timper.lonelysword.base.AppViewModel
import javax.inject.Inject

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope
class MainViewModel @Inject constructor() : AppViewModel() {

//    val activity: AppActivity<*, *>

    init {
    }

    var hellow = ObservableField("sdfadf")
}
