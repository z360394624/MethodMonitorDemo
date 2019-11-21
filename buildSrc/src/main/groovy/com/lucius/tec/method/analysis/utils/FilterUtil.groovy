package com.lucius.tec.method.analysis.utils

import com.lucius.tec.method.analysis.core.Config


class FilterUtil {

    static final String DEFAULT_PACKAGE_NAME = 'com/integer/tec/'

    static {
        Config.TARGET_LIST.add("com.lucius.tec")
    }

    static boolean shouldProcessPreDexJar(String path) {
        for (String packageName in Config.FILTER_LIST) {
            if (packageName != null && path.contains(packageName)) {
                return false
            }
        }
        return !path.contains("com.android.support") \
            && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        println "entryName = $entryName, constrain = ${entryName.contains("/kotlin-classes")}"
        if (entryName == null
                || !entryName.endsWith(".class") \
                || entryName.contains("R\$") \
                || entryName.contains("R.class") \
                || entryName.contains(Config.CLASS_NAME)
                || entryName.contains("BuildConfig.class")) {
            return false
        }
        String currentEntryName = entryName.replaceAll("/", ".")
        for (String packageName in Config.TARGET_LIST) {
            if (packageName != null && currentEntryName.contains(packageName)) {
                return true
            }
        }
        return entryName != null \
            && entryName.startsWith(DEFAULT_PACKAGE_NAME)
    }

}