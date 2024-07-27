package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class CopyTool extends BlockTool {
    public CopyTool(BlockTools tools, CursorRay cursorRay) {
        super("Copy", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\copy.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        //Only activate with Ctrl+C
        if (key == GLFW.GLFW_KEY_C && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return false;
    }

    @Override
    public void activate() {
        cursorRay.enableBoundaryMode((aabb, aBoolean) -> blockBoundarySetEvent(aabb, aBoolean));
        cursorRay.boundary_lockToPlane = false;
    }


    private void blockBoundarySetEvent(AABB aabb, boolean created) {
        PasteTool.clipboard = new ChunkVoxels(
                (int) (aabb.getXLength()),
                (int) (aabb.getYLength()),
                (int) (aabb.getZLength()));

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    PasteTool.clipboard.setBlock(
                            (int) (x - aabb.min.x),
                            (int) (y - aabb.min.y),
                            (int) (z - aabb.min.z),
                            GameScene.world.getBlockID(x, y, z));
                    //Set block data
                    if (GameScene.world.getBlockData(x, y, z) != null) {
                        PasteTool.clipboard.setBlockData(
                                (int) (x - aabb.min.x),
                                (int) (y - aabb.min.y),
                                (int) (z - aabb.min.z),
                                GameScene.world.getBlockData(x, y, z));
                    }
                }
            }
        }
        PasteTool.updateMesh();
    }


    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
//        if (action == GLFW.GLFW_RELEASE) {
//            if (key == GLFW.GLFW_KEY_S) {
//                saveClipboard();
//                return true;
//            } else if (key == GLFW.GLFW_KEY_L) {
//                loadClipboard();
//                return true;
//            }
//        }
        return false;
    }

    private void loadClipboard() {
        // Create a JFileChooser instance
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load File");
        fileChooser.setApproveButtonText("Load");

        // Show the save dialog
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(null, "File loaded: " + fileToSave.getAbsolutePath());
        }
    }

    private void saveClipboard() {
        // Create a JFileChooser instance
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Create New File");
        fileChooser.setApproveButtonText("Create");

        // Show the save dialog
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                if (fileToSave.createNewFile()) {
                    JOptionPane.showMessageDialog(null, "File created: " + fileToSave.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(null, "File already exists: " + fileToSave.getAbsolutePath());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "An error occurred while creating the file.");
                e.printStackTrace();
            }
        }
    }
}
