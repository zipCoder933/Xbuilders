package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.model.Server;
import com.xbuilders.engine.server.model.players.PositionLock;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.content.vanilla.items.entities.animal.LegPair;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class QuadPedalLandAnimal extends LandAnimal {


    //This animal specific
    int textureIndex;


    public QuadPedalLandAnimal(int id, long uniqueIdentifier, ClientWindow window, boolean rideable) {
        super(id, uniqueIdentifier, window);
        aabb.setOffsetAndSize(1f, 1.5f, 1f, true);
        this.rideable = rideable;
        frustumSphereRadius = 2;
        currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 1000);
    }

    public static class QuadPedalLandAnimal_StaticData {
        public EntityMesh body, sittingBody, saddle;
        public int[] textures;
        public LegPair legs;

        public QuadPedalLandAnimal_StaticData(
                String bodyOBJ,
                String sittingBodyOBJ,
                String legOBJ,
                String saddleOBJ,
                String texturesDir) throws IOException {
            //Generate body
            body = new EntityMesh();
            body.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(bodyOBJ)));

            //Generate sitting body
            if (sittingBodyOBJ != null) {
                sittingBody = new EntityMesh();
                sittingBody.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(sittingBodyOBJ)));
            }

            //Generate legs
            EntityMesh legsModel = new EntityMesh();
            legsModel.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(legOBJ)));
            legs = new LegPair(legsModel);

            //Generate saddle
            if (saddleOBJ != null) {
                saddle = new EntityMesh();
                saddle.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(saddleOBJ)));
            }

            //Generate textures
            File[] textureFiles = ResourceUtils.resource(texturesDir).listFiles();
            textures = new int[textureFiles.length];
            for (int i = 0; i < textureFiles.length; i++) {
                textures[i] = Objects.requireNonNull(
                        TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
            }
        }
    }

    @Override
    public void serializeDefinitionData(JsonGenerator generator) throws IOException {
        generator.writeNumberField(JSON_SPECIES, textureIndex);
    }


    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);

        goForwardCallback = amount -> {
            legMovement += amount;
        };

        //Get static data from subclass
        try {
            QuadPedalLandAnimal_StaticData ead = getStaticData();
            this.body = ead.body;
            this.sittingBody = ead.sittingBody;
            this.legs = ead.legs;
            this.saddle = ead.saddle;
            this.textures = ead.textures;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }

        if (hasData) {
            textureIndex = node.get(JSON_SPECIES).asInt();
            textureIndex = MathUtils.clamp(textureIndex, 0, this.textures.length - 1);
        } else textureIndex = RandomUtils.random.nextInt(this.textures.length);

    }

    public abstract QuadPedalLandAnimal_StaticData getStaticData() throws IOException;

    //This speces
    EntityMesh body, sittingBody, saddle;
    public int[] textures;
    LegPair legs;
    boolean rideable = true;

    private float legMovement = 0;
    public float SCALE = 0.6f;
    protected float legXSpacing = 0.32f * SCALE;
    protected float legZSpacing = 0.9f * SCALE;
    protected float legYSpacing = -1.3f * SCALE;
    public final PositionLock lock = new PositionLock(this, -1);


    public void animal_move() {
        if (playerIsRidingThis()) {
            float rotSpeed = 0.5f;
            if (Server.userPlayer.forwardKeyPressed()) {
                goForward(0.2f, true);
                rotSpeed = 3;
                currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 1000);
            } else if (allowVoluntaryMovement()) super.animal_move();

            if (Server.userPlayer.leftKeyPressed()) {
                setRotationYDeg(getRotationYDeg() - rotSpeed);
            } else if (Server.userPlayer.rightKeyPressed()) {
                setRotationYDeg(getRotationYDeg() + rotSpeed);
            }
        } else if (allowVoluntaryMovement() && inFrustum) super.animal_move();
    }

    @Override
    public void animal_drawBody() {
        shader.bind();
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);

        if (currentAction != null && currentAction.type == AnimalAction.ActionType.IDLE
                && sittingBody != null
                && currentAction.duration > 1000) {
            drawSitting();
        } else {
            drawBody();
            //Z is the directon of the horse
            legs.draw(modelMatrix, shader, legXSpacing, legYSpacing, legZSpacing, legMovement, textures[textureIndex]);
            legs.draw(modelMatrix, shader, legXSpacing, legYSpacing, -legZSpacing, legMovement, textures[textureIndex]);
        }
    }

    protected void drawBody() {
        body.draw(false, textures[textureIndex]);
        if (saddle != null) saddle.draw(false, textures[textureIndex]);
    }

    protected void drawSitting() {
        sittingBody.draw(false, textures[textureIndex]);
    }

    @Override
    public boolean run_ClickEvent() {
        if (!tamed) return false;

        if (rideable) {
            Server.userPlayer.positionLock = lock;
        } else {
            if (currentAction.type == AnimalAction.ActionType.IDLE) {
                currentAction = new AnimalAction(AnimalAction.ActionType.OTHER, 10);
            } else {//Make the animal sit
                if (distToPlayer < 5) {
                    setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer()));
                }
                currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 10 * 1000);
            }
        }
        multiplayerProps.markStateChanged();
        return true;
    }
}