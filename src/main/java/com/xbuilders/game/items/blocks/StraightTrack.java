//package com.xbuilders.game.items.blocks;
//
//import com.xbuilders.engine.gameScene.GameScene;
//import com.xbuilders.engine.items.BlockList;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.BlockTexture;
//import com.xbuilders.engine.player.pipeline.BlockHistory;
//import org.joml.Vector3i;
//
//import java.util.ArrayList;
//
//public class StraightTrack extends Block {
//
//    public StraightTrack(int id, String name, BlockTexture texture) {
//        super(id, name, texture);
//        solid = false;
//        opaque = false;
//        type = RenderType.FLOOR;
//    }
//
//    private void addNeighbor(ArrayList<Vector3i> trackPositions, int x2, int y2, int z2) {
//        Block b = GameScene.world.getBlock(x2, y2, z2);
//        if (MinecartUtils.isTrack(b)) {
//            trackPositions.add(new Vector3i(x2, y2, z2));
//        }
//    }
//
//    private boolean changeBlock(int x, int y, int z) {
//        ArrayList<Vector3i> trackPositions = new ArrayList<>();
//        BlockOrientation orientation = new BlockOrientation((byte) 0, (byte) 0);
//        addNeighbor(trackPositions, x + 1, y, z);
//        addNeighbor(trackPositions, x - 1, y, z);
//        addNeighbor(trackPositions, x, y, z + 1);
//        addNeighbor(trackPositions, x, y, z - 1);
//
//        addNeighbor(trackPositions, x + 1, y + 1, z);
//        addNeighbor(trackPositions, x - 1, y + 1, z);
//        addNeighbor(trackPositions, x, y + 1, z + 1);
//        addNeighbor(trackPositions, x, y + 1, z - 1);
//
//        addNeighbor(trackPositions, x + 1, y - 1, z);
//        addNeighbor(trackPositions, x - 1, y - 1, z);
//        addNeighbor(trackPositions, x, y - 1, z + 1);
//        addNeighbor(trackPositions, x, y - 1, z - 1);
//
//        boolean curvedTrack = false;
//        boolean straightTrack = false;
//
//        if (!trackPositions.isEmpty()) {
//            if (isTrackAtPos2(trackPositions, x - 1, y, z) && isTrackAtPos2(trackPositions, x, y, z - 1)) {
//                orientation.setXZ((byte) 2);
//                GameItems.CURVED_TRACK.set(x, y, z, orientation);
//                curvedTrack = true;
//                return true;
//            } else if (isTrackAtPos2(trackPositions, x + 1, y, z) && isTrackAtPos2(trackPositions, x, y, z - 1)) {
//                orientation.setXZ((byte) 3);
//                GameItems.CURVED_TRACK.set(x, y, z, orientation);
//                curvedTrack = true;
//                return true;
//            } else if (isTrackAtPos2(trackPositions, x - 1, y, z) && isTrackAtPos2(trackPositions, x, y, z + 1)) {
//                orientation.setXZ((byte) 1);
//                GameItems.CURVED_TRACK.set(x, y, z, orientation);
//                curvedTrack = true;
//                return true;
//            } else if (isTrackAtPos2(trackPositions, x + 1, y, z) && isTrackAtPos2(trackPositions, x, y, z + 1)) {
//                orientation.setXZ((byte) 0);
//                GameItems.CURVED_TRACK.set(x, y, z, orientation);
//                curvedTrack = true;
//                return true;
//            } //=====================
//            else if (isTrackAtPos2(trackPositions, x - 1, y, z) || isTrackAtPos2(trackPositions, x + 1, y, z)) {
//                orientation.setXZ((byte) 1);
//                straightTrack = true;
//                this.set(x, y, z, orientation);
//            } else if (isTrackAtPos2(trackPositions, x, y, z - 1) || isTrackAtPos2(trackPositions, x, y, z + 1)) {
//                orientation.setXZ((byte) 0);
//                this.set(x, y, z, orientation);
//                straightTrack = true;
//            }
//        }
//        if (!curvedTrack) {
//            if (isTrackAtPos(trackPositions, x + 1, y - 1, z)) {
//                orientation.setXZ((byte) 1);
//                GameItems.RAISED_TRACK.set(x, y, z, orientation);
//                return true;
//            } else if (isTrackAtPos(trackPositions, x - 1, y - 1, z)) {
//                orientation.setXZ((byte) 3);
//                GameItems.RAISED_TRACK.set(x, y, z, orientation);
//                return true;
//            } else if (isTrackAtPos(trackPositions, x, y - 1, z + 1)) {
//                orientation.setXZ((byte) 2);
//                GameItems.RAISED_TRACK.set(x, y, z, orientation);
//                return true;
//            } else if (isTrackAtPos(trackPositions, x, y - 1, z - 1)) {
//                orientation.setXZ((byte) 0);
//                GameItems.RAISED_TRACK.set(x, y, z, orientation);
//                return true;
//            }
//        }
//        return straightTrack;
//    }
//
//    public static boolean isNotSecure(int x, int y, int z) {
//        Block b = GameScene.world.getBlock(x, y + 1, z);
//        return (MinecartUtils.isTrack(b) || !b.isSolid());
//    }
//
//    @Override
//    public boolean setBlock(int x, int y, int z, BlockData data) {
//        if (isNotSecure(getPointerHandler(), x, y, z)) {
//            return false;
//        }
//        if (!changeBlock(x, y, z)) {
//            this.set(x, y, z, data);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onLocalChange(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition) {
//        int x = changedPosition.x;
//        int y = changedPosition.y;
//        int z = changedPosition.z;
//        if (isNotSecure(getPointerHandler(), x, y, z)) {
//            BlockList.BLOCK_AIR.set(x, y, z);
//        } else {
//            changeBlock(x, y, z);
//        }
//    }
//
//    private boolean isTrackAtPos(ArrayList<Vector3i> trackPositions, int x, int y, int z) {
//        return trackPositions.contains(new Vector3i(x, y, z));
//    }
//
//    private boolean isTrackAtPos2(ArrayList<Vector3i> trackPositions, int x, int y, int z) {
//        return trackPositions.contains(new Vector3i(x, y, z))
//                || trackPositions.contains(new Vector3i(x, y + 1, z))
//                || trackPositions.contains(new Vector3i(x, y - 1, z));
//    }
//}