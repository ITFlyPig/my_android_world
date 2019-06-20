package com.easybug.plugint
import com.wangyuelin.easybug.aop.Util
import org.gradle.api.Project

import java.util.jar.JarEntry

/**
 * 代码的注入
 */
public class MyInject {
    /**
     * 对文件夹下的class注入
     * @param dir 存放class的文件夹路径
     * @param needPackageName 需要注入的包名/包名的一部分
     * @param project
     */
    public static void injectDir(String dirPath, String needPackageName, Project project) {
        File dir = new File(dirPath)
        project.logger.error("输入：" + dirPath)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath//确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('$')//代理类
                        && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                    project.logger.error(filePath)

                }

            }
        }
    }

    /**
     * 对jar进行注入
     * @param path
     * @param needPackageName
     * @param project
     */
    public static File injectJar(String jarPath, String tempDir, String[] needPackageNames, Project project) {
        if (jarPath == null) {
            return
        }

        //1.判断是否是需要处理饿jar（jar是否是处理过的）

        //2.处理jar
        return JarUtil.injectJar(new File(jarPath), tempDir, project)

    }
}