
# Xbuilders 3
**A voxel game written in Java + LWJGL**
** the repository is currently not accepting contributions **

# GIVE IT UP!!!



## Important notes
* The JVM version must be 17. If it is higher the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.

## TODO Optimizations + Bufgixes
* There is a bug where when too many block events are queued, the chunk does not update until the large ones are done
* Sometimes block events keep recurring over and over again even though allowBlockEvents is false


### A NOTE ABOUT DETERMINING BOTTLENECKS
* Using a profiler is CRUCIAL:
  * https://www.baeldung.com/java-profilers
  * You should be able to use tools to profile the CPU AND GPU

### For some reason FPS slows down to about 30FPS when living in complex terrains like default terrain or complex terrain
  * https://www.reddit.com/r/opengl/comments/1d8tb6v/low_fps_when_drawing_lots_of_triangles_what_to_do/
  * https://www.reddit.com/r/gamedev/comments/1d8tdlk/lots_of_triangles_low_fps/
  * https://www.reddit.com/r/VoxelGameDev/comments/1d8t945/low_fps_with_lots_of_triangles_what_to_do/

solutions
* PROFILE THE CODE FIRST TO DETERMINE REAL BOTTLENECK
* use a SSBO for light data and calculate interlopated light values on shader

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
