package com.xbuilders.engine.utils.resource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public class ResourceLister {

    private static String[] resourceList;

    private static void init() {
        if (resourceList != null) return;
        Pattern pattern = Pattern.compile(".*\\Q\\classes\\E(.*)");
        final List<String> list = ResourceLister._listAllJarfileResources(pattern).stream().toList();
        resourceList = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            resourceList[i] = list.get(i).replaceFirst(".*\\Q\\classes\\E", "");
        }
    }

    private static String formatPath(String path) {
        path = path.replace("/", "\\");
        if (path.startsWith("\\")) path = path.substring(1);
        if (path.endsWith("\\")) path = path.substring(0, path.length() - 1);
        return path;
    }

    private static String[] listAllSubResources(String path) {
        init();
        //Get all matching regex patterns in resourceList
        path = formatPath(path);
        Pattern pattern = Pattern.compile(".*\\Q\\" + path + "\\E(.*)");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < resourceList.length; i++) {
            if (pattern.matcher(resourceList[i]).matches()) {
                list.add(resourceList[i]);
            }
        }
        return list.toArray(new String[0]);
    }

    public static String[] listDirectSubResources(String path) {
        init();
        //Get all matching regex patterns in resourceList
        path = formatPath(path);
        Pattern pattern = Pattern.compile(".*\\Q\\" + path + "\\E(.*)");

        //We only want unique values
        HashSet<String> list = new HashSet<>();
        for (int i = 0; i < resourceList.length; i++) {
            if (pattern.matcher(resourceList[i]).matches()) {
                String direct = resourceList[i];
                //System.out.println(direct);
                //Truncate the beginning of the path, so that only the direct descendant is left
                direct = direct.substring(path.length() + 1);
                //System.out.println("\t"+direct);

                //Truncate Anything after the first \, and remove the beginning \
                int end = direct.indexOf("\\", 1);
                if (end == -1) end = direct.length();
                direct = direct.substring(1, end);
                list.add(direct);
            }
        }
        return list.toArray(new String[0]);
    }

    public static String getDirectDescendant(String fullPath, String basePath) {
        if (fullPath.startsWith(basePath)) {
            String remainingPath = fullPath.substring(basePath.length());
            if (remainingPath.startsWith("\\") || remainingPath.startsWith("/")) {
                remainingPath = remainingPath.substring(1); // Remove leading separator
            }

            int separatorIndex = remainingPath.indexOf("\\");
            if (separatorIndex == -1) {
                separatorIndex = remainingPath.indexOf("/");
            }

            if (separatorIndex != -1) {
                return remainingPath.substring(0, separatorIndex);
            } else {
                return remainingPath; // Return the entire remaining part
            }
        }
        return null; // Return null if the full path doesn't start with the base path
    }

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> _listAllJarfileResources(
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            retval.addAll(_listAllJarfileResources(element, pattern));
        }
        return retval;
    }


    private static Collection<String> _listAllJarfileResources(
            final String element,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if (file.isDirectory()) {
            retval.addAll(_getResourcesFromDirectory(file, pattern));
        } else {
            retval.addAll(_getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> _getResourcesFromJarFile(
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

    private static Collection<String> _getResourcesFromDirectory(
            final File directory,
            final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(_getResourcesFromDirectory(file, pattern));
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


//    public static void main(final String[] args) {
//        System.out.println(
//                Arrays.toString(ResourceLister.listDirectSubResources("assets\\xbuilders\\models\\block")));
//    }
}