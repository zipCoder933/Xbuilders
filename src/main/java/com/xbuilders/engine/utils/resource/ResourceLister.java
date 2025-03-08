package com.xbuilders.engine.utils.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public class ResourceLister {

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> listAllJarfileResources(
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            retval.addAll(listAllJarfileResources(element, pattern));
        }
        return retval;
    }

    public static List<String> listFilesInResource(String path) {
        //We dont need pattern.quote because Q and E already quote it

        //Format the path and remove trailing or leading slashes
        path = path.replace("/", "\\");
        if (path.startsWith("\\")) path = path.substring(1);
        if (path.endsWith("\\")) path = path.substring(0, path.length() - 1);

        Pattern pattern = Pattern.compile(".*\\Q\\classes\\" + path + "\\E(.*)");

        final Collection<String> list = ResourceLister.listAllJarfileResources(pattern);
        return list.stream().toList();
    }

    //---------

    private static Collection<String> listAllJarfileResources(
            final String element,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(
            final File file,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
                retval.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        retval.add(fileName);
                    }
                } catch (final IOException e) {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }


    /**
     * list the resources that match args[0]
     *
     * @param args
     *            args[0] is the pattern to match, or list all resources if
     *            there are no args
     */
    public static void main(final String[] args) {
//        Pattern pattern = Pattern.compile(".*");
//        for (final String name : ResourceLister.listAllJarfileResources(pattern)) {
//            System.out.println(name);
//        }
        for (final String name : ResourceLister.listFilesInResource("/assets/xbuilders/")) {
            System.out.println(name);
        }
    }
}