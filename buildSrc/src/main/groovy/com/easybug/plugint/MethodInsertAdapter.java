package com.easybug.plugint;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

/**
 * 方法插入代码before、after、around，都可以通过AdviceAdapter实现
 */
public class MethodInsertAdapter extends AdviceAdapter {
    private ArrayList<String> params;//存方法对应的形参
    private String name;


    /**
     * Creates a new {@link AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link Type Type}).
     */
    protected MethodInsertAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
        params = new ArrayList<>();
        this.name = name;
    }

    /**
     * 开始进入方法
     */
    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        System.out.println("onMethodEnter：" + name);

    }

    /**
     * 将要退出方法
     * @param opcode
     */
    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        System.out.println("onMethodExit：" + name);

    }

    /**
     * 访问本地变量，临时存下，onMethodEnter使用
     * @param name
     * @param desc
     * @param signature
     * @param start
     * @param end
     * @param index
     */
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
        final Type[] argTypes = Type.getArgumentTypes(desc);
        System.out.println("参数的个数：" + argTypes.length + " 参数：" + name + " desc:" + desc + " signature:" + signature);
    }

    /**
     * 处理方法的注解
     * @param desc
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}
