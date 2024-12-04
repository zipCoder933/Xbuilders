/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.block;

import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureFile;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

import static com.xbuilders.engine.rendering.chunk.meshers.bufferSet.vertexSet.CompactVertexSet.MAX_BLOCK_ANIMATION_LENGTH;

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
    final Texture texture;


    public Texture getTexture() {
        return texture;
    }

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

    public static String formatFilepath(String path) {
        return path.replaceAll("\\\\", "/").replaceAll(fileExtensionCode, "");
    }

    AtomicInteger index = new AtomicInteger();

    public BlockArrayTexture(File blockTexturesDir, File builtinBlockTexturesDir) throws IOException {
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

        /**
         * Image files is the list that is given directly to the texture array
         * All other files are just for finding the texture
         * The index tells us where in the array the texture is
         */
        //Add some default textures first
        BuiltinTextures.addBuiltinTextures(builtinBlockTexturesDir, imageFiles, index, textureSize);

        addTexturesFromDir(blockTexturesDir, files, imageFiles);
        filePaths = new TextureFile[imageFiles.size()];
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = imageFiles.get(i);
        }
        layerCount = filePaths.length;
        System.out.println("Block Texture loaded; Layers: " + layerCount + "; Size: " + textureSize + " x " + textureSize);
        this.texture = TextureUtils.makeTextureArray(textureSize, textureSize, false, filePaths);
    }


    public int createNewArrayTexture() throws IOException {
        return TextureUtils.makeTextureArray(textureSize, textureSize, false, filePaths).id;
    }

    private void addTexturesFromDir(File baseDir,
                                    File[] files, List<TextureFile> imageFiles) throws IOException {

        //Search through the files
        for (int i = 0; i < files.length; i++) {
            //Recursively search through directories
            if (files[i].isDirectory()) addTexturesFromDir(baseDir,
                    files[i].listFiles(), imageFiles);
            else {
                String name = files[i].getName();
                if (name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".jpg")
                        || name.toLowerCase().endsWith(".jpeg")) {

                    name = files[i].getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
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
                        if (lengthMultiplier > MAX_BLOCK_ANIMATION_LENGTH)
                            lengthMultiplier = MAX_BLOCK_ANIMATION_LENGTH;
//                        System.out.println("Splitting " + name + " into " + lengthMultiplier + " pieces");
                        animationMap.put(name, lengthMultiplier);
                    } else {
                        imageFiles.add(new TextureFile(path));
                        index.getAndAdd(1);
                    }
                }
            }
        }
    }


}
