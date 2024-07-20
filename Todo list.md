### DONT Do what makes the code perfect. Do what is most convinient
Just do the simplest solution. Code simplicity, does not matter!

## Multiplayer fixes
1. Determine any performance issues in multiplayer
2. Optimize sunlight updating
3. Decide if we need to send block data as chunks if there are lots of changes

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
- ** Fix the Nuklear crash**
  - carefully understand what the real problem is
- there is a bug where sometimes torchlight isnt erased when a block is placed over it to hide it all up
- there is a bug where sometimes a chunk wont update one of its sides (in terrain gen and regular block setting)

### features
- add line, sphere and replace tools
- Add trees into terrian
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

