package com.lucius.tec.method.analysis.core

import com.lucius.tec.method.analysis.utils.FilterUtil
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AnalyzerAdapter
import org.objectweb.asm.commons.LocalVariablesSorter
import org.objectweb.asm.Type

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class MethodMonitorInjector {

    /**
     * 创建**.jar.opt文件，然后插入代码，删除原jar，重命名**.jar.opt为**.jar
     * @param jarFile
     * @return
     */
    static File insertCodeIntoJarFile(File jarFile) {
        if (jarFile) {
            def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                def bytes
                if (FilterUtil.shouldProcessClass(entryName)) {
                    bytes = insertCodeInsn(inputStream)
                } else {
                    bytes = IOUtils.toByteArray(inputStream)
                }
                jarOutputStream.write(bytes)
                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)
        }
        return jarFile
    }

    static File insertCodeIntoClassFile(File classFile) {
        if (classFile) {
            def optClass = new File(classFile.getParent(), classFile.name + ".opt")
            if (optClass.exists())
                optClass.delete()
            def bytes = insertCodeInsn(new FileInputStream(classFile))
            FileOutputStream optClassStream = new FileOutputStream(optClass)
            optClassStream.write(bytes)
            if (classFile.exists()) {
                classFile.delete()
            }
            optClass.renameTo(classFile)
        }
        return classFile
    }

    //refer hack class when object init
    static byte[] insertCodeInsn(InputStream inputStream) {
        ClassReader classReader = new ClassReader(inputStream)
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new AddMethodMonitor(Opcodes.ASM5, classWriter)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }


    static class AddMethodMonitor extends ClassVisitor {

        private String className;

        AddMethodMonitor(int api, ClassVisitor cv) {
            //Responsechain 的下一个 ClassVisitor，这里我们将传入 ClassWriter，
            // 负责改写后代码的输出
            super(api, cv);
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        // 重写 visitMethod，访问到 "operation" 方法时，
        // 给出自定义 MethodVisitor，实际改写方法内容
        MethodVisitor visitMethod(final int access, final String name,
                                  final String desc, final String signature, final String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null) {
                // 使用自定义 MethodVisitor，实际改写方法内容
                AddMonitorMethodAdapter wrappedMv = new AddMonitorMethodAdapter(className + File.separator + name, Opcodes.ASM5, mv);
                wrappedMv.analyzerAdapter = new AnalyzerAdapter(className, access, name, desc, wrappedMv);
                wrappedMv.localVariablesSorter = new LocalVariablesSorter(access, desc, wrappedMv.analyzerAdapter);
                return wrappedMv;
            }
            return mv;
        }

    }

    static class AddMonitorMethodAdapter extends MethodVisitor {

        private int startTimeIndex

        private String methodName

        public LocalVariablesSorter localVariablesSorter;
        public AnalyzerAdapter analyzerAdapter;

        AddMonitorMethodAdapter(String methodName, int api, MethodVisitor mv) {
            super(api, mv);
            this.methodName = methodName
        }

        /**
         * visitVarInsn方法第一个参数是指令，第二个参数是局部变量表中的地址
         */
        void visitCode() {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            startTimeIndex = localVariablesSorter.newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(Opcodes.LSTORE, startTimeIndex);
            super.visitCode()
        }

        @Override
        void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                mv.visitLdcInsn(methodName);
                mv.visitVarInsn(Opcodes.LLOAD, startTimeIndex);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Config.CLASS_NAME, Config.METHOD_NAME, "(Ljava/lang/String;J)V", false);
            }
            super.visitInsn(opcode);
        }
    }

}