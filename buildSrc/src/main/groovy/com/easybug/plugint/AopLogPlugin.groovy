package com.easybug.plugint

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension
import com.easybug.plugint.PreClass

/**
 * 绝对不能使用依赖的方式依赖插件，会报错，插件和Library是不一样的
 */

class AopLogPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final def log = project.logger
        log.error("开始添加AOP代码")
//        def android = project.extensions.findByType(AppExtension.class)
//        android.registerTransform(new PreClass(project))

        project.android.registerTransform(new PreClass(project))

    }
}