# Do what is easiest
DONT Do what makes the code perfect. Do what is most convinient
Just do the simplest solution. Code simplicity, does not matter!


## Important
- **Carefully test this release on a previous release to check to see if the performance has changed in any way**


### bugs
- The light that is erased sometimes causes an infinite loop of switching between 0 and the sun value? resulting in inproper rendering of the mesh
- The Nublada crash still exists
  - hs_err_pid4752.log
  - I set the font buffer to public and static and it didnt fix the issue
- **THE LOW FPS AT CERTAIN VIEWING ANGLES IS STILL AN ISSUE**
  - See bugs and Performance.md

### features
- Add trees into terrain
- add sphere tools
- add birds, fish and turtles
- Add player skins

# liquid propagation
* There is a list of important nodes in each propagator
* When a block in the pipeline is activated, the propigator will check if the block **or its neighbors** are the important block (water in this case)
* If so, add it to the list
* Every tick, we iterate over the whole list and propagate
  * We delete nodes that have been propagated
  * If a node is out of bounds, just delete it
  * We want to empty the list by the time we are done
    * If we did actually propagate, new nodes wil be added that will go into the next tick

We don't need a special liquid mesh. we can just use what we already have. when propagating, water is in range from 7 to 0, the height of the block in the liqid mesh is equal to its value but averaged across all 4 neighbors per vertex

