package com.lucius.tec.method.analysis.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.lucius.tec.method.analysis.utils.FilterUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project


class ScannerTransform extends Transform {

    Project project

    ScannerTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "ScannerInjector"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(
            Context context,
            Collection<TransformInput> inputs,
            Collection<TransformInput> referencedInputs,
            TransformOutputProvider outputProvider,
            boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        /** init config value */
        if (project.methodMonitorExt.filterList != null) {
            Config.FILTER_LIST.addAll(project.methodMonitorExt.filterList)
        }
        if (project.methodMonitorExt.targetList != null) {
            Config.TARGET_LIST.addAll(project.methodMonitorExt.targetList)
        }
        if (project.methodMonitorExt.className != null) {
            Config.CLASS_NAME = project.methodMonitorExt.className
        }
        if (project.methodMonitorExt.methodName != null) {
            Config.METHOD_NAME = project.methodMonitorExt.methodName
        }
        outputProvider.deleteAll();

        long startTime = System.currentTimeMillis()
        boolean leftSlash = File.separator == '/'
        inputs.each {
            TransformInput input ->
                input.jarInputs.each {
                    JarInput jarInput ->
                        String destName = jarInput.name
                        // rename jar files
                        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                        if (destName.endsWith(".jar")) {
                            destName = destName.substring(0, destName.length() - 4)
                        }
                        // input file
                        File src = jarInput.file
                        // output file
                        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                        //inject code to find jar
                        if (FilterUtil.shouldProcessPreDexJar(src.absolutePath)) {
                            println "MethodMonitorInjector jar = ${MethodMonitorInjector.class.getName()}"
                            MethodMonitorInjector.insertCodeIntoJarFile(src)
                        }
                        FileUtils.copyFile(src, dest)
                }
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                        String root = directoryInput.file.absolutePath
                        if (!root.endsWith(File.separator)) {
                            root += File.separator
                        }
                        directoryInput.file.eachFileRecurse {
                            File file ->
                                def path = file.absolutePath.replace(root, '')
                                if (!leftSlash) {
                                    path = path.replaceAll("\\\\", "/")
                                }

                                if (file.isFile() && FilterUtil.shouldProcessClass(path)) {
                                    System.out.println("src = ${file}")
                                    MethodMonitorInjector.insertCodeIntoClassFile(file);
                                }
                        }
                        // copy to dest
                        FileUtils.copyDirectory(directoryInput.file, dest)
                }
        }
    }
}