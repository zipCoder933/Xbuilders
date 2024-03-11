# Xbuilders
A voxel game written in Java + LWJGL

## Overview
This game is a minecraft like block game, with priority on performance and simplicity.
The game has a 3d grid of chunks, The world height is limited to 255 blocks, however that is self imposed primarly due to preformance and sunlight generation in the future, and can be easily bypassed.

### Current features include:
* Animated blocks
* Multiple block types, including stairs, slabs, pillars,
    * (Block types that have not yet been fully implemented include fenceposts, ladders, floor items, torches and lamps)
* Entities (including animals)
  * Currently the added mobile entities include a Fox
* A main screen
* World saving/loading
* Fast chunk generation with greedy meshing
* Support for large chunk distances (up to 400 voxel radius)

### Things to know:
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.
