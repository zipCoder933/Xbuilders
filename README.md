[//]: # (![Xbuilders landscape]&#40;./assets/images/b.jpg&#41;)

# Xbuilders 3
**A voxel game written in Java + LWJGL**

# Why XB3 over XB2?

## Important notes
* The JVM version must be 17. If it is higher the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.

## TODO Optimizations + Bufgixes
* **performance optimizations when traveling thru world (IMPORTANT)**
* If a naive meshed block is next to another chunk, the block is black
  * ![bug.png](assets\notes\images\bug.png)
* There is a bug where when too many block events are queued, the chunk does not update until the large ones are done
* Sometimes block events keep recurring over and over again even though allowBlockEvents is false
* After a chunk is updated, Some sprites still persist in mesh even if they are not actually in voxel data
* There are sometimes dark (14) sun values that can be left behind when bulk block setting is done
* **The game sometimes crashes**
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
* **Add copy/paste tools**
  * Simplify block tool controls
* **Add a conversion tool from XB2 to XB3**
  * Make all entity and tools match xb2 ids

## FIXED Bugs (watch for these!)
* I fixed a bug that prevented chunks from loading when blocks are set outside chunk voxel bounds
  * IF YOU ARE USING BYTEBUFFERS FOR CHUNK DATA:
    * Make sure that the chunkVoxels class prevents bytes from being written to the data if they are out of bounds
* **Fixed a bug where some chunks dont load meshes**
  * The issue was that the generation status was set to 2 after being set to 3
  * The solution was to only update the generation status if the new value was higher

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


# Information about features (dont delete)
## GAME BLOCK TOOLS
Here is a comprehensive list of all of the tools designed to make it faster to build:
* The block mode needs to be fast to change while also having as many features as possible
* Block mode tools and tool properties can be viewed via a dropdown menu by pressing Q

### tool parameters
A block tool has parameters. These can be toggled via hotkeys or using the dropdown menu

All block tools that use a block boudnary have 2 other parameters
* An option to toggle planar mode (make the boundary flat in X, Y or Z)
* An option to toggle the positioning of the endpoint to sit on ray hit, or ray hit+normal

## BLOCK EVENTS
* I started by allowing block types to choose how they want to exist. Certain conditions around the block would make it not allowed to be there
  * make a way for events to "shutdown" safely
  * find a way to intelligently execute block events on other threads
* when too many block events are called, it pauses the frame.
  * a solution would to be to shuttle all remaining events after the threshold limit per frame is set to a thread pool
    * I might also need to cancel/limit block set and local change events when doing it in bulk, as it would slow things down too

## WATER/FIRE/GRASS PROPAGATION WITH CELLULAR AUTOMATA
* we can use cellular autonoma interface so that the usage is essentially an abstraction of what is really going on

on a separate thread
- find a bunch of nodes to apply cellular autonoma on
- propagate using a BFS
- only the nodes that apply to said propagation process will survive
  - keep the bfs nodes separate for each process, this would also help them run at different speeds while also improving performance
- these nodes keep propagating so that we dont have to index them on each step
- if the distance from the node starting position is beyond a specific distance, index new nodes but keep the old ones
  - a node will be deleted if it is too far from player

- grass, that is dependent on the amount of time since placed, can store a timestamp in its block data?








<!---
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


## Overview
This game is a minecraft like block game, written in Java, with priority on **performance** and **simplicity**.

This game can run at top performance at up to a 400 voxel view radius. It has a 3d grid of chunks, The world height is limited to 255 blocks, however that is self imposed primarly due to preformance and sunlight generation in the future, and can be easily bypassed.



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

-->
