# Bugs/Performance
## A NOTE ABOUT DETERMINING BOTTLENECKS
Using a profiler is CRUCIAL. You should be able to use tools to profile the CPU AND GPU

CPU profiling
* https://www.baeldung.com/java-profilers

GPU profiling
* **https://www.khronos.org/opengl/wiki/Debugging_Tools**
* https://developer.nvidia.com/nsight-graphics
* https://google.gprivate.com/search.php?search?q=amd+gpu+profiler
* you can also just use opengl timer queries to evaluate how much time gl calls take up
    * https://www.lighthouse3d.com/tutorials/opengl-timer-query/

## Block pipeline bugfix
* There is a bug where when too many block events are queued, the chunk does not update until the large ones are done
* Sometimes block events keep recurring over and over again even though allowBlockEvents is false
* Sometimes a large boundary of block events are not updated on another thread because the event count is somehow usually 1 or 4 events in size

## FPS slows down to about 30FPS when looking out at certain angles of the terrain
Threads:
* https://www.reddit.com/r/opengl/comments/1d8tb6v/low_fps_when_drawing_lots_of_triangles_what_to_do/
* https://www.reddit.com/r/gamedev/comments/1d8tdlk/lots_of_triangles_low_fps/
* https://www.reddit.com/r/VoxelGameDev/comments/1d8t945/low_fps_with_lots_of_triangles_what_to_do/

Causes:
* This issue is most likely not related to chunk rendering...
* I tested it and it isnt the block event pipeline
* This issue goes away when I switch my PC throttling mode from "balanced" or "cool" to "Performance"


**This is still an issue (7/13/2024)**
* Even with occlusion culling this issue can still be present from certain viewing angles and at certain times, It isnt a very big deal, but can still occur.
* (The occlusion culling does seem to help a lot however)
* PROFILE THE CODE FIRST TO DETERMINE REAL BOTTLENECK

## Performance optimizations when traveling thru world
* The optimization for memory manegment when traveling thru the world isnt finished yet
  the only bottleneckes are:
* greedy mesher
* naive mesher
    * (unless it is generating the whole mesh, the contribution is minor)
* sunlight generation

notes:
* hashmaps and hashsets are major bottlenecks. avoid these whenever possible

## Crash from nuklear
* BUG REPORT: https://github.com/LWJGL/lwjgl3/issues/986
* https://inside.java/2020/12/03/crash-outside-the-jvm/
* FROM NOW ON: Record every log file that you get
* http://forum.lwjgl.org/index.php?topic=6917.msg36395#msg36395


# FIXED Bugs (watch for these!)
- recent changes were not saved
    - It Was just the gameScene closeGame() method not being called
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



