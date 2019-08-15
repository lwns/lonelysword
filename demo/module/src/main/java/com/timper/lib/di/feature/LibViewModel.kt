package com.timper.lib.di.feature

import com.timper.lib.di.GetUseCaseUseCase
import com.timper.lonelysword.ActivityScope
import com.timper.lonelysword.base.AppViewModel
import javax.inject.Inject

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
class LibViewModel @Inject constructor(val getUseCaseUseCase: GetUseCaseUseCase) : AppViewModel() {

  override fun afterViews() {
    super.afterViews()
  }
}

