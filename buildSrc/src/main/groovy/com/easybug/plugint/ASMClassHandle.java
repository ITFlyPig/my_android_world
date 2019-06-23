package com.easybug.plugint;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * class字节码插入代码
 */
public class ASMClassHandle implements IClassHandle {
    private static boolean test;

    private ClassBean classBean;

    public ASMClassHandle(ClassBean classBean) {
        this.classBean = classBean;
    }

    @Override
    public boolean isNeedHanle(String signature) {
        return Packages.contains(ClassUtil.getPackage(signature));
    }

    @Override
    public byte[] insertCode() {
        if (classBean == null || classBean.bytes == null) {
            return null;
        }
        ClassReader classReader = new ClassReader(classBean.bytes);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new AopClassAdapter(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES); //EXPAND_FRAMES
        byte[] bytes = classWriter.toByteArray();
        if (!test && classBean.path.contains("ZipUtils")) {
            test = true;
            String path = "/Users/yuelinwang/Documents/安卓出名的三方框架/Aop/class文件" + (classBean.path.substring(classBean.path.lastIndexOf("/") + 1));
            System.out.println("保存修改后的class文件：" + path);
            ClassUtil.saveToFile(bytes, path);
        }
        return bytes;
    }
}
