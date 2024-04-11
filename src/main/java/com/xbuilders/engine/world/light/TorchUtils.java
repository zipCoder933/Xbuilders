package com.xbuilders.engine.world.light;


import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;

import java.util.*;

class TorchUtils {

    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //Transparent to opaque
    //------------------------------------------------------------------
    //------------------------------------------------------------------

    public static void transparentToOpaque(final Chunk chunk, final int x, final int y, final int z) {
        final TorchChannelSet channelSet = chunk.data.getTorch(x, y, z);
        if (channelSet != null) {
            for (final byte falloff : channelSet.list.keySet()) {
                final List<ChunkNode> queue = new ArrayList<>();
                queue.add(new ChunkNode(chunk, x, y, z));
               eraseSection(queue, falloff);
                queue.addAll(findNonzeroNeighbors(chunk, x, y, z, falloff));
               continueBFS(queue, falloff);
            }
        }
    }

    private static ArrayList<ChunkNode> findNonzeroNeighbors(final Chunk chunk, final int x, final int y, final int z, final byte channel) {
        final ArrayList<ChunkNode> nodes = new ArrayList<ChunkNode>();
        for (int x2 = -1; x2 < 2; ++x2) {
            for (int y2 = -1; y2 < 2; ++y2) {
                for (int z2 = -1; z2 < 2; ++z2) {
                    final ChunkNode node = checkNeigbor(chunk, x + x2, y + y2, z + z2, channel);
                    if (node != null) {
                        nodes.add(node);
                    }
                }
            }
        }
        return nodes;
    }

