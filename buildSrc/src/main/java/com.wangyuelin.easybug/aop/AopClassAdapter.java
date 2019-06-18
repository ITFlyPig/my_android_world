package com.wangyuelin.easybug.aop;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 描述:决定需要处理哪些方法
 *
 * @outhor wangyuelin
 * @create 2018-10-19 下午4:18
 */
public class AopClassAdapter extends ClassVisitor implements Opcodes {
    private String className;
    private String mConstructorName = "<init>";
    private int classAccess;


    public AopClassAdapter(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;//类名称
        classAccess = access;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        System.out.println("name:" + name + "  desc:" + desc + "  signature:" + signature  + "  是否是静态方法：" + (Util.isStatic(access)));
        if (name == null || name.equals("") || name.equals(mConstructorName) || ((classAccess & Opcodes.ACC_INTERFACE) != 0)) {//过滤不需要插入代码的方法
            return mv;
        }
        System.out.println("真正的开始处理Class文件：" + className);
        final Type[] argTypes = Type.getArgumentTypes(desc);
        mv = new AopMethodVisitor(this.api, mv, argTypes, name, className, access);
        return mv;
    }


}