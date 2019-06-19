import com.easybug.plugint.JavassistHelper
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class JarUtil{

    public static void injectJar(File jarFile, String tempDir, String needPackageName, Project project) {

        JavassistHelper.instance.appendClassPath(jarFile.path)
        //加入android.jar，否则找不到android相关的所有类
        JavassistHelper.instance.appendClassPath(project.android.bootClasspath[0].toString())

        //读取原来的jar
        JarFile originJar = new JarFile(jarFile)

        //输出的临时jar文件
        String  hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8)//避免和现有的jar文件重复
        File outputJar = new File(tempDir, hexName + jarFile.getName())
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
                def className = entryName.replace(".class", "")
                modifiedClassBytes = modifyClasses(originClassBytes, className)
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(originClassBytes)//使用未修改的字节码
            } else {
                jarOutputStream.write(modifiedClassBytes)//使用修改后的字节码
            }


        }

    }


    /**
     * 获得修改后的类字节码
     * @param originClassBytes  未修改的字节码
     * @param className   类名称
     * @return
     */
    private static byte[] modifyClasses(byte[] originClassBytes, String className) {
        CtClass ctClass = JavassistHelper.instance.getClass()
        if (ctClass == null) {
            //没有获取到想要修改的class
            return null
        }
        if (ctClass.isFrozen()) ctClass.defrost()
        //getDeclaredMethods获取自己申明的方法，getMethods()会把所有父类的方法都加上
        for (CtMethod ctmethod : ctClass.getDeclaredMethods()) {
            ctmethod.insertAfter("MethodCall.onEnd(" + ctmethod.longName + ");")
        }

        byte [] modifyClassBytes = ctClass.toBytecode()
        JavassistHelper.instance.detach(ctClass)
        return modifyClassBytes;
    }

}