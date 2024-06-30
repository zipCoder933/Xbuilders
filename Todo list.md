# Optimize XBuilders 2 or use Xbuilders 3?
Xbuilders 3 performance is very clearly better than xb2.

# Todo list
1. FIX BLOCK PIPELINE BUGS FIRST!!!
   * **Play the game TO catch errors that appear**
   * Fix the listed bugs
5. **Fix the saving bug where the following data does not get saved:**
   - recent changes
   - entity data (banners dont get their data saved?)
6. player must be able to jump underwater
7. add birds, fish and turtles
8. add minecarts
9. finetune animal behaviors (dogs)
10. add line, sphere and replace tools
11. **make inventory scroll faster by replacing the group with a grid of icons that change when scrolled**


## liquid propagation
* have a live propagation thread (LP) that can propagate water, fire and grass
* instead of constantly checking for nodes to propagate or depropagate, have the live propagator (LP) get notified of any relavent changes anywhere
* water can be propagated or depropagated
* we don't need a special liquid mesh. we can just use what we already have
   * FOR THE MESH: when propagating, water is in range from 7 to 0, the height of the block in the liqid mesh is equal to its value but averaged across all 4 neighbors per vertex

## simple multiplayer
1. lets start by getting all players to be able to connect and disconnect with ease
2. setup a simple chat interface and allow for basic binary messages to be easily sent and recieved

* I want to design it so that players can be connected even if in different worlds
   * move the JOIN MULTIPLAYER button into the load world page
   * handle all the text box parsing and stuff like that by the page itself. simply send a join rrquest object so that the other process doesnt have to deal with the details.
   * use the network class I already created (sinulation interface included)
