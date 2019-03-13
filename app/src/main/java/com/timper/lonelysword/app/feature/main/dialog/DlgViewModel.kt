package com.timper.lonelysword.app.feature.main.dialog

import androidx.databinding.ObservableField
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import javax.inject.Inject

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
@ActivityScope
class DlgViewModel @Inject constructor() : AppViewModel() {

//    val activity: AppActivity<*, *>

  init {
  }

  var hellow = ObservableField("sdfadf")
}
