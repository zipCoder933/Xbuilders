# Bugs/Performance
### A NOTE ABOUT DETERMINING BOTTLENECKS
* Using a profiler is CRUCIAL:
    * https://www.baeldung.com/java-profilers
    * You should be able to use tools to profile the CPU AND GPU

### Block pipeline bugfix
* **There is a bug where when too many block events are queued, the chunk does not update until the large ones are done**
* **Sometimes block events keep recurring over and over again even though allowBlockEvents is false**
* **Sometimes a large boundary of block events are not updated on another thread because the event count is somehow usually 1 or 4 events in size**

### For some reason FPS slows down to about 30FPS when living in complex terrains like default terrain or complex terrain
* https://www.reddit.com/r/opengl/comments/1d8tb6v/low_fps_when_drawing_lots_of_triangles_what_to_do/
* https://www.reddit.com/r/gamedev/comments/1d8tdlk/lots_of_triangles_low_fps/
* https://www.reddit.com/r/VoxelGameDev/comments/1d8t945/low_fps_with_lots_of_triangles_what_to_do/
* **Implement occlusion culling first! (occlusion culling with openGL ARB occlusion query)**
    * **Use the one-note page to learn how to do it** Its a lot easier than it seems!
* PROFILE THE CODE FIRST TO DETERMINE REAL BOTTLENECK

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

