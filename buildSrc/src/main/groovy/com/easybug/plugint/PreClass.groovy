package com.easybug.plugint

import com.android.build.api.transform.*
import com.google.common.collect.Sets
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class PreClass extends Transform {

    Project project

    PreClass(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "Aop"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.EXTERNAL_LIBRARIES, )
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            try {
                //获取到插入代码所在的jar包，然后添加到Javassist的类搜索路径
                input.jarInputs.each {
                    if (it.file.getAbsolutePath().contains("performance")) {
                        LogUtil.e("将jar包添加到搜索路径：" + it.file.path)
                        JavassistHelper.instance.appendClassPath(it.file.path)
                    }
                }
                input.jarInputs.each {
                    String[] packages = new String[1]
                    packages[0] = "wangyuelin"
                    File modifiedFile = MyInject.injectJar(it.file.getAbsolutePath(), context.temporaryDir.path, packages, project)
                    String outputFileName = null

                    if (modifiedFile != null) {//使用修改后的jar文件
                        outputFileName = modifiedFile.name
                    } else {//使用未修改的jar文件
                        outputFileName = it.name.replace(".jar", "") + '-' + it.file.path.hashCode()
                        modifiedFile = it.file
                    }

                    def output = outputProvider.getContentLocation(outputFileName, it.contentTypes, it.scopes, Format.JAR)//获得输出的路劲
                    LogUtil.e("将修改后的从：" + modifiedFile.path)
                    LogUtil.e("拷贝到：" + output.path)
                    FileUtils.copyFile(modifiedFile, output)
                }
            } catch (Exception e) {
                project.logger.err e.getMessage()
            }
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                MyInject.injectDir(directoryInput.file.absolutePath, "com", project)
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        JavassistHelper.instance.clearImportedPackages()

    }
}