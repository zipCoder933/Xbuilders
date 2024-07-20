# Do what is easiest
DONT Do what makes the code perfect. Do what is most convinient
Just do the simplest solution. Code simplicity, does not matter!


## Important
- **Carefully test this release on a previous release to check to see if the performance has changed in any way**


### bugs
- The Nublada crash still exists
- **THE LOW FPS AT CERTAIN VIEWING ANGLES IS STILL AN ISSUE**
  - See bugs and Performance.md
- FIX BLOCK PIPELINE BUGS
   * **Play the game TO catch errors that appear**
   * Fix the listed bugs
- Fix entity collison objects being "soft" when they are stacked on top of each other
- Players can "climb" up walls by holding against them

## Multiplayer
1. Determine any performance issues in multiplayer
2. Optimize sunlight updating
3. Decide if we need to send block data as chunks if there are lots of changes
4. Update entities

### features
- add line, sphere and replace tools
- add block tools menu
- add birds, fish and turtles
- add a system to handle live events (do after multiplayer)
  - add liquid propagation with block data
  - make a special block type for liquid that has certex height depending on block data

# liquid propagation
* have a live propagation thread (LP) that can propagate water, fire and grass
* instead of constantly checking for nodes to propagate or depropagate, have the live propagator (LP) get notified of any relavent changes anywhere
* water can be propagated or depropagated
* we don't need a special liquid mesh. we can just use what we already have
   * FOR THE MESH: when propagating, water is in range from 7 to 0, the height of the block in the liqid mesh is equal to its value but averaged across all 4 neighbors per vertex

