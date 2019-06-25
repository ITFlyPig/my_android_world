package com.easybug.plugint;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;

/**
 * class字节码插入代码
 */
public class ASMClassHandle implements IClassHandle {

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
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new AopClassAdapter(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES); //EXPAND_FRAMES
        return classWriter.toByteArray();
    }
}
