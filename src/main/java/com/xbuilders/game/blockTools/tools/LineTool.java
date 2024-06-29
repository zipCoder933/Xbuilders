//package com.xbuilders.game.blockTools.tools;
//
//import com.xbuilders.engine.items.ItemType;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.player.camera.CursorRay;
//import com.xbuilders.game.blockTools.BlockTool;
//import com.xbuilders.game.blockTools.BlockTools;
//import com.xbuilders.window.BaseWindow;
//import org.lwjgl.glfw.GLFW;
//
//public class LineTool extends BlockTool {
//
//    public LineTool(BlockTools tools, CursorRay cursorRay) {
//        super("Line", tools, cursorRay);
//    }
//
//
//    @Override
//    public boolean setBlock(ItemQuantity item, CursorRaycast ray, BlockData data, boolean isCreationMode) {
//        Stopwatch watch = new Stopwatch();
//        watch.start();
//        long timeSinceStart = System.currentTimeMillis();
//
//        if (isCreationMode) {
//            if (item != null && item.getItem().itemType == ItemType.BLOCK) {
//                Block block = (Block) item.getItem();
//                setting.setLine(block, ray.cursorRay, timeSinceStart, toolSize, data);
//            }
//        } else setting.eraseLine(ray.cursorRay, timeSinceStart, toolSize);
//
//        blockSetter.wakeUp();
//        return true;
//    }
//
//    @Override
//    public boolean drawCursor(CursorRaycast ray, PGraphics g) {
//        g.translate(
//                ray.cursorRay.hitPostition.x + 0.5f,
//                ray.cursorRay.hitPostition.y + 0.5f,
//                ray.cursorRay.hitPostition.z + 0.5f);
//
//        float add = 0f;
//        g.strokeWeight(1.5f);
//        g.noFill();
//        g.stroke(255);
//
//        g.pushMatrix();
//        float transSize = ((int) toolSize / 2) + 0.5f;
//        if (toolSize % 2 != 0) {
//            transSize += 0.5f;
//        }
//        if (ray.cursorRay.hitNormal.x > 0) {
//            g.translate(transSize, 0, 0);
//            g.box(toolSize + 0.1f, 1 + add, 1 + add);
//        } else if (ray.cursorRay.hitNormal.x < 0) {
//            g.translate(-transSize, 0, 0);
//            g.box(toolSize + 0.1f, 1 + add, 1 + add);
//        } else {
//            if (ray.cursorRay.hitNormal.y > 0) {
//                g.translate(0, transSize, 0);
//                g.box(1 + add, toolSize + 0.1f, 1 + add);
//            } else {
//                if (ray.cursorRay.hitNormal.y < 0) {
//                    g.translate(0, -transSize, 0);
//                    g.box(1 + add, toolSize + 0.1f, 1 + add);
//                } else {
//                    if (ray.cursorRay.hitNormal.z > 0) {
//                        g.translate(0, 0, transSize);
//                        g.box(1 + add, 1 + add, toolSize + 0.1f);
//                    } else {
//                        g.translate(0, 0, -transSize);
//                        g.box(1 + add, 1 + add, toolSize + 0.1f);
//                    }
//                }
//            }
//            g.popMatrix();
//
//            g.stroke(255, 0, 0);
//            g.pushMatrix();
//            transSize = ((int) toolSize / 2);
//            if (toolSize % 2 == 0) {
//                transSize -= 0.5f;
//            }
//            if (ray.cursorRay.hitNormal.x > 0) {
//                g.translate(-transSize, 0, 0);
//                g.box(toolSize + 0.1f, 1 + add, 1 + add);
//            } else if (ray.cursorRay.hitNormal.x < 0) {
//                g.translate(transSize, 0, 0);
//                g.box(toolSize + 0.1f, 1 + add, 1 + add);
//            } else {
//                if (ray.cursorRay.hitNormal.y > 0) {
//                    g.translate(0, -transSize, 0);
//                    g.box(1 + add, toolSize + 0.1f, 1 + add);
//                } else {
//                    if (ray.cursorRay.hitNormal.y < 0) {
//                        g.translate(0, transSize, 0);
//                        g.box(1 + add, toolSize + 0.1f, 1 + add);
//                    } else {
//                        if (ray.cursorRay.hitNormal.z > 0) {
//                            g.translate(0, 0, -transSize);
//                            g.box(1 + add, 1 + add, toolSize + 0.1f);
//                        } else {
//                            g.translate(0, 0, transSize);
//                            g.box(1 + add, 1 + add, toolSize + 0.1f);
//                        }
//                    }
//                }
//            }
//
//        }
//        g.popMatrix();
//
//        return true;
//    }
//
//    @Override
//    public boolean shouldActivate(int key, int scancode, int action, int mods) {
//        if (key == GLFW.GLFW_KEY_4) return true;
//        return false;
//    }
//}
