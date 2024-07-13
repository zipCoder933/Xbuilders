![Xbuilders landscape](assets/images/b.jpg)

# Xbuilders
**A voxel game written in Java + LWJGL.**

## Running the Game
1. In the source folder, there should be a file called "XBuilders3.jar". Double-click to run it.
   - If nothing happens, the most likely cause could be that you dont have JRE 17 or later installed on your machine. 
   - In that case, you can install openJDK 17 here: https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-17
2. The game will do an initial setup. Double-click the jarfile again to run the game.

## Important notes
* The JVM version must be 17. If it is higher, the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nuklear library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y

### Textures
Textures taken mostly from Pixel Perfection, with some handcrafted ones, and textures taken from open source minetest texture packs as well:
* https://github.com/Athemis/PixelPerfectionCE/tree/master
* https://github.com/Wallbraker/PixelPerfection?tab=readme-ov-file

### Blender profiles
All entities and block types are made using blender.
There are 2 blender profiles, one for blocks and one for entities
* The block profile has +Y as up direction
* The entity profile has -Y as up direction

### Icon generation
If the resulting icons from icon generation are empty, the most likely cause would be that the iconRTT.vs vertex shader was not updated to match the chunk vertex shader
