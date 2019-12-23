package com.light.rain.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Log4j2
public class ScanKlassUtil {

    public static void main(String[] args) throws IOException {
        ScanKlassUtil scanKlassUtil = new ScanKlassUtil();
        scanKlassUtil.scanIOC("com.light.rain.example",new HashMap<>());

    }
    private static final char PACKAGE_SEPARATOR = '.';

    private static final char PATH_SEPARATOR = '/';

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    private final String DEFAULT_CLASS_PATTERN = ".class";


    public static String resolveBasePackage(String basePackage) {
        return basePackage.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }


    public static String resolveRelativePath(String basePackage) {
        return basePackage.replace( PATH_SEPARATOR,PACKAGE_SEPARATOR);
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

            try {
                scanResourceList(result,convertClassLoaderURL(url).getFile(),DEFAULT_CLASS_PATTERN);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        return result;
    }


    protected Set<Resource> scanResourceList(Set<Resource> resources, File file,String subPattern){

        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File subFile : files) {
                scanResourceList(resources,subFile,subPattern);
            }
        }else {
            if(file.getName().endsWith(subPattern)){
                try {
                    resources.add(new UrlResource(file.toURI()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

        }

        return resources;


    }



public Set<Class> convertFile2Class(Set<Resource> result,String relativePath){

    Set<Class> classLinkedHashSet = new LinkedHashSet<>();

    result.forEach(resource -> {

        String filename = resource.getFilename();
        String substring = resolveRelativePath(filename.substring(filename.indexOf(relativePath)));
        String className = substring.substring(0, substring.indexOf(DEFAULT_CLASS_PATTERN));
//        log.info("resource:[{}];className:[{}]",substring,className);


        ClassLoader clToUse = getClass().getClassLoader();

        try {
            Class<?> aClass = clToUse.loadClass(className);
            classLinkedHashSet.add(aClass);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    });

    return classLinkedHashSet;

}


    private Set<Class> scanCandidateComponents(String basePackage){

        String relativePath = resolveBasePackage(basePackage);

        String rootDirPath = CLASSPATH_ALL_URL_PREFIX +relativePath;

        Set<Resource> result = new LinkedHashSet<>();

        try {
            String locationPattern = rootDirPath.substring("classpath*:".length());
            log.info("locationPattern:[{}]",locationPattern);
            result = this.doFindAllClassPathResources(locationPattern);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertFile2Class(result,relativePath);

    }

    /**
     * 检查附带Singleton注解的类
     * @param c
     * @return
     */
    public boolean checkIOCBean(Class c){

        Annotation[] annotations = c.getAnnotations();

        for (Annotation annotation : annotations) {
            if(annotation instanceof  com.google.inject.Singleton) return true;
        }

        return false;
    }


    /**
     * 扫描ioc
     * @param basePackage
     * @return
     */
    public Map<Class,Class> scanIOC(String basePackage,Map<Class,Class> kvs){

        Set<Class> classes = this.scanCandidateComponents(basePackage);

        classes.forEach(klass->{

            if(checkIOCBean(klass)){
                Class[] interfaces = klass.getInterfaces();
                if(null!=interfaces && interfaces.length>0){
                    for (Class anInterface : interfaces) {
                        kvs.put(anInterface,klass);
                    }
                }else {
                    kvs.put(klass,klass);
                }
            }
        });

        return kvs;

    }








}
