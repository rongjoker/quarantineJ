package com.light.rain.example.test;

import org.objectweb.asm.*;

import java.lang.reflect.Member;
import java.util.Map;

public class ClassPrinter extends ClassVisitor {

    private final Class<?> clazz;

    private final Map<Member, String[]> memberMap;

    public ClassPrinter(Class<?> clazz, Map<Member, String[]> memberMap) {
        super(Opcodes.ASM6);
        this.clazz = clazz;
        this.memberMap = memberMap;
    }


    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
//        System.out.println(name + " extends " + superName + " {");
    }
    public void visitSource(String source, String debug) {
    }
    public void visitOuterClass(String owner, String name, String desc) {
    }
    public AnnotationVisitor visitAnnotation(String desc,
                                             boolean visible) {

        return null;
    }

    public void visitAttribute(Attribute attr) {
    }
    public void visitInnerClass(String name, String outerName,
                                String innerName, int access) {
    }
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
//        System.out.println(" visitField   " + desc + " " + name);
        return null;
    }

    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions){
        System.out.println("visitMethod: "+name+" "+signature+";desc: "+desc);
        MethodVisitorPrint methodVisitorPrint = new MethodVisitorPrint(clazz, memberMap, name, desc, isStatic(access));

        return methodVisitorPrint;
    }

    private static boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) > 0);
    }
    public void visitEnd() {
        System.out.println("}");
    } }
