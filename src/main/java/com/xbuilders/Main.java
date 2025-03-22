package com.xbuilders;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.utils.resource.ResourceLister;

import java.util.Collection;
import java.util.regex.Pattern;

public class Main {
    public static final String GAME_VERSION = "1.6.0";
    public static LocalClient localClient;

    public static void main(String[] args) {
        System.out.println(GAME_VERSION);
        Collection<String> strings = ResourceLister._listAllJarfileResources(Pattern.compile(".*\\Q\\xbuilders\\E(.*)"));
//        String[] strings = ResourceLister.listSubResources("\\");



        for (String s : strings) System.out.println(s);
        System.out.println("Strings: "+strings.size());

//        localClient = new LocalClient(args, GAME_VERSION);
    }
}
