package com.easybug.plugint


import com.easybug.plugint.util.LogUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 绝对不能使用依赖的方式依赖插件，会报错，插件和Library是不一样的
 */

class AopLogPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        init(project)

        LogUtil.e("开始添加切面代码")
//        def android = project.extensions.findByType(AppExtension.class)
//        android.registerTransform(new PreClass(project))

        AopConfig aopConfig = getConfig(project)

        println "aopConfig.isDebug:" +  aopConfig.isDebug
        project.android.registerTransform(new PreClass(aopConfig, project))

    }

    private void init(Project project) {
        LogUtil.init(project)

    }

    private AopConfig getConfig(Project project) {
        if (project == null) {
            return
        }

        AopExtension aopExtension = project.extensions.create("Aop", AopExtension)
        AopConfig aopConfig = new AopConfig.Builder()
                .setDebug(aopExtension.isDebug)
                .setAop(aopExtension.isAop)
                .build()
        return aopConfig

    }

}
