package com.wangyuelin.easybug.aop;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 描述:修改字节码，插入日志代码
 *
 * @outhor wangyuelin
 * @create 2018-10-19 下午4:21
 */
public class AopMethodVisitor extends MethodVisitor implements Opcodes {
    private Type[] argTypes;     //方法参数
    private String name;         //方法名称
    private String className;    //类名称
    private int access;          //访问修饰


    public AopMethodVisitor(int i, MethodVisitor methodVisitor, Type[] argTypes, String name, String className, int access) {
        super(i, methodVisitor);
        this.argTypes = argTypes;
        this.name = name;
        this.className = className;
        this.access = access;

    }


    @Override
    public void visitCode() {
//        visitInsn()：访问一个0操作数的指令  visitLdcInsn：访问LDC命令：将一个常量加载到操作数栈  visitTypeInsn: 放问只有一个类类型作为参数的指令
        //ICONST_0：指令意思是将常量0push到操作数栈顶
        //ALOAD:指令用于将局部变量栈中的变量推动到操作栈上
        //ASTORE: 从操作站弹出并将其存储在局部变量栈



        /**
         * 实例方法局部变量表中的存储：
         * 0：this
         * 1：第一个方法参数
         * 2：第二个方法参数
         * ......
         *
         * 静态方法局部变量表中的存储：
         * 0：第一个方法参数
         * 1：第二个方法参数
         */

        int paramLength = argTypes == null ? 0 : argTypes.length;//方法的参数数
        //visitIntInsn 访问一个只有一个int操作数的指令

        /**
         * visitIntInsn:访问只有一个int类型的指令
         */
        mv.visitIntInsn(Opcodes.BIPUSH, paramLength);//将一个常量加载到操作数栈：bipush
        /**
         *visitTypeInsn:访问只有一个引用类型参数的指令
         */
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");//创建数组的指令ANEWARRAY
        /**
         * visitVarInsn: 访问局部变量的指令
         * 可以用来存或者取局部变量值
         * 第一个参数：将一个数值从操作数栈存储到局部变量表 ASTORE 第二个参数：the index of a local variable
         * 这里的索引为什么是paramLength，因为刚开始方法传进来的已经有paramLength个参数了，这些参数也得存在局部变量表
         *
         * ASTORE:将一个数值从操作数栈存储到局部变量表
         */
        int arrayIndexInLocalTable = Util.isStatic(access) ? (paramLength):(paramLength + 1);//数组在局部变量表中应该存的位置
        mv.visitVarInsn(Opcodes.ASTORE, arrayIndexInLocalTable);



        int i = 0; //将方法的参数填充到数组里面

        int indexFuncVarInLocalTable = Util.isStatic(access) ? 0 : 1;//静态方法和实例方法参数在局部变量表中的开始位置是不一样的
        for (Type tp : argTypes) {
            /**
             * ALOAD命令：将指定的引用类型本地变量推送至栈顶
             */
            mv.visitVarInsn(Opcodes.ALOAD, arrayIndexInLocalTable);

            /**
             * 将i的值压到栈顶，这里i的值表示数组中的位置
             */
            mv.visitIntInsn(Opcodes.BIPUSH, i);

            if (tp.equals(Type.BOOLEAN_TYPE)) {//布尔类型的参数
                mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);//这里的意思就是将i对应的局部变量加载到栈顶

                /**
                 * 调用系统的方法将基本的变量封装为引用变量
                 */
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if (tp.equals(Type.BYTE_TYPE)) {
                mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            } else if (tp.equals(Type.CHAR_TYPE)) {
                mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            } else if (tp.equals(Type.SHORT_TYPE)) {
                mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            } else if (tp.equals(Type.INT_TYPE)) {
                mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if (tp.equals(Type.LONG_TYPE)) {
                mv.visitVarInsn(Opcodes.LLOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                indexFuncVarInLocalTable++;//long和double的变量要占局部变量表两个位置
            } else if (tp.equals(Type.FLOAT_TYPE)) {
                mv.visitVarInsn(Opcodes.FLOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            } else if (tp.equals(Type.DOUBLE_TYPE)) {
                mv.visitVarInsn(Opcodes.DLOAD, indexFuncVarInLocalTable);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                indexFuncVarInLocalTable++;
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, indexFuncVarInLocalTable);//加载基本变量表中的值
            }

            /**
             * AASTORE:将栈顶引用型数值存入指定数组的指定索引位置
             * 数组和数组的位置之前已经压如操作数栈了
             */
            mv.visitInsn(Opcodes.AASTORE);

            i++;//下一个变量在数组中的位置
            indexFuncVarInLocalTable++;//下一个变量
        }



//        mv.visitInsn(Opcodes.AASTORE);//将栈顶引用型数值存入指定数组的指定索引位置

        /**
         * 访问LDC命令：将一个常量加载到操作数栈
         */
        this.visitLdcInsn(className);
        this.visitLdcInsn(name);

        /**
         * 将数组推送到栈顶
         */
        this.visitVarInsn(Opcodes.ALOAD, arrayIndexInLocalTable);

        this.visitMethodInsn(INVOKESTATIC,
                "com/wangyuelin/easybug/log/AopLog",
                "methodEnter",
                "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", false);

        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
//        if (opcode >= IRETURN && opcode <= RETURN)// 在返回之前安插after 代码。
//            this.visitMethodInsn(INVOKESTATIC, "com/aop/AopInteceptor", "after", "()V", false);
            super.visitInsn(opcode);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);//获取方法的参数名
    }
}