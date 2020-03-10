package com.alibaba.android.arouter.register.utils

import com.alibaba.android.arouter.register.core.RegisterTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Scan all class in the package: com/alibaba/android/arouter/
 * find out all routers,interceptors and providers
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/20 11:48
 */
class ScanUtil {

    /**
     * scan jar file
     * @param jarFile All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    static void scanJar(File jarFile, File destFile) {
        if (jarFile) {
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    Logger.i('ROUTER_CLASS_PACKAGE_NAME: ' + entryName)
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                } else if(entryName.startsWith(ScanSetting.DAGGER_MODULE_CLASS_PACKAGE_NAME)){//lonesword dagger
                    Logger.i('DAGGER_MODULE_CLASS_PACKAGE_NAME: ' + entryName)
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanDaggerClass(inputStream)
                    inputStream.close()
                } else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    Logger.i('GENERATE_TO_CLASS_FILE_NAME: ' + entryName)
                    // mark this jar file contains LogisticsCenter.class
                    // After the scan is complete, we will generate register code into this file
                    RegisterTransform.fileContainsInitClass = destFile
                } else if (ScanSetting.GENERATE_TO_DAGGER_CLASS_FILE_NAME == entryName) {
                    Logger.i('GENERATE_TO_DAGGER_CLASS_FILE_NAME: ' + entryName)
                    RegisterTransform.daggerFileContainsInitClass = destFile//需要修改的文件
                }
            }
            file.close()
        }
    }

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null && entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    static boolean shouldDaggerProcessClass(String entryName) {
        return entryName != null && entryName.startsWith(ScanSetting.DAGGER_MODULE_CLASS_PACKAGE_NAME)
    }

    /**
     * scan class file
     * @param class file
     */
    static void scanClass(File file) {
        scanClass(new FileInputStream(file))
    }

    /**
     * scan class file
     * @param class file
     */
    static void scanDaggerClass(File file) {
        scanDaggerClass(new FileInputStream(file))
    }

    static void scanDaggerClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanDaggerClassVisitor cv = new ScanDaggerClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            RegisterTransform.registerList.each { ext ->
                if (ext.interfaceName && interfaces != null) {
                    interfaces.each { itName ->
                        if (itName == ext.interfaceName) {
                            ext.classList.add(name)
                        }
                    }
                }
            }
        }
    }


    static class ScanDaggerClassVisitor extends ClassVisitor {

        ScanDaggerClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            RegisterTransform.daggerRegisterList.each { ext ->

                Logger.i('dagger 列表1: ' + ext)
                if (ext.interfaceName && interfaces != null) {
                    Logger.i('dagger 列表2: ' + ext.interfaceName)
                    interfaces.each { itName ->
                        Logger.i('dagger 列表3: ' + itName)
                        if (itName == ext.interfaceName) {
                            ext.daggerClassList.add(name)
                        }
                    }
                }
            }
        }
    }
}