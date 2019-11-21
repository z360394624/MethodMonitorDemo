package com.lucius.tec.method.analysis.utils

import org.gradle.api.Project

/**
 * Format log
 *
 * @author qiudongchao
 */
class Logger {
    static org.gradle.api.logging.Logger logger

    static void make(Project project) {
        logger = project.getLogger()
    }

    static void i(String info) {
        if (null != info && null != logger) {
            logger.info("GRouter::Register >>> " + info)
        }
    }

    static void e(String error) {
        if (null != error && null != logger) {
            logger.error("GRouter::Register >>> " + error)
        }
    }

    static void w(String warning) {
        if (null != warning && null != logger) {
            logger.warn("GRouter::Register >> " + warning)
        }
    }
}
