package com.xbuilders.engine;

import com.xbuilders.engine.utils.JarResourceLister;

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
public class ResourceList {


    /**
     * list the resources that match args[0]
     *
     * @param args args[0] is the pattern to match, or list all resources if
     *             there are no args
     */
    public static void main(final String[] args) {
        final List<String> list = JarResourceLister.listAllFiles("\\assets");
        for (final String name : list) {
            System.out.println(name);
        }
    }
}  