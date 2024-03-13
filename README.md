![Xbuilders landscape](./assets/images/b.jpg)

# Xbuilders
**A voxel game written in Java + LWJGL**

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

### Current features include:
* Animated blocks
* Multiple block types, including stairs, slabs, pillars, ladders, floor items (tracks, etc.) and ladders
* Entities (including animals)
  * Current entities include a Fox (animal)
* A UI main screen
* World saving/loading
* Fast chunk generation with greedy meshing
* Support for large chunk distances (up to 400 voxel radius)

### Things to know:
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.


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