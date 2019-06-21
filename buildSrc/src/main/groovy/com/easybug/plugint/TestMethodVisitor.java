package com.easybug.plugint;

import com.easybug.plugint.LogUtil;

import org.objectweb.asm.MethodVisitor;

public class TestMethodVisitor extends MethodVisitor {

    public TestMethodVisitor(int api) {
        super(api);
    }

    public TestMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }
}
