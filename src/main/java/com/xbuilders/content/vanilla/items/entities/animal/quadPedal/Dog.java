package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.content.vanilla.items.Blocks;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.entity.EntitySupplier;
import com.xbuilders.engine.server.model.players.Player;

import java.io.IOException;


public class Dog extends QuadPedalLandAnimal {
    public Dog(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window, false);
    }

    static QuadPedalLandAnimal_StaticData staticData;

    @Override
    public QuadPedalLandAnimal_StaticData getStaticData() throws IOException {
        if (staticData == null) {
            staticData = new QuadPedalLandAnimal_StaticData("items\\entity\\animal\\dog\\large\\body.obj", "items\\entity\\animal\\dog\\large\\sitting.obj", "items\\entity\\animal\\dog\\large\\leg.obj", null, "items\\entity\\animal\\dog\\textures");
        }
        return staticData;
    }

    @Override
    public void initSupplier(EntitySupplier entitySupplier) {
        super.initSupplier(entitySupplier);
        entitySupplier.spawnCondition = (x, y, z) -> {
            if (GameScene.getLightLevel(x, y, z) > 5) return false;//If it's too bright, don't spawn
            Block floor = GameScene.world.getBlock(x, (int) (y + Math.ceil(aabb.box.getYLength())), z);
            if (floor.solid && GameScene.world.getBlockID(x, y, z) == Blocks.BLOCK_AIR) return true;
            return false;
        };
        entitySupplier.isAutonomous = true;
    }

    Player playerWithLowestDist;
    long lastPlayerCheckTime;


    public void animal_move() {
        if (tamed) super.animal_move();
        else {
//            if (GameScene.server.isPlayingMultiplayer()) {
//                if (playerWithLowestDist == null || System.currentTimeMillis() - lastPlayerCheckTime > 1000) {
//                    currentAction = new AnimalAction(AnimalAction.ActionType.FOLLOW);
//                    lastPlayerCheckTime = System.currentTimeMillis();
//                    playerWithLowestDist = null;
//                    float lowestDist = Float.MAX_VALUE;
//                    for (PlayerClient pc : GameScene.server.clients) {
//                        if (pc.player != null) {
//                            float dist = pc.player.worldPosition.distance(worldPosition);
//                            if (dist < lowestDist) {
//                                lowestDist = dist;
//                                playerWithLowestDist = pc.player;
//                            }
//                        }
//                    }
//                }
//            } else
            playerWithLowestDist = GameScene.player;

            //If the player is too close, the dog will start to attack
            if (distToPlayer < 2) {
                GameScene.player.addHealth(-0.1f);
            }

            if (playerWithLowestDist != null) {
                setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer(playerWithLowestDist)) + random.noise(2f, -3, 3));
                if (worldPosition.distance(playerWithLowestDist.worldPosition) > 4) goForward(0.17f, jumpOverBlocks);
                else goForward(0.1f, jumpOverBlocks);
            }

        }
    }

    @Override
    public void loadDefinitionData(Input input, Kryo kyro) throws IOException {
        super.loadDefinitionData(input, kyro);

        setActivity(0.7f);
        //Z is the direciton of the animal
        legXSpacing = 0.30f * SCALE;
        legZSpacing = 0.82f * SCALE;
        legYSpacing = -1.15f * SCALE; //negative=higher, positive=lower
    }


}
