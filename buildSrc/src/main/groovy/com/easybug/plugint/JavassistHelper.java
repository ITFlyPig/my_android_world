package com.easybug.plugint;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class JavassistHelper {
    final ClassPool classPool;

    private JavassistHelper(){
        classPool = ClassPool.getDefault();
    }

    private static class Holder{
        private static JavassistHelper helper = new JavassistHelper();
    }

    public static JavassistHelper getInstance() {
        return Holder.helper;
    }

    /**
     * 添加类搜索路径
     * @param path
     */
    public void appendClassPath(String path) {
        try {
            classPool.appendClassPath(path);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public CtClass getClass(String className) {
        try {
            className = className.replaceAll("/", ".");
            return classPool.getCtClass(className);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void detach(CtClass ctClass){
        ctClass.detach();//用完一定记得要卸载，否则pool里的永远是旧的代码
    }

    /**
     * 载入包/类
     * @param packageName
     */
    public void importPackage(String packageName) {
        classPool.importPackage(packageName);
    }

}
