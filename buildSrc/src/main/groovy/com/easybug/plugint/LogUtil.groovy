package com.easybug.plugint

import org.gradle.api.Project
class LogUtil {
    private static Project project

    /**
     * 初始化
     * @param pro
     */
    public static void init(Project pro) {
        project = pro
    }

    public static void d(String msg) {
        if (project != null) {
            project.logger.debug(msg)
        }
    }

    public static void e(String msg) {
        if (project != null) {
            project.logger.error(msg)
        }
    }
}