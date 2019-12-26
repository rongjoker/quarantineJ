package com.light.rain.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * 类读取
 */
public class SimpleClassVisitor extends ClassVisitor {

    private final Class<?> clazz;

    private final Map<Member, String[]> memberMap;

    public SimpleClassVisitor(Class<?> clazz, Map<Member, String[]> memberMap) {
        super(Opcodes.ASM6);
        this.clazz = clazz;
        this.memberMap = memberMap;
    }

    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions){
        SimpleMethodVisitor simpleMethodVisitor = new SimpleMethodVisitor(clazz, memberMap, name, desc, isStatic(access));

        return simpleMethodVisitor;
    }

    private static boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) > 0);
    }
}