    private static ChunkNode checkNeigbor(Chunk chunk, int x, int y, int z, final byte channel) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return null;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final TorchChannelSet fragChannels = chunk.data.getTorch(x, y, z);
        if (fragChannels != null && fragChannels.get(channel) > 0) {
            return new ChunkNode(chunk, x, y, z);
        }
        return null;
    }


    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //Opaque to transparent
    //------------------------------------------------------------------
    //------------------------------------------------------------------
    public static void opaqueToTransparent(final Chunk chunk, final int x, final int y, final int z) {
        final HashMap<Byte, OTTNode> channels = findChannelsAndNeigbors(chunk, x, y, z);
        for (final Map.Entry<Byte, OTTNode> entry2 : channels.entrySet()) {
            final byte falloff = entry2.getKey();
            final ChunkNode startNode = entry2.getValue().node;
            final List<ChunkNode> queue = new ArrayList<>();
            queue.add(startNode);
           continueBFS(queue, falloff);
        }
    }

    protected static HashMap<Byte, OTTNode> findChannelsAndNeigbors(final Chunk chunk, final int x, final int y, final int z) {
        final HashMap<Byte, OTTNode> list = new HashMap<Byte, OTTNode>();
        findNeg2(chunk, x + 1, y, z, list);
        findNeg2(chunk, x - 1, y, z, list);
        findNeg2(chunk, x, y + 1, z, list);
        findNeg2(chunk, x, y - 1, z, list);
        findNeg2(chunk, x, y, z + 1, list);
        findNeg2(chunk, x, y, z - 1, list);
        return list;
    }

    private static void findNeg2(Chunk chunk, int x, int y, int z, final HashMap<Byte, OTTNode> list) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final TorchChannelSet neigboringChannels = chunk.data.getTorch(x, y, z);
        if (neigboringChannels != null) {
            for (final Map.Entry<Byte, Byte> entry : neigboringChannels.list.entrySet()) {
                final byte lightVal = entry.getValue();
                final byte falloff = entry.getKey();
                if (lightVal > 0 && (!list.containsKey(falloff) || list.get(falloff).lightVal < lightVal)) {
                    list.put(falloff, new OTTNode(new ChunkNode(chunk, x, y, z), lightVal));
                }
            }
        }
    }


    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //General utilities
    //------------------------------------------------------------------
    //------------------------------------------------------------------

    public static boolean isTorchAtThisPosition(final Chunk chunk, final int x, final int y, final int z) {
        final TorchChannelSet torchChannel = chunk.data.getTorch(x, y, z);
        return torchChannel != null && torchChannel.getCombinedLight() == 15;
    }

    public static void setTorch(final Chunk chunk, final int x, final int y, final int z, byte lightFallof) {
        if (!isTorchAtThisPosition(chunk, x, y, z)) {
            lightFallof = (byte) MathUtils.clamp(lightFallof, 1, 15);
            final List<ChunkNode> queue = new ArrayList<>();
            queue.add(new ChunkNode(chunk, x, y, z));
            chunk.data.setTorch(x, y, z, lightFallof, (byte) 15);
            continueBFS(queue, lightFallof);
        }
    }

    public static void removeTorchlight(final Chunk chunk, final int x, final int y, final int z, final byte lightFalloff) {
        final TorchChannelSet channels = chunk.data.getTorch(x, y, z);
        if (channels != null && channels.get(lightFalloff) == 15) {
            final List<ChunkNode> queue = new ArrayList<>();
            queue.add(new ChunkNode(chunk, x, y, z));
            final HashSet<ChunkNode> edges = eraseSection(queue, lightFalloff);
            queue.clear();
            queue.addAll(edges);
            continueBFS(queue, lightFalloff);
        }
    }


    public static void continueBFS(final List<ChunkNode> queue, final byte falloff) {
        while (queue.size() > 0) {
            final ChunkNode node = queue.remove(0);
            final TorchChannelSet torchChannels = node.chunk.data.getTorch(node.x, node.y, node.z);
            final int lightValue = (torchChannels == null) ? 0 : torchChannels.get(falloff);
            checkNeighborCont(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, falloff);
            checkNeighborCont(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, falloff);
            checkNeighborCont(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, falloff);
            checkNeighborCont(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, falloff);
            checkNeighborCont(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, falloff);
            checkNeighborCont(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, falloff);
        }
    }

    private static void checkNeighborCont(Chunk chunk, int x, int y, int z, final int lightLevel, final List<ChunkNode> queue, final byte lightFallof) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final ChunkNode node = new ChunkNode(chunk, x, y, z);
        final TorchChannelSet torchChannel = chunk.data.getTorch(x, y, z);
        final int neighborLevel = (torchChannel == null) ? 0 : torchChannel.get(lightFallof);
        final Block block = ItemList.getBlock(chunk.data.getBlock(x, y, z));
        if ((!block.opaque || block.luminous) && neighborLevel + (lightFallof + 1) <= lightLevel) {
            chunk.data.setTorch(x, y, z, lightFallof, (byte) (lightLevel - lightFallof));
            queue.add(node);
        }
    }

    public static HashSet<ChunkNode> eraseSection(final List<ChunkNode> queue, final byte falloff) {
        final HashSet<ChunkNode> edgeNodes = new HashSet<ChunkNode>();
        while (queue.size() > 0) {
            final ChunkNode node = queue.remove(0);
            final TorchChannelSet torchChannels = node.chunk.data.getTorch(node.x, node.y, node.z);
            final int lightValue = (torchChannels == null) ? 0 : torchChannels.get(falloff);
            node.chunk.data.setTorch(node.x, node.y, node.z, falloff, (byte) 0);
            checkNeighborErase(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, edgeNodes, falloff);
            checkNeighborErase(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, edgeNodes, falloff);
            checkNeighborErase(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, edgeNodes, falloff);
            checkNeighborErase(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, edgeNodes, falloff);
            checkNeighborErase(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, edgeNodes, falloff);
            checkNeighborErase(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, edgeNodes, falloff);
        }
        return edgeNodes;
    }

    private static void checkNeighborErase(Chunk chunk, int x, int y, int z, final int lightLevel, final List<ChunkNode> queue, final HashSet<ChunkNode> edgeNodes, final byte falloff) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final ChunkNode node = new ChunkNode(chunk, x, y, z);
        final TorchChannelSet torchChannels = chunk.data.getTorch(x, y, z);
        final int neighborLevel = (torchChannels == null) ? 0 : torchChannels.get(falloff);
        if (neighborLevel < lightLevel) {
            queue.add(node);
        } else {
            edgeNodes.add(node);
        }
    }
}