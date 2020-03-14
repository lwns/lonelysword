package com.timper.module.feature.garden.daggerlibclass

import com.timper.lonelysword.annotations.apt.Dagger
import com.timper.module.feature.garden.GardenActivity
import dagger.Module
import javax.inject.Inject

/**
 * User: tangpeng.yang
 * Date: 2020/3/14
 * Description:
 * FIXME
 */
@Module
@Dagger(GardenActivity::class)
class DaggerLibraryClass @Inject constructor(){

    val content  = "我是将要注入到GardenActivity的class，只要有注解@Dagger(GardenActivity::class)的类都可以将我注入"
}
