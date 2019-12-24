package com.light.rain.example.test;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.*;

import java.lang.reflect.Member;
import java.util.Map;

@Log4j2
public class MethodVisitorPrint extends MethodVisitor {

    private final Class<?> clazz;

    private final Map<Member, String[]> memberMap;

    private final String name;

    private final Type[] args;

    private final String[] parameterNames;

    private final boolean isStatic;

    private boolean hasLvtInfo = false;

    /*
     * The nth entry contains the slot index of the LVT table entry holding the
     * argument name for the nth parameter.
     */
    private final int[] lvtSlotIndex;

    public MethodVisitorPrint(Class<?> clazz, Map<Member, String[]> map, String name, String desc, boolean isStatic) {
        super(Opcodes.ASM6);
        this.clazz = clazz;
        this.memberMap = map;
        this.name = name;
        this.args = Type.getArgumentTypes(desc);
        this.parameterNames = new String[this.args.length];
        this.isStatic = isStatic;
        this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
    }


    private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
        int[] lvtIndex = new int[paramTypes.length];
        int nextIndex = (isStatic ? 0 : 1);
        for (int i = 0; i < paramTypes.length; i++) {
            lvtIndex[i] = nextIndex;
            if (isWideType(paramTypes[i])) {
                nextIndex += 2;
            }
            else {
                nextIndex++;
            }
        }
        return lvtIndex;
    }

    private static boolean isWideType(Type aType) {
        // float is not a wide type
        return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.hasLvtInfo = true;
        for (int i = 0; i < this.lvtSlotIndex.length; i++) {
            if (this.lvtSlotIndex[i] == index) {
                this.parameterNames[i] = name;
            }
        }
    }

    @Override
    public void visitEnd() {
        if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
            // visitLocalVariable will never be called for static no args methods
            // which doesn't use any local variables.
            // This means that hasLvtInfo could be false for that kind of methods
            // even if the class has local variable info.
            Member member = resolveMember();
            if(null!=member)
            this.memberMap.put(member, this.parameterNames);
        }
    }

    private static final String CONSTRUCTOR = "<init>";
    private static final String CLOSESTRUCTOR = "<clinit>";

    private Member resolveMember() {
        ClassLoader loader = this.clazz.getClassLoader();
        Class<?>[] argTypes = new Class<?>[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            try {
                argTypes[i] = loader.loadClass(this.args[i].getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
//            argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
        }
        try {
            if (CONSTRUCTOR.equals(this.name) || CLOSESTRUCTOR.equals(this.name)) {
                return null;//构造方法暂时不使用
//                return this.clazz.getDeclaredConstructor(argTypes);
            }
            return this.clazz.getDeclaredMethod(this.name, argTypes);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Method [" +this.clazz.getName()+"#"+ this.name +
                    "] was discovered in the .class file but cannot be resolved in the class object", ex);
        }
    }


}
