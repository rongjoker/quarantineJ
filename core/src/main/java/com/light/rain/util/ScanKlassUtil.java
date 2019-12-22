package com.light.rain.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

@Log4j2
public class ScanKlassUtil {

    public static void main(String[] args) throws IOException {
        ScanKlassUtil scanKlassUtil = new ScanKlassUtil();
        scanKlassUtil.scanCandidateComponents("com.light.rain.util");

    }
    private static final char PACKAGE_SEPARATOR = '.';

    private static final char PATH_SEPARATOR = '/';

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    private final String DEFAULT_RESOURCE_PATTERN = "**/*.class";


    public static String resolveBasePackage(String basePackage) {
        return basePackage.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);
    }

    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = this.getClass().getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            log.info("url:[{}]",url);
            result.add(convertClassLoaderURL(url));

        }

        return result;
    }


    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {

        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = new LinkedHashSet<>(matchingFiles.size());
        for (File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }



    private Set<Class> scanCandidateComponents(String basePackage){

        String rootDirPath = CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage);


        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
//        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);

        try {
            String locationPattern = rootDirPath.substring("classpath*:".length());
            log.info("locationPattern:[{}]",locationPattern);
            Set<Resource> root = this.doFindAllClassPathResources(locationPattern);

            root.forEach(resource -> {

                try {
                    log.info("resource.getFile:[{}]",resource.getFile());
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }


            });


        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }


}
