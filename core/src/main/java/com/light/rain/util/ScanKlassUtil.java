package com.light.rain.util;

import java.util.Set;

public class ScanKlassUtil {

    private static final char PACKAGE_SEPARATOR = '.';

    private static final char PATH_SEPARATOR = '/';

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";


    public static String convertClassNameToResourcePath(String className) {
        return className.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }


    private Set<Class> scanCandidateComponents(String basePackage){


//        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
//                resolveBasePackage(basePackage) + '/' + this.resourcePattern;
//        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
//



        return null;

    }


}
