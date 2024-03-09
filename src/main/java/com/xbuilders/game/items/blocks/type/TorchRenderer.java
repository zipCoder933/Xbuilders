///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.utils.math.AABB;
//import java.util.function.Consumer;
//
///**
// * @author zipCoder933
// */
//public class TorchRenderer extends BlockType {
//
//    private boolean sideIsSolid(Block block) {
//        return block != null && block.isSolid();
//    }
//
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        Blockdata data = BlockDataUtils.getBlockdata(data);
//
//        if (data != null && data.get(0) == 2 && sideIsSolid(positiveZ)) {
//            if (positiveZ.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 2);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 2);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (data != null && data.get(0) == 3 && sideIsSolid(negativeX)) {
//            if (negativeX.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 3);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 3);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (data != null && data.get(0) == 0 && sideIsSolid(negativeZ)) {
//            if (negativeZ.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 0);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 0);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (data != null && data.get(0) == 1 && sideIsSolid(positiveX)) {
//            if (positiveX.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 1);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 1);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(positiveY)) {
//            make_torch_center_faces(verts_torch, uv_torch, block, buffers, x, y, z);
//        } else if (sideIsSolid(positiveZ)) {
//            if (positiveZ.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 2);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 2);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(negativeX)) {
//            if (negativeX.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 3);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 3);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(negativeZ)) {
//            if (negativeZ.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 0);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 0);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(positiveX)) {
//            if (positiveX.getRenderType() == BlockRenderType.FENCE) {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side_fence, 1);
//                make_torch_side_fence_center_faces(verts2, uv_torch_side_fence, block, buffers, x, y, z);
//            } else {
//                PVector[] verts2 = verts2 = rotateVerticiesYAxis(verts_torch_side, 1);
//                make_torch_side_center_faces(verts2, uv_torch_side, block, buffers, x, y, z);
//            }
//        } else {
//            make_torch_center_faces(verts_torch, uv_torch, block, buffers, x, y, z);
//        }
//    }
//
//
//    @Override
//    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//    }
//
//    @Override
//    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//
//        if (data == null) {
//            box.setPosAndSize(x, y, z, 1, 1, 1);
//            consumer.accept(box);
//        } else {
//            if (data.get(0) == 2 && sideIsSolid(VoxelGame.getWorld().getBlock(x, y, z + 1))) {
//                side2(consumer, box, x, y, z);
//            } else if (data.get(0) == 3 && sideIsSolid(VoxelGame.getWorld().getBlock(x - 1, y, z))) {
//                side3(consumer, box, x, y, z);
//            } else if (data.get(0) == 0 && sideIsSolid(VoxelGame.getWorld().getBlock(x, y, z - 1))) {
//                side0(consumer, box, x, y, z);
//            } else if (data.get(0) == 1 && sideIsSolid(VoxelGame.getWorld().getBlock(x + 1, y, z))) {
//                side1(consumer, box, x, y, z);
//            } else if (VoxelGame.getWorld().getBlock(x, y + 1, z).isSolid()) {
//                box.setPosAndSize(x + ONE_SIXTEENTH * 6, y, z + ONE_SIXTEENTH * 6, ONE_SIXTEENTH * 4, 1, ONE_SIXTEENTH * 4);
//                consumer.accept(box);
//            } else if (sideIsSolid(VoxelGame.getWorld().getBlock(x, y, z + 1))) {
//                side2(consumer, box, x, y, z);
//            } else if (sideIsSolid(VoxelGame.getWorld().getBlock(x - 1, y, z))) {
//                side3(consumer, box, x, y, z);
//            } else if (sideIsSolid(VoxelGame.getWorld().getBlock(x, y, z - 1))) {
//                side0(consumer, box, x, y, z);
//            } else if (sideIsSolid(VoxelGame.getWorld().getBlock(x + 1, y, z))) {
//                side1(consumer, box, x, y, z);
//            }
//        }
//    }
//
//}
