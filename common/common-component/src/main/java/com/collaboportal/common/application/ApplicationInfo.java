
package com.collaboportal.common.application;

import io.micrometer.common.util.StringUtils;

public class ApplicationInfo {

    /**
     * 应用前缀
     */
    public static String routePrefix;

    /**
     * 为指定 path 裁剪掉 routePrefix 前缀
     * @param path 指定 path
     * @return /
     */
    public static String cutPathPrefix(String path) {
        if(! StringUtils.isEmpty(routePrefix) && ! routePrefix.equals("/") && path.startsWith(routePrefix)){
            path = path.substring(routePrefix.length());
        }
        return path;
    }

}