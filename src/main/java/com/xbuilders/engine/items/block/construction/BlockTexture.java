package com.xbuilders.engine.items.block.construction;

import com.xbuilders.engine.items.block.BlockArrayTexture;

import static com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.CompactVertexSet.MAX_BLOCK_ANIMATION_LENGTH;

public class BlockTexture {

    public class FaceTexture {

        public final int zLayer;
        private byte animationFrames;

        public FaceTexture(int id, int animLength) {
            this.zLayer = id;
            this.setAnimationFrames(animLength);
        }

        public byte getAnimationFrames() {
            return animationFrames;
        }

        public void setAnimationFrames(int animationFrames) {
            if(animationFrames > MAX_BLOCK_ANIMATION_LENGTH) {
                animationFrames = MAX_BLOCK_ANIMATION_LENGTH;
            }
            this.animationFrames = (byte) animationFrames;
        }
    }

    public final void init(BlockArrayTexture textureNameMap) {
        POS_X = new FaceTexture(textureNameMap.getTextureLayer(POS_X_NAME), textureNameMap.getAnimationLength(POS_X_NAME));
        NEG_X = new FaceTexture(textureNameMap.getTextureLayer(NEG_X_NAME), textureNameMap.getAnimationLength(NEG_X_NAME));
        POS_Y = new FaceTexture(textureNameMap.getTextureLayer(POS_Y_NAME), textureNameMap.getAnimationLength(POS_Y_NAME));
        NEG_Y = new FaceTexture(textureNameMap.getTextureLayer(NEG_Y_NAME), textureNameMap.getAnimationLength(NEG_Y_NAME));
        POS_Z = new FaceTexture(textureNameMap.getTextureLayer(POS_Z_NAME), textureNameMap.getAnimationLength(POS_Z_NAME));
        NEG_Z = new FaceTexture(textureNameMap.getTextureLayer(NEG_Z_NAME), textureNameMap.getAnimationLength(NEG_Z_NAME));
    }

    public final String POS_Y_NAME, NEG_Y_NAME, POS_X_NAME, NEG_X_NAME, POS_Z_NAME, NEG_Z_NAME;
    private FaceTexture POS_X, NEG_X, POS_Y, NEG_Y, POS_Z, NEG_Z;

    public FaceTexture getPOS_X() {
        return POS_X;
    }

    public FaceTexture getNEG_X() {
        return NEG_X;
    }

    public FaceTexture getPOS_Y() {
        return POS_Y;
    }

    public FaceTexture getNEG_Y() {
        return NEG_Y;
    }

    public FaceTexture getPOS_Z() {
        return POS_Z;
    }

    public FaceTexture getNEG_Z() {
        return NEG_Z;
    }

    public BlockTexture(String POS_Y, String NEG_Y, String POS_X, String NEG_X, String POS_Z, String NEG_Z) {
        this.NEG_Y_NAME = POS_Y;
        this.POS_Y_NAME = NEG_Y;
        this.POS_X_NAME = POS_X;
        this.NEG_X_NAME = NEG_X;
        this.POS_Z_NAME = POS_Z;
        this.NEG_Z_NAME = NEG_Z;
    }

    public BlockTexture(String POS_Y, String NEG_Y, String SIDES) {
        this.NEG_Y_NAME = POS_Y;
        this.POS_Y_NAME = NEG_Y;
        this.POS_X_NAME = SIDES;
        this.NEG_X_NAME = SIDES;
        this.POS_Z_NAME = SIDES;
        this.NEG_Z_NAME = SIDES;
    }

    public BlockTexture(String index) {
        POS_X_NAME = index;
        NEG_X_NAME = index;
        POS_Y_NAME = index;
        NEG_Y_NAME = index;
        POS_Z_NAME = index;
        NEG_Z_NAME = index;
    }

    public String toString() {
        return POS_Y_NAME + " " + NEG_Y_NAME + " " + POS_X_NAME + " " + NEG_X_NAME + " " + POS_Z_NAME + " " + NEG_Z_NAME;
    }

}
