package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.Main;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers.ChunkMeshBundle;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import java.util.concurrent.Future;
import static com.xbuilders.engine.common.world.World.meshService;
import static com.xbuilders.engine.common.world.World.playerUpdating_meshService;

public class ClientChunk extends Chunk {
    //Client sided only
    public final ChunkMeshBundle meshes;
    public final Matrix4f client_modelMatrix;
    private Future<ChunkMeshBundle> mesherFuture;


    public ClientChunk(Vector3i position, boolean isTopChunk,
                       FutureChunk futureChunk, float distToPlayer, World world, int blockTextureID) {
        super(position, isTopChunk, futureChunk, distToPlayer, world);
        this.client_modelMatrix = new Matrix4f();
        this.meshes = new ChunkMeshBundle(blockTextureID, this, world.terrain);
        initVariables();
    }

    public ClientChunk(Chunk other, Vector3i position, boolean isTopChunk,
                       FutureChunk futureChunk, float distToPlayer, int blockTextureID) {
        super(other, position, isTopChunk, futureChunk, distToPlayer);
        this.client_modelMatrix = new Matrix4f();

        if (other instanceof ClientChunk clientOther) {
            this.meshes = clientOther.meshes;
        } else {
            this.meshes = new ChunkMeshBundle(blockTextureID, this, other.world.terrain);
        }

        initVariables();
    }

    private void initVariables() {
        this.client_modelMatrix.identity().setTranslation(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH);
        meshes.init(aabb);
    }


    public void updateMVP(Matrix4f projection, Matrix4f view) {
        mvp.update(projection, view, client_modelMatrix);
    }


    public void prepare(Terrain terrain, long frame, boolean isSettingUpWorld) {
        if (loadFuture != null && loadFuture.isDone()) {

            if (isTopChunk && pillarInformation != null && !pillarInformation.pillarLightLoaded && pillarInformation.isPillarLoaded()) {
                pillarInformation.initLighting(null, terrain, client_distToPlayer);
                pillarInformation.pillarLightLoaded = true;
            }

            if (getGenerationStatus() >= GEN_SUN_LOADED && !gen_Complete()) {
                if (neghbors.allNeghborsLoaded) {
                    loadFuture = null;
                    Client.frameTester.startProcess();
                    mesherFuture = meshService.submit(() -> {

                        if (Main.getClient().world.data == null) return null; // Quick fix. TODO: remove this line

                        meshes.compute();
                        setGenerationStatus(GEN_COMPLETE);
                        return meshes;
                    });
                    Client.frameTester.endProcess("red Compute meshes");
                } else {
                    /**
                     * The cacheNeighbors is still a bottleneck. I have kind of fixed it
                     * by only calling it every 10th frame
                     */
                    Client.frameTester.startProcess();
                    if (frame % 20 == 0 || isSettingUpWorld) {
                        neghbors.cacheNeighbors();
                    }
                    Client.frameTester.endProcess("red Cache Neghbors");
                }
            }
        }

        // send mesh to GPU
        if (inFrustum || isSettingUpWorld) {
            Client.frameTester.startProcess();
            entities.chunkUpdatedMesh = true;
            sendMeshToGPU();
            Client.frameTester.endProcess("Send mesh to GPU");
        }
    }

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh(boolean isPlayerUpdate) {
        setGenerationStatus(GEN_COMPLETE);
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }

        if (isPlayerUpdate) mesherFuture = playerUpdating_meshService.submit(() -> {
            meshes.compute();
            return meshes;
        });
        else mesherFuture = meshService.submit(() -> {
            meshes.compute();
            return meshes;
        });
    }

    /**
     * sends the mesh to the GPU after meshing
     */
    public void sendMeshToGPU() {
        // Send mesh to GPU if mesh thread is finished
        if (mesherFuture != null && mesherFuture.isDone() && gen_Complete()) {
            try {
                mesherFuture.get().sendToGPU();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mesherFuture = null;
            }
        }
    }


    public void updateMesh(boolean updateAllNeighbors, int x, int y, int z) {
        //TODO: There is a bug where the chunk is not updating the mesh
//        if (updateAllNeighbors) ClientWindow.printlnDev("Reg mesh (all neighbors)");
//        else ClientWindow.printlnDev("Reg mesh (" + x + ", " + y + ", " + z + ")");


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
