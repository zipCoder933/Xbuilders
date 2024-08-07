# Smooth lighting in Chunk Mesh
By easily interpolating light values between vertices, we not only get smooth lighting, but also ambient occlusion and flat lighting. (see  https://youtu.be/ouNONhk3tnw?t=65&feature=shared)



## Details
if the vertex sampled all 8 voxel positions, it would result in the model picking up its own light values which would usually be 0. this is not good
[<img src="images/s1.png" width="250"/>](image.png)


to solve this, we only sample the 4 light values that are parallel with the face normal instead.

[<img src="images/s2.png" width="250"/>](image.png)

This way, we not only get smooth lighting but we get flat shading and ambient occlusion for free!

To see how all that works, observe the photo: note how the corners are dark but that does not effect neighboring faces.

[<img src="images/s4.png" width="250"/>](image.png)

Observe in the below image:
Based on the orientation of the face (shown in yellow), we retrieve the 4 voxels (outlined in black) touching the vertex (red) and then we average all 4 voxel light values together.

[<img src="images/s3.png" width="250"/>](image.png)

## Possible naïve or greedy optimizations:
* If a block is transparent, we could just return the light value within it
    * Probs not a good idea. Although it would save compute, I would miss out on ambient occlusion for all transparent blocks
	
## Implementation in naïve meshing solutions:
1. We could get the interpolated values for each corner of a voxel as if it was a perfect cube and then interpolate between those values
2. We could calculate the interpolated values for each face as if it was a perfect cube and assign the values to each face of our model that has the same normal as the calculated faces
	
## Implementation in greedy meshing 
1. For each greedy quad, we will have to get 9 light values instead of 1.
2. We can pack these nine 4 bit values into a 36 bit number and only merge faces if the 2 numbers are the same
3. When we render the quad, we unpack the light values and interpolate them for each vertex
	* NOTE: to save memory and time, it might be better to interpolate the light values for each vertex ahead of time. That means we only have 4 values to remember when comparing quads, saving memory

### Memory
* Our current Packed light with a single torch channel uses up 8 bits = 32 bits for our light
* if we had rgb lighting, it would be 16 bits = 64 bits for light

Should to separate the light mask from the block ID mask?
* with both in the same mask, we run into. 2 problems:
	1. 16 for block id + 32 for light = 48. that means we have 16 bits left over if we use a long data type for the mask
	2. if we use RGB light, the entire 4 light values use 16 * 4 = 64 bits. (1 long)
* However, when separating the lightmaps, we have to take these things into 
 	1. Whenever we compare 2 parts of the mask to check if they are equal, we also need to check if the lightmap parts are equal as well, otherwise we end up with faces that merge only by voxel and not by light
  		2. We can create a method that compares 2 indexes and than compare both in that method
