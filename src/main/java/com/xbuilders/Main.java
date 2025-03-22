package com.xbuilders;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.utils.resource.ResourceLister;
import com.xbuilders.engine.utils.resource.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Main {
    public static final String GAME_VERSION = "1.6.0";
    public static LocalClient localClient;

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(GAME_VERSION);
        ResourceLister.init();
        for (String assets : ResourceLister.listSubResources("/")) {
            System.out.println(assets);
        }

//        localClient = new LocalClient(args, GAME_VERSION);
    }

}
