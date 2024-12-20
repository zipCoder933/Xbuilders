package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.window.NKWindow;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

public abstract class ContainerUI extends UI_ItemWindow {

    BlockData data;
    final Vector3i target = new Vector3i();

    public ContainerUI(NkContext ctx, NKWindow window, String title) {
        super(ctx, window, title);
    }

    public void assignToBlock(Block block) {
        block.clickEvent(false, (x, y, z) -> {
            BlockData data = GameScene.world.getBlockData(x, y, z);
            this.data = data;
            target.set(x, y, z);
            readContainerData(data.toByteArray());
            setOpen(true);
        });

        block.removeBlockEvent(false, (x, y, z, history) -> {
            if (history.previousBlockData == null) return;
            readContainerData(history.previousBlockData.toByteArray());
            dropAllStorage(x, y, z);
        });
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        //We constantly check if the block data has changed
        BlockData data = GameScene.world.getBlockData(target.x, target.y, target.z);
        if (data != null && !data.equals(this.data)) {
            //Update data
            readContainerData(data.toByteArray());
        }
    }

    public abstract void dropAllStorage(int x, int y, int z);

    public abstract void readContainerData(byte[] data);

    public abstract byte[] writeContainerData();

    public void writeDataToWorld() {
//        System.out.println("Writing data to world " + System.currentTimeMillis());
        data = new BlockData(writeContainerData());
        //using gameScene to set block data ensures it is set in the world and the client
        GameScene.setBlockData(data, target.x, target.y, target.z);
    }

    public void onCloseEvent() {
        writeDataToWorld();
    }
}
