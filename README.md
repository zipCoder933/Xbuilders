
![Xbuilders landscape](./assets/images/b.jpg)

# Xbuilders 3
**A voxel game written in Java + LWJGL**


# Managable To-Do List towards XBuilders 2
We split the implementation features into a list of items that can be completed in a very short time period. Each item should be able to be completed in roughly 2 hours:
we can calculate the number of weeks it will take with the folowing formula:
`number of weeks = total hours / hours spent per week
`

1. Add copy/paste tools
7. Import a few terrains from xb2
9. Add doors as blocks
10. add trapdoors and fence gates as blocks
10. load/write to chunks as regions
   1. include file version as metadata
   12. Make a conversion tool to convert xb2 worlds to xb3
8.  Add more animals
9. important features
   1. Make chunks load light and meshes before the user enters the game
   2. Player spawn position must actually work
   4. Add JSON settings
10. bugfixes and performance optimizations

# Bugfixes
* voxels from a previous game show up in new chunks
* Determine if we need to erase block data when blocks are deleted
* Tnt does not clear itself properly, it leaves active tnt's behind that keeps detonating each other

### When setting LOTS of blocks, it somehow gets separated into multiple queues that get cleared in adjacent frames, instead of getting processed in one resolve() on another thread
Observe the faulty code:
```dtd
UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
25801 Block Events (1 frames in row)

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
8208 Block Events (2 frames in row)

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
4055 Block Events (3 frames in row)

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
1787 Block Events (4 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 32

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
1963 Block Events (5 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 32

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
1421 Block Events (6 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 24

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
948 Block Events (7 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 16
Opaque to transparent: 38
Transparent to opaque: 0
Done. Chunks affected: 24

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
6870 Block Events (8 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 24

UPDATING EVENTS: 	MultiThreaded: true allowBlockEvents: true
24 Block Events (9 frames in row)
Opaque to transparent: 0
Transparent to opaque: 0
Done. Chunks affected: 11
```

possible culprits
* the events are dispatched on a frame that has block events
  * count how many block events that are set when boundary is set
  * 

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

-->
