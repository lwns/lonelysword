package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.RegisterTransform
import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Simple version of AutoRegister plugin for ARouter
 * @author billy.qi email: qiyilike@163.com
 * @since 17/12/06 15:35
 */
public class PluginLaunch implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        //only application module needs this plugin to generate register code
        if (isApp) {
            Logger.make(project)

            Logger.i('Project enable arouter-register plugin')

            def android = project.extensions.getByType(AppExtension)
            def transformImpl = new RegisterTransform(project)

            //init arouter-auto-register settings
            ArrayList<ScanSetting> list = new ArrayList<>(3)
            list.add(new ScanSetting(ScanSetting.INTERFACE_PACKAGE_NAME+'IRouteRoot'))
            list.add(new ScanSetting(ScanSetting.INTERFACE_PACKAGE_NAME+'IInterceptorGroup'))
            list.add(new ScanSetting(ScanSetting.INTERFACE_PACKAGE_NAME+'IProviderGroup'))
            ArrayList<ScanSetting> daggerList = new ArrayList<>(1)
            daggerList.add(new ScanSetting(ScanSetting.DAGGER_INTERFACE_PACKAGE_NAME+'MultiModule'))
            RegisterTransform.registerList = list
            RegisterTransform.daggerRegisterList = daggerList
            //register this plugin
            android.registerTransform(transformImpl)
        }
    }

}
