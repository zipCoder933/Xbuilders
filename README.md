![Xbuilders landscape](./assets/images/b.jpg)

# Xbuilders 3
**A voxel game written in Java + LWJGL**

## IMPORTANT OPTIMIZATION NOTES
* The optimization for memory manegment when traveling thru the world isnt finished yet
  * the memory culprit in this case is still the naive and greedy meshers
  * there is still some memory optimizations to do, its alot better than before but could still be improved
  * **Very Important**: When sunlight generation is enabled, the memory usage is far greater than before
    * not sure if it is because of the sublight nodes, or the greedy mesher playing a role in that...

## Overview
This game is a minecraft like block game, written in Java, with priority on **performance** and **simplicity**.

The game uses greedy meshing to generate chunks and supports light, custom block types and mobile entities.

This game can run at top performance at up to a 400 voxel view radius. It has a 3d grid of chunks, The world height is limited to 255 blocks, however that is self imposed primarly due to preformance and sunlight generation in the future, and can be easily bypassed.

## Current Features
I plan on adding all the features from XBuilders 2 into this game, For now, here are the currenly available features in XBuilders 3
* Block tools
  * Block boundary
  * Copy/Paste
* Decorative blocks
  * slab,stairs,fence,fence gate, trapdoor, door, pillar, floor item, vines, torch, lamp and glass pane
* Animals (Entity)
  * Fox, horse, dog, rabbit and cat
* Torchlight and sunlight
* Growable trees
* Smooth mesh lighting
* Animated block textures

## Important notes
* The JVM version must be 17. If it is higher the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.

## TODO Optimizations + Bufgixes
* **performance optimizations when traveling thru world (IMPORTANT)**
* For some reason FPS slows down to about 30FPS when living in complex terrains like default terrain or complex terrain
* There is a bug where when too many block events are queued, the chunk does not update until the large ones are done
* Sometimes block events keep recurring over and over again even though allowBlockEvents is false

### The game sometimes crashes
https://inside.java/2020/12/03/crash-outside-the-jvm/
**FROM NOW ON:** Record every log file that you get and put the traces here:
Most of them seem to be coming from here:

```java
 // load draw vertices & elements directly into vertex + element buffer
ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, max_vertex_buffer, null));
ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, max_element_buffer, null));
try (MemoryStack stack = stackPush()) {
    // fill convert configuration
    NkConvertConfig config = NkConvertConfig.calloc(stack)
            .vertex_layout(VERTEX_LAYOUT)
            .vertex_size(20)
            .vertex_alignment(4)
            .tex_null(null_texture)
            .circle_segment_count(22)
            .curve_segment_count(22)
            .arc_segment_count(22)
            .global_alpha(1.0f)
            .shape_AA(AA)
            .line_AA(AA);

    // setup buffers to load vertices and elements
    NkBuffer vbuf = NkBuffer.malloc(stack);
    NkBuffer ebuf = NkBuffer.malloc(stack);

    nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
    nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
    nk_convert(ctx, cmds, vbuf, ebuf, config);//<-- This line is causing a lot of crashes
}
glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
glUnmapBuffer(GL_ARRAY_BUFFER);
```

* J 3998  org.lwjgl.nuklear.Nuklear.nnk_convert(JJJJJ)I (0 bytes) @ 0x0000019cf26002fe [0x0000019cf26002a0+0x000000000000005e]
* J 5054 c1 org.lwjgl.nuklear.Nuklear.nk_convert(Lorg/lwjgl/nuklear/NkContext;Lorg/lwjgl/nuklear/NkBuffer;Lorg/lwjgl/nuklear/NkBuffer;Lorg/lwjgl/nuklear/NkBuffer;Lorg/lwjgl/nuklear/NkConvertConfig;)I (39 bytes) @ 0x0000019ceb11ff0c [0x0000019ceb11f900+0x000000000000060c]
* J 3978 c1 com.xbuilders.window.NKWindow.NKrender(III)V (645 bytes) @ 0x0000019ceaa6c0bc [0x0000019ceaa69ae0+0x00000000000025dc]
* j  com.xbuilders.engine.ui.gameScene.GameUI.draw()V+113
* j  com.xbuilders.engine.gameScene.GameScene.render()V+101
* J 5062 c1 com.xbuilders.game.Main.render()V (22 bytes) @ 0x0000019ceb121444 [0x0000019ceb121360+0x00000000000000e4]
* j  com.xbuilders.game.Main.run()V+49
* j  com.xbuilders.game.Main.main([Ljava/lang/String;)V+75

  * I think the cause of this could be from the byte-buffers not being handled in a safe manner. Byte buffers+off-heap memory can cause crashes if not handled properly.
  * TODO: Switch out chunk voxels to use arrays instead of off-heap buffers
    * Do a search of the ENTIRE project to find all bytebuffers, shortbuffers, floatbuffers and intbuffers
  * TODO: Learn how to safely handle buffers and implmement those principles in all of the code

## TODO Features
* **Water**
  * Water live propagation
* **Finish entities**
  * Add saddle tools
  * Minecarts
    * boats
    * custom vehicles
  * banners
  * Add sea turtles
  * Add fish
  * Add parrots
* **player skins**
* **Add a conversion tool from XB2 to XB3**
  * Make all entity and tools match xb2 ids

## FIXED Bugs (watch for these!)
* I fixed a bug that prevented chunks from loading when blocks are set outside chunk voxel bounds
  * IF YOU ARE USING BYTEBUFFERS FOR CHUNK DATA:
    * Make sure that the chunkVoxels class prevents bytes from being written to the data if they are out of bounds
* **Fixed a bug where some chunks dont load meshes**
  * The issue was that the generation status was set to 2 after being set to 3
  * The solution was to only update the generation status if the new value was higher
* There was one time when there was a rendering bottleneck when drawing chunks
* After a chunk is updated, Some sprites still persist in mesh even if they are not actually in voxel data
  * The culprit to this was that the buffers were not updating unnless they were not empty, and so they wouldnt send to GPU
  * To prevent future bugs, MAKE SURE that the mesh is marked as empty if there is no vertex data to send

### Textures
Textures taken mostly from Pixel Perfection, with some handcrafted ones, and textures taken from open source minetest texture packs as well:
* https://github.com/Athemis/PixelPerfectionCE/tree/master
* https://github.com/Wallbraker/PixelPerfection?tab=readme-ov-file

### Blender profiles
There are 2 blender profiles, one for blocks and one for entities
* The block profile has +Y as up direction
* The entity profile has -Y as up direction

### Icon generation
If the resulting icons from icon generation are empty, the most likely cause would be that the iconRTT.vs vertex shader was not updated to match the chunk vertex shader

## TODO Multiplayer
a super easy way to do multiplayer could be
* each player owns a set of chunks thay modified.
* those chunks are read only to others
* periodically, the updated chunks are all sent to other players to be loaded in world or saved on disk



## Keys and buttons
Key | action
--|--
W|up/fly
S|down
spacebar|jump/enable gravity
P|toggle collisions
arrow keys|move horizontally
F|toggle fast movement
M|toggle menu
ESC|leave world/exit game
I|toggle inventory
F11|save screenshot


Mouse | action
--|--
Scroll|select block
Right-click|delete block
Left-click|create block






## Screenshots
A few of the blocks currently available
![blocks](./assets/images/a.jpg)

Inside a house
![indoors](./assets/images/c.jpg)

A group of foxes
![foxes](./assets/images/e.jpg)

Lots of foxes!
![lots of foxes](./assets/images/f.jpg)

Screenshot of the inventory menu
![inventory](./assets/images/g.jpg)

Ingame screenshot:
![ingame](./assets/images/h.jpg)
