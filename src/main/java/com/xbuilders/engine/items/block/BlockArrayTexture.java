/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.block;

import com.xbuilders.window.utils.texture.TextureFile;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

/**
 * @author zipCoder933
 */
public class BlockArrayTexture {

    private final HashMap<String, Integer> textureMap;
    private final HashMap<String, File> fileMap;
    private final HashMap<String, Integer> animationMap;
    public final int layerCount;
    private final int textureSize;
    private TextureFile[] filePaths;

    private static final String fileExtensionCode = "\\.(?i)(jpg|jpeg|png|gif|bmp|ico|tiff)$";

    public int getTextureLayer(String name) {
        name = formatFilepath(name);
        Integer key = textureMap.get(name);

        if (key == null) {
            throw new NoSuchFieldError("Texture \"" + name + "\" not found!");
        }
        return key;
    }

    public int getAnimationLength(String name) {
        Integer value = animationMap.get(formatFilepath(name));
        return value == null ? 1 : value;
    }

    public File getTextureFile(String name) {
        return fileMap.get(formatFilepath(name));
    }

    private String formatFilepath(String path){
        return path.replaceAll("\\\\", "/").replaceAll(fileExtensionCode, "");
    }

    AtomicInteger index = new AtomicInteger();

    public BlockArrayTexture(File blockTexturesDir) throws IOException {
        textureMap = new HashMap<>();
        fileMap = new HashMap<>();
        animationMap = new HashMap<>();
        //load the textures
        File[] files = blockTexturesDir.listFiles();

        if (files.length == 0) {
            throw new IOException("No textures in directory");
        }

        BufferedImage img = ImageIO.read(files[0]);
        textureSize = img.getWidth();

        List<TextureFile> imageFiles = new ArrayList<>();
        index.set(1);
        indexDirectory(blockTexturesDir, files, imageFiles);
        filePaths = new TextureFile[imageFiles.size()];
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = imageFiles.get(i);
        }
        layerCount = filePaths.length;
        System.out.println("Loaded " + layerCount + " Block texture files. Size: " + textureSize + " x " + textureSize);
    }

    private void indexDirectory(File baseDir, File[] files, List<TextureFile> imageFiles) throws IOException {
        System.out.println("Indexing directory: " + files[0].getParentFile().getAbsolutePath());
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) indexDirectory(baseDir, files[i].listFiles(), imageFiles);
            else {
                String name = files[i].getName();
                if (name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".jpg")
                        || name.toLowerCase().endsWith(".jpeg")) {

                    name = files[i].getAbsolutePath().substring(baseDir.getAbsolutePath().length()+1);
                    name = formatFilepath(name);

//                    System.out.println("\t"+name+" ("+index.get()+")");
                    String path = files[i].getAbsolutePath();
                    textureMap.put(name, index.get());
                    fileMap.put(name, files[i]);

                    BufferedImage image = ImageIO.read(files[i]);
                    if (image.getWidth() < image.getHeight()) {//if the image is not square, split it up
                        int lengthMultiplier = image.getHeight() / image.getWidth();
                        for (int j = 0; j < lengthMultiplier; j++) {
                            imageFiles.add(new TextureFile(path, 0, j * image.getWidth(), image.getWidth(), image.getWidth()));
                            index.getAndAdd(1);
                        }
                        animationMap.put(name, lengthMultiplier);
                    } else {
                        imageFiles.add(new TextureFile(path));
                        index.getAndAdd(1);
                    }
                }
            }
        }
    }

    /**
     * @return the texture ID
     * @throws IOException
     */
    public int createArrayTexture() throws IOException {
        return (TextureUtils.makeTextureArray(textureSize, textureSize, false, filePaths).id);
    }
}
