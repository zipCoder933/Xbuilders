# smooth lighting in a voxel mesh

we can interpolate per vertex between voxels to retrieve smooth light values
(see https://youtu.be/ouNONhk3tnw?t=65&feature=shared)

## A few important notes
* if the vertex sampled all 8 voxel positions, it would result in the model picking up its own light values which would usually be 0. this is not good
    * to solve this, we sample the 4 light values that align with the face normal instead.
        * This way, we not only get smooth lighting but we get flat shading and ambient occlusion for free!

