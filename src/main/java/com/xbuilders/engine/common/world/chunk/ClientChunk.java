package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.Main;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers.ChunkMeshBundle;
import com.xbuilders.engine.common.world.ClientWorld;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.concurrent.Future;

import static com.xbuilders.engine.common.world.World.*;

public class ClientChunk extends Chunk {
    private ChunkMeshBundle meshes;
    public final Matrix4f client_modelMatrix;
    private Future<ChunkMeshBundle> mesherFuture;
    static int blockTextureID;

    public ChunkMeshBundle getMeshes() {
        return meshes;
    }

    public float getDistToPlayer() {
        return Main.getClient().userPlayer.worldPosition.distance(position.x * Chunk.WIDTH, position.y * Chunk.HEIGHT, position.z * Chunk.WIDTH);
    }

    public ClientChunk(Vector3i position,
                       FutureChunk futureChunk, ClientWorld world,
                       int blockTextureID2) {
        super(position, futureChunk, world);
        blockTextureID = blockTextureID2;
        this.client_modelMatrix = new Matrix4f();
        this.client_modelMatrix.identity().setTranslation(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH);
    }

    public ClientChunk(Chunk other, Vector3i position,
                       FutureChunk futureChunk, ClientWorld world,
                       int blockTextureID) {
        super(other, position, futureChunk, world);
        this.client_modelMatrix = new Matrix4f();

        if (other instanceof ClientChunk clientOther) { //If this is client chunk, Recycle and init the chunk meshes
            this.meshes = clientOther.getMeshes();
            meshes.init(aabb);
        }

        this.client_modelMatrix.identity().setTranslation(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH);
    }


    public void updateMVP(Matrix4f projection, Matrix4f view) {
        mvp.update(projection, view, client_modelMatrix);
    }


    public void prepare(long frame, boolean isSettingUpWorld) {
        //We have to initialize all OPENGL stuff in a place where they wont crash the game
        if (meshes == null) {
            this.meshes = new ChunkMeshBundle(blockTextureID, this, world.terrain);
            this.meshes.init(aabb);
        }

        if (inFrustum || isSettingUpWorld) { //Only updated meshes for where we can see
            if (!getMeshes().hasBeenGenerated() && mesherFuture == null) {  //Generate the mesh for the first time
                mesherFuture = meshService.submit(() -> {
//                    System.out.println("Generating mesh at " + position.toString());
                    getMeshes().compute();
                    return getMeshes();
                });
            }

            Client.frameTester.startProcess();// Send mesh to GPU if mesh thread is finished
            entities.chunkUpdatedMesh = true;
            if (mesherFuture != null && mesherFuture.isDone()) {
                try {
                    mesherFuture.get().sendToGPU();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mesherFuture = null;
                }
            }
            Client.frameTester.endProcess("Send mesh to GPU");
        }

//        if (loadFuture != null && loadFuture.isDone()) {
//
//            if (pillarInformation != null && pillarInformation.isTopChunk(this) && !pillarInformation.pillarLightLoaded && pillarInformation.isPillarLoaded()) {
//                pillarInformation.initLighting(null, terrain, getDistToPlayer());
//                pillarInformation.pillarLightLoaded = true;
//            }
//
//            if (getGenerationStatus() >= GEN_SUN_LOADED && !gen_Complete()) {
//                if (neghbors.allNeghborsLoaded) {
//                    loadFuture = null;
//                    Client.frameTester.startProcess();
//                    mesherFuture = meshService.submit(() -> {
//                        getMeshes().compute();
//                        setGenerationStatus(GEN_COMPLETE);
//                        return getMeshes();
//                    });
//                    Client.frameTester.endProcess("red Compute meshes");
//                } else {
//                    /**
//                     * The cacheNeighbors is still a bottleneck. I have kind of fixed it
//                     * by only calling it every 10th frame
//                     */
//                    Client.frameTester.startProcess();
//                    if (frame % 20 == 0 || isSettingUpWorld) {
//                        neghbors.cacheNeighbors();
//                    }
//                    Client.frameTester.endProcess("red Cache Neghbors");
//                }
//            }
//        }

    }

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh(boolean isPlayerUpdate) {
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }

        if (isPlayerUpdate) mesherFuture = playerUpdating_meshService.submit(() -> {
            getMeshes().compute();
            return getMeshes();
        });
        else mesherFuture = meshService.submit(() -> {
            getMeshes().compute();
            return getMeshes();
        });
    }


    public void updateMesh(boolean updateAllNeighbors, int x, int y, int z) {
        if (!neghbors.allFacingNeghborsLoaded) {
            neghbors.cacheNeighbors();
        }
        generateMesh(true);
        if (neghbors.allFacingNeghborsLoaded) {
            if (x == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_X_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.NEG_X_NEIGHBOR]).generateMesh(true);
            } else if (x == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_X_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.POS_X_NEIGHBOR]).generateMesh(true);
            }

            if (y == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR]).generateMesh(true);
            } else if (y == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_Y_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.POS_Y_NEIGHBOR]).generateMesh(true);
            }

            if (z == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR]).generateMesh(true);
            } else if (z == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_Z_NEIGHBOR] != null)
                    ((ClientChunk) neghbors.neighbors[neghbors.POS_Z_NEIGHBOR]).generateMesh(true);
            }
        }
    }

}
