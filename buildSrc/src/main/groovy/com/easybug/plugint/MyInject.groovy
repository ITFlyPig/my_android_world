package com.easybug.plugint


import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

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
                def name = file.name
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('$')//代理类
                        && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                    project.logger.error("需要处理的Class文件：" + file.getAbsolutePath())
                    byte[] bytes = ClassUtil.inject(file.path)
                    def classPath = file.parentFile.absolutePath + File.separator + name
                    FileOutputStream fos = new FileOutputStream(classPath)
                    fos.write(bytes)
                    fos.close()
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
        return JarUtil.injectJar(new File(jarPath), tempDir, project, needPackageNames)

    }
}