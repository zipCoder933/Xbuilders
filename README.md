![Xbuilders landscape](assets/images/b.jpg)

# Xbuilders 3
**A voxel game written in Java + LWJGL.**

This game was created from the ground up as a replacement for Xbuilders 2.

## Important notes
* The JVM version must be 17. If it is higher the following message will show in output:
  * `[LWJGL] [ThreadLocalUtil] Unsupported JNI version detected, this may result in a crash. Please inform LWJGL developers.`
* I use LWJGL's Nublada GUI library that is builtin to LWJGL to do all of the UI rendering.
* Each chunk is 32x32x32 in size. Chunks coordinates are 3D
* The up direction is -Y, and the down direction is +Y
* Textures are sourced from Pixel perfection along with a few other open source minetest texture packs. Additionally, I have handcrafted a few of my own textures as well.

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
