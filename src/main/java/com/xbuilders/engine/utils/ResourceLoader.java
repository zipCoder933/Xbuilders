package com.xbuilders.engine.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {
    public ResourceLoader() {
    }

    /**
     * If a path starts with a slash (leading slash) it is an absolute path, otherwise it is a relative path
     * All paths are separated by a forward slash
     */
    public static final String FILE_SEPARATOR = "/";

    private String formatPath(String path) {
        if (!path.startsWith(FILE_SEPARATOR)) path = FILE_SEPARATOR + path;
        return path.replace("\\", FILE_SEPARATOR);
    }


    public List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        path = formatPath(path);
        System.out.println("Resource path: " + path);
        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                resource = path + FILE_SEPARATOR + resource;
                filenames.add(resource);
                System.out.println("\tResource: " + resource);
            }
        }
        System.out.println("Found " + filenames.size() + " resources.");

        return filenames;
    }

    public InputStream getResourceAsStream(String path) {
        path = formatPath(path);
        final InputStream in
                = getContextClassLoader().getResourceAsStream(path);

        return in == null ? getClass().getResourceAsStream(path) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    public byte[] readResource(String path) throws IOException {
        InputStream is = getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + path);
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }


    /*



    //Overloading methods with other parameters
     */
    public byte[] readResource(String path1, String path2) throws IOException {
        path1 = formatPath(path1 + FILE_SEPARATOR + path2);
        return readResource(path1);
    }

    public InputStream getResourceAsStream(String path1, String path2) {
        path1 = formatPath(path1 + FILE_SEPARATOR + path2);
        return getResourceAsStream(path1);
    }

    public List<String> getResourceFiles(String path1, String path2) throws IOException {
        path1 = formatPath(path1 + FILE_SEPARATOR + path2);
        return getResourceFiles(path1);
    }
}
