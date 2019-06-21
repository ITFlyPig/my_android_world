package com.easybug.plugint;

/**
 * 定义字节处理的接口，字节处理可以交给ASM、Javassist等实现
 */
public interface IClassHandle {

    /**
     * 是否需要处理这个class文件
     * @param signature
     * @return
     */
    public boolean isNeedHanle(String signature);

    /**
     * 插入代码
     */
   public byte[] insertCode();



}
