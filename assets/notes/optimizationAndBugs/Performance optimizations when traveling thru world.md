# Performance optimizations when traveling thru world

## 1. Identify the cause
* Its not the chunk rendering...
* Its NOT mesh generaiton or mesh sending!

** I think it is the memory usage**
  * There are stair bumps in memory that align perfectly to when the player is traveling thru the world
  * The reason why running thru the dev terrain is faster is because the memory hill is much smoother

**What could be causing too much memory usage? **
* The first culprit is from Terrain.generateChunkInner()
* There could be some memory usage from sunlight generaiton
* The second culprit is probbably chunk mesh generation


## 2. Fix it