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
import javax.imageio.ImageIO;

/**
 * @author zipCoder933
 */
public class BlockTextureArray {

    private final HashMap<String, Integer> textureMap;
    private final HashMap<String, File> fileMap;
    private final HashMap<String, Integer> animationMap;
    public final int layerCount;
    private final int textureSize;
    private TextureFile[] filePaths;

    private static final String fileExtensionCode = "\\.(?i)(jpg|jpeg|png|gif|bmp|ico|tiff)$";

    public int getTextureLayer(String name) {
        Integer key = textureMap.get(name.replaceAll(fileExtensionCode, ""));

        if (key == null) {
            throw new NoSuchFieldError("Texture \"" + name + "\" not found!");
        }
        return key;
    }

    public int getAnimationLength(String name) {
        Integer value = animationMap.get(name.replaceAll(fileExtensionCode, ""));
        return value == null ? 1 : value;
    }

    public File getTextureFile(String name) {
        return fileMap.get(name.replaceAll(fileExtensionCode, ""));
    }

    public BlockTextureArray(File blockTexturesDir) throws IOException {
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
        int index = 1;
        List<TextureFile> imageFiles = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            String path = files[i].getAbsolutePath();

            if (name.toLowerCase().endsWith(".png")
                    || name.toLowerCase().endsWith(".jpg")
                    || name.toLowerCase().endsWith(".jpeg")) {
                name = name.replaceAll(fileExtensionCode, "");

                textureMap.put(name, index);
                fileMap.put(name, files[i]);

                BufferedImage image = ImageIO.read(files[i]);
                if (image.getWidth() < image.getHeight()) {//if the image is not square, split it up
                    int lengthMultiplier = image.getHeight() / image.getWidth();
                    for (int j = 0; j < lengthMultiplier; j++) {
                        imageFiles.add(new TextureFile(path, 0, j * image.getWidth(), image.getWidth(), image.getWidth()));
                        index++;
                    }
                    animationMap.put(name, lengthMultiplier);
                } else {
                    imageFiles.add(new TextureFile(path));
                    index++;
                }
            }
        }
        filePaths = new TextureFile[imageFiles.size()];
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = imageFiles.get(i);
        }
        layerCount = filePaths.length;
        System.out.println("Loaded " + layerCount + " Block texture files. Size: " + textureSize + " x " + textureSize);
    }

    /**
     * @return the texture ID
     * @throws IOException
     */
    public int createArrayTexture() throws IOException {
        return (TextureUtils.makeTextureArray(textureSize, textureSize, false, filePaths).id);
    }
}
