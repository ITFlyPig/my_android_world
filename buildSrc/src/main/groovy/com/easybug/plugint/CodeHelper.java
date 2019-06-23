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
    public static void putValue(MethodVisitor mv, int arrayIndex, int localVarIndex, Type localVarType) {
        if (mv == null) {
            return;
        }
        mv.visitIntInsn(Opcodes.BIPUSH, arrayIndex);//将数组的索引压入操作数栈
        mv.visitVarInsn(Opcodes.ALOAD, localVarIndex);//获取本地变量表索引位置的值
        toReference(mv, localVarType);//确保取出的值是引用类型
        mv.visitInsn(Opcodes.AASTORE);//将值存入数组对应位置
        mv.visitInsn(Opcodes.DUP);
    }

    /**
     * 将基本类型转为引用类型,相当于Xxx.valueof()
     *
     * @param type
     */
    public static void toReference(MethodVisitor mv, Type type) {
        if (type.equals(Type.BOOLEAN_TYPE)) {//布尔类型的参数
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
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
