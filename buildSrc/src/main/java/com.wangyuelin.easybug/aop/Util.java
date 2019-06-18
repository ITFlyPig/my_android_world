package com.wangyuelin.easybug.aop;

import org.objectweb.asm.Opcodes;

public class Util {

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
