package com.lucius.tec.method.analysis

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.lucius.tec.method.analysis.core.ScannerTransform
import com.lucius.tec.method.analysis.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginLauncher implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        project.extensions.create("methodMonitorExt", MethodMonitorExtension)
        if (isApp) {
            Logger.make(project)
            def android = project.extensions.getByType(AppExtension)
            def transformImpl = new ScannerTransform(project)
            android.registerTransform(transformImpl)
        }
    }

}