[//]: # (![Xbuilders landscape]&#40;./assets/images/b.jpg&#41;)

# Xbuilders 3
**A voxel game written in Java + LWJGL**
** the repository is currently not accepting contributions **

## Overview
This game is a minecraft like block game, written in Java, with priority on **performance** and **simplicity**.

The game uses greedy meshing to generate chunks and supports light, custom block types and mobile entities.

This game can run at top performance at up to a 400 voxel view radius. It has a 3d grid of chunks, The world height is limited to 255 blocks, however that is self imposed primarly due to preformance and sunlight generation in the future, and can be easily bypassed.



## Important notes
* The JVM version must be 17. If it is higher the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.

## TODO Optimizations + Bufgixes
**A NOTE ABOUT DETERMINING BOTTLENECKS**
* Using a profiler is CRUCIAL:
  * https://www.baeldung.com/java-profilers
  * You should be able to use tools to profile the CPU AND GPU

* For some reason FPS slows down to about 30FPS when living in complex terrains like default terrain or complex terrain
  * https://www.reddit.com/r/opengl/comments/1d8tb6v/low_fps_when_drawing_lots_of_triangles_what_to_do/
  * https://www.reddit.com/r/gamedev/comments/1d8tdlk/lots_of_triangles_low_fps/
  * https://www.reddit.com/r/VoxelGameDev/comments/1d8t945/low_fps_with_lots_of_triangles_what_to_do/
* There is a bug where when too many block events are queued, the chunk does not update until the large ones are done
* Sometimes block events keep recurring over and over again even though allowBlockEvents is false

### Performance optimizations when traveling thru world
* The optimization for memory manegment when traveling thru the world isnt finished yet
the only bottleneckes are:
* greedy mesher
* naive mesher
  * (unless it is generating the whole mesh, the contribution is minor)
* sunlight generation

notes:
* hashmaps and hashsets are major bottlenecks. avoid these whenever possible


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
* The game sometimes crashes
  * BUG REPORT: https://github.com/LWJGL/lwjgl3/issues/986
  * https://inside.java/2020/12/03/crash-outside-the-jvm/
  * FROM NOW ON: Record every log file that you get

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

[//]: # (## Current Features)

[//]: # (I plan on adding all the features from XBuilders 2 into this game, For now, here are the currenly available features in XBuilders 3)

[//]: # (* Block tools)

[//]: # (  * Block boundary)

[//]: # (  * Copy/Paste)

[//]: # (* Decorative blocks)

[//]: # (  * slab,stairs,fence,fence gate, trapdoor, door, pillar, floor item, vines, torch, lamp and glass pane)

[//]: # (* Animals &#40;Entity&#41;)

[//]: # (  * Fox, horse, dog, rabbit and cat)

[//]: # (* Torchlight and sunlight)

[//]: # (* Growable trees)

[//]: # (* Smooth mesh lighting)

[//]: # (* Animated block textures)

[//]: # (* Growable trees)

[//]: # (* Smooth mesh lighting)

[//]: # (* Animated block textures)

[//]: # ()
[//]: # ()
[//]: # (## Keys and buttons)

[//]: # (Key | action)

[//]: # (--|--)

[//]: # (W|up/fly)

[//]: # (S|down)

[//]: # (spacebar|jump/enable gravity)

[//]: # (P|toggle collisions)

[//]: # (arrow keys|move horizontally)

[//]: # (F|toggle fast movement)

[//]: # (M|toggle menu)

[//]: # (ESC|leave world/exit game)

[//]: # (I|toggle inventory)

[//]: # (F11|save screenshot)

[//]: # ()
[//]: # ()
[//]: # (Mouse | action)

[//]: # (--|--)

[//]: # (Scroll|select block)

[//]: # (Right-click|delete block)

[//]: # (Left-click|create block)




[//]: # ()
[//]: # ()
[//]: # (## Screenshots)

[//]: # (A few of the blocks currently available)

[//]: # (![blocks]&#40;./assets/images/a.jpg&#41;)

[//]: # ()
[//]: # (Inside a house)

[//]: # (![indoors]&#40;./assets/images/c.jpg&#41;)

[//]: # ()
[//]: # (A group of foxes)

[//]: # (![foxes]&#40;./assets/images/e.jpg&#41;)

[//]: # ()
[//]: # (Lots of foxes!)

[//]: # (![lots of foxes]&#40;./assets/images/f.jpg&#41;)

[//]: # ()
[//]: # (Screenshot of the inventory menu)

[//]: # (![inventory]&#40;./assets/images/g.jpg&#41;)

[//]: # ()
[//]: # (Ingame screenshot:)

[//]: # (![ingame]&#40;./assets/images/h.jpg&#41;)
