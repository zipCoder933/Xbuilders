# smooth lighting in mesh
By easily interpolating light values between vertices, we not only get smooth lighting, but also ambient occlusion and flat lighting. (see  https://youtu.be/ouNONhk3tnw?t=65&feature=shared)


## A few important notes
* if the vertex sampled all 8 voxel positions, it would result in the model picking up its own light values which would usually be 0. this is not good
* to solve this, we only sample the 4 light values that are parallel with the face normal instead.
	* This way, we not only get smooth lighting but we get flat shading and ambient occlusion for free!
	* To see how all that works, observe the photo: note how the corners are dark but that does not effect neighboring faces.
	
	
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
	* NOTE: to save memory and time, it might be better to interpolate the light values for each vertex ahead of time. That means we only have 4 values to remember when comparing quads, making a 16 bit number instead of 36
