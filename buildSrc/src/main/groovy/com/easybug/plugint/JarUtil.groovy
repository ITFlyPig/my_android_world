package com.easybug.plugint

import com.easybug.plugint.JavassistHelper
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.CodeAttribute
import javassist.bytecode.LocalVariableAttribute
import javassist.bytecode.MethodInfo
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.internal.impldep.com.google.api.client.util.ArrayMap
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class JarUtil {

    /**
     * jar中的字节码注入代码
     * @param jarFile 需要被注入的jar包
     * @param tempDir 临时文件存放
     * @param project
     */
    public static File injectJar(File jarFile, String tempDir, Project project, String[] needPackages) {

//        JavassistHelper.instance.appendClassPath(jarFile.path)
        //加入android.jar，否则找不到android相关的所有类
//        JavassistHelper.instance.appendClassPath(project.android.bootClasspath[0].toString())
        LogUtil.e("bootClasspath 路径：" + project.android.bootClasspath[0].toString())

        //读取原来的jar
        JarFile originJar = new JarFile(jarFile)

        //输出的临时jar文件
        String hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8)
        //避免和现有的jar文件重复
        File outputJar = new File(tempDir, hexName + jarFile.getName())
        LogUtil.e("原来的jar文件：" + jarFile.path)
        LogUtil.e("修改后的jar文件：" + outputJar)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))

        //开始遍历jar文件里面的class
        Enumeration enumeration = originJar.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = originJar.getInputStream(jarEntry)

            String entryName = jarEntry.getName()

            ZipEntry zipEntry = new ZipEntry(entryName)
            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null//修改后的class字节码
            byte[] originClassBytes = IOUtils.toByteArray(inputStream)//未修改的class字节码


            if (entryName.endsWith(".class")) {//确认是class文件，然后修改
                LogUtil.e("开始修改的class文件：" + entryName)
//                def className = entryName.replace(".class", "")
//                modifiedClassBytes = modifyClasses(className, needPackages)
                ClassBean bean = new ClassBean(entryName, originClassBytes)
                IClassHandle iClassHandle = new ASMClassHandle(bean)
                modifiedClassBytes = iClassHandle.insertCode()
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(originClassBytes)//使用未修改的字节码
                LogUtil.e("class文件修改失败")
            } else {
                jarOutputStream.write(modifiedClassBytes)//使用修改后的字节码
                LogUtil.e("class文件修改成功---------------end")
            }


        }

        jarOutputStream.close()
        originJar.close()
        return outputJar
    }


    /**
     * 获得修改后的类字节码
     * @param originClassBytes 未修改的字节码
     * @param className 类名称
     * @return
     */
    private static byte[] modifyClasses(String className, String[] needPackages) {
        CtClass ctClass = JavassistHelper.instance.getClass(className)
        LogUtil.e("获取到对应的CtClass")

        boolean needHandle = isNeedPackage(ctClass.packageName, needPackages)
        LogUtil.e("是否需要处理：" + needHandle)

        if (ctClass == null || !needHandle) {
            //没有获取到想要修改的class
            return null
        }
        if (ctClass.isFrozen()) ctClass.defrost()
        //getDeclaredMethods获取自己申明的方法，getMethods()会把所有父类的方法都加上
        if (!isAopClass(ctClass.name)) {
            for (CtMethod ctmethod : ctClass.getDeclaredMethods()) {
                LogUtil.e("开始对方法插入切面代码：" + ctmethod.longName + " className:" + ctClass.name)
                if (!ctmethod.isEmpty() && !ctmethod.getMethodInfo().isConstructor()) {
//有方法体且不是构造方法才插入
                    LogUtil.e("有方法体，插入start")
//                ctmethod.insertAfter("System.out.println(\"Aop插入\");")

                    String beforeCode = "com.wangyuelin.performance.MethodCall.onStart(\"" + ctmethod.longName + "\"," + paramCode(getParamNames(ctmethod)) + ");"
                    String afterCode = "com.wangyuelin.performance.MethodCall.onEnd(\"" + ctmethod.longName + "\");\n"
                    LogUtil.e("插入代码：" + afterCode + "\n " + beforeCode)
                    ctmethod.insertBefore(beforeCode)
                    ctmethod.insertAfter(afterCode)
                    LogUtil.e("有方法体，插入end")


                }

            }
        }

        byte[] modifyClassBytes = ctClass.toBytecode()
        JavassistHelper.instance.detach(ctClass)

        return modifyClassBytes
    }

    public static boolean isNeedHandle(File jarFile, String[] packages) {
        if (packages == null || packages.length == 0) {
            return false
        }

        //遍历jar，并且处理里面的class
        eachClass(jarFile, new JarEachListener() {

            @Override
            void onEntry(JarEntry jarEntry) {


            }
        })

    }

    /**
     * 遍历jar包
     * @param jarFile 需要被遍历的jar文件
     * @param listener 每一个被遍历到的Entry的回调
     */
    public static void eachClass(File jarFile, JarEachListener listener) {
        if (listener == null || jarFile == null) {
            return
        }
        JarFile originJar = new JarFile(jarFile)
        //开始遍历jar文件里面的class
        Enumeration enumeration = originJar.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            listener.onEntry(jarEntry)
        }
    }

    interface JarEachListener {
        void onEntry(JarEntry jarEntry);
    }

    /**
     * 是否是aop的类
     * @param className
     * @return
     */
    private static boolean isAopClass(String className) {
        return className.contains("com.wangyuelin.performance.MethodCall")
    }

    /**
     * 是否是需要处理的包
     * @param packageStr
     * @return
     */
    private static boolean isNeedPackage(String packageStr, String[] needPackages) {
        if (needPackages == null || needPackages.length == 0) {
            return false
        }

        for (int i = 0; i < needPackages.length; i++) {
            if (packageStr.contains(needPackages[i])) {
                return true
            }
        }
        return false
    }

    /**
     * 获取方法的参数名称
     * @param ctMethod
     * @return
     */
    private static String[] getParamNames(CtMethod ctMethod) {
        if (ctMethod == null) {
            return null
        }
        MethodInfo methodInfo = ctMethod.getMethodInfo()
        CtClass[] parameterTypes = ctMethod.getParameterTypes()
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute()
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag)
        if (attr == null) {
            return null
        }
        //方法本地参数的个数
        int localParamSize = attr.tableLength()
        for (int i = 0; i < localParamSize; i++) {
            LogUtil.e("本地参数：" + attr.variableName(i))
        }
        //形参是放在本地方法表的最后的
        String[] paramNames = new String[parameterTypes.length]
        int j = 0
        for (int i = localParamSize - parameterTypes.length; i < localParamSize; i++) {
            paramNames[j] = attr.variableName(i)//取本地方法表的最后几个参数（也就是形参）
            j++
        }
        LogUtil.e("获取到的参数：" + paramNames.toString())
        return paramNames
    }

    /**
     * 将参数转为代码
     * @param paramNames
     * @return
     */
    private static String paramCode(String[] paramNames) {
        String code = "null"
        if (paramNames == null) {
            return code
        }
        code = "new Object[]{"
        for (int i = 0; i < paramNames.length; i++) {
            code += paramNames[i]
            if (i < (paramNames.length - 1)) {
                code += ","
            }
        }
        code += "}"
        return code
    }

    /**
     * 由于Javassist获取的变量表，就算是实例方法，0不一定存this，也就是方法的参数不一定存在变量表最开始，没法准确获取
     * 修改为ASM获取方法参数名称
     * @param className类的全名称
     * @return
     */
    private static HashMap<String, List<String>> parseParam(String className) {
//        final String className = method.getDeclaringClass().getName();

        HashMap<String, List<String>> map = new ArrayMap<>()
        ClassReader cr = new ClassReader(className)
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        cr.accept(new ClassVisitor() {
            @Override
            MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
                map.put(name, new ArrayList<>())
                if (mv != null) {
                    mv = new MethodVisitor() {
                        @Override
                        void visitLocalVariable(String vname, String vdesc, String vsignature, Label start, Label end, int index) {
                            super.visitLocalVariable(vname, vdesc, vsignature, start, end, index)
                            map.get(name).add(vname)
                        }
                    }
                }

                return mv
            }
        }, ClassReader.SKIP_DEBUG)
        return map
    }


}