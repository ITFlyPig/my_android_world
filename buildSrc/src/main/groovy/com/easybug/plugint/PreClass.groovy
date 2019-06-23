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

                input.jarInputs.each {
                    String[] packages = new String[1]
                    packages[0] = "wangyuelin"
                    ClassUtil.tempDir = context.temporaryDir.path
                    File modifiedFile = MyInject.injectJar(it.file.getAbsolutePath(), context.temporaryDir.path, packages, project)

                    if (modifiedFile != null) {//使用修改后的jar文件
                        outputFileName = modifiedFile.name
                    } else {//使用未修改的jar文件
                        outputFileName = it.name.replace(".jar", "") + '-' + it.file.path.hashCode()
                        modifiedFile = it.file
                    }

                    def output = outputProvider.getContentLocation(outputFileName, it.contentTypes, it.scopes, Format.JAR)//获得输出的路劲
                    println "将修改后的jar拷贝到：" + output.path
                    FileUtils.copyFile(modifiedFile, output)
                }
            } catch (Exception e) {

                LogUtil.e( "Preclass 异常" + e.getMessage())
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
    }
}