package com.easybug.plugint;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

/**
 * 方法插入代码before、after、around，都可以通过AdviceAdapter实现
 */
public class MethodInsertAdapter extends AdviceAdapter {
    private ArrayList<String> params;//存方法对应的形参
    private String methodName;
    private Type[] paramTypes;//方法参数的类型
    private String methodSignature;//方法的签名


    protected MethodInsertAdapter(int api, MethodVisitor mv, int access, String name, String signature, String desc, String className) {
        super(api, mv, access, name, desc);
        params = new ArrayList<>();
        this.methodName = name;
        paramTypes = Type.getArgumentTypes(desc);
        this.methodSignature = signature;
        this.methodSignature = MethodUtil.getMethodSignature(className, name, paramTypes);
    }

    @Override
    protected void onMethodEnter() {
        mv.visitLdcInsn(methodSignature);//将方法的签名加载到操作数栈的栈顶
        int paramSize = paramTypes.length;
        if (paramSize == 0) {//没有参数
            mv.visitInsn(ACONST_NULL);//将null放到操作数栈顶
        } else {//有参数
            //1.构造存储参数的Object数组
            CodeHelper.newObjectArray(mv, paramSize);
            //2.将参数存到数组对应的位置
            int localVarIndex = CodeHelper.isStatic(methodAccess) ? 0 : 1;
            int typeIndex = 0;
            while (typeIndex < paramSize) {
                CodeHelper.putValue(mv, typeIndex, localVarIndex, paramTypes[typeIndex], (typeIndex + 1)  < paramSize);
                typeIndex++;
                localVarIndex++;
            }
        }
        //3.调用方法，将 Object数组/null 传递进去
        mv.visitMethodInsn(INVOKESTATIC, "com/wangyuelin/performance/MethodCall", "onStart", "(Ljava/lang/String;[Ljava/lang/Object;)V", false);
    }


    @Override
    protected void onMethodExit(int opcode) {


    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
        System.out.println("本地变量：" + name + " paramSize:" + paramTypes.length + " index:" + index);
    }


}
