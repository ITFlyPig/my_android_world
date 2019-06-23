package com.easybug.plugint;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class CodeHelper {
    /**
     * new Object数组
     *
     * @param mv
     * @param size 数组的大小
     */
    public static void newObjectArray(MethodVisitor mv, int size) {
        if (mv == null) {
            return;
        }
        mv.visitIntInsn(Opcodes.BIPUSH, size);//将数组的大小压如操作数栈
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");//new 出Object数组
        mv.visitInsn(Opcodes.DUP);
    }

    /**
     * 为数组的索引index赋值
     *
     * @param mv
     */
    public static void putValue(MethodVisitor mv, int arrayIndex, int localVarIndex, Type localVarType, boolean isNeedDup) {
        if (mv == null) {
            return;
        }
        mv.visitIntInsn(Opcodes.BIPUSH, arrayIndex);//将数组的索引压入操作数栈
        loadParam(mv, localVarType, localVarIndex);
        mv.visitInsn(Opcodes.AASTORE);//将值存入数组对应位置
        if (isNeedDup)
        mv.visitInsn(Opcodes.DUP);
    }

    /**
     * 对于不同类型的局部变量表中的数据，调用对应的命令加载
     * @param mv
     * @param tp
     * @param indexFuncVarInLocalTable
     */
    public static void loadParam(MethodVisitor mv, Type tp, int indexFuncVarInLocalTable) {
        if (tp.equals(Type.BOOLEAN_TYPE)) {//布尔类型的参数
            mv.visitVarInsn(Opcodes.ILOAD, indexFuncVarInLocalTable);//这里的意思就是将i对应的局部变量加载到栈顶
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
        } else if (tp.equals(Type.FLOAT_TYPE)) {
            mv.visitVarInsn(Opcodes.FLOAD, indexFuncVarInLocalTable);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (tp.equals(Type.DOUBLE_TYPE)) {
            mv.visitVarInsn(Opcodes.DLOAD, indexFuncVarInLocalTable);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, indexFuncVarInLocalTable);//加载基本变量表中的值
        }

    }

    /**
     * 判断访问标志是否有静态标志
     * @param access
     * @return
     */
    public static boolean isStatic(int access) {
        //STATIC 0x0008	 修饰 方法和字段
        return  ((access & Opcodes.ACC_STATIC) != 0);

    }

}
