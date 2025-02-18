package com.xbuilders.engine.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {
    public ResourceLoader() {
    }

    private static final String FILE_SEPARATOR = "/";

    public List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
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

    public InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
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
}
