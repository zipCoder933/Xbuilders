### DONT Do what makes the code perfect. Do what is most convinient
There is a balance, but when you spend too much time making the inside of the code perfect, you waste time unnecisarrily.
**All that matters is that the code works!!!**

### bugs 
- FIX BLOCK PIPELINE BUGS
   * **Play the game TO catch errors that appear**
   * Fix the listed bugs
- Fix entity collison objects being "soft" when they are stacked on top of each other
- crashes from NKconvert are still an issue
  - follow up with the bug report

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

# Multiplayer
## Connection
We can use the same IP adresses with different ports to connect 2 servers on the same computer!
(See the test/networkTester file for an example)
1. lets start by getting all players to be able to connect and disconnect with ease
2. setup a simple chat interface and allow for basic binary messages to be easily sent and recieved

## Sending voxel data
all threaded block events are automated processes. They have ownership and are shared by the player closest to it at the current moment
We already would need this for water or red stone etc

### Solution 1: send block changes as they get fed through the block pipeline
* Multithreaded effects should be shared automations 
  * Should Instantaneous effects be shared or not? 
  * Light? 
    * No 
  * Instantaneous block events? 
    * Yes


### Solution 2: send every voxel change at the base level
* In this case we would definitely need to batch voxel changes together
* Sending all that data might cause overhead? Not sure.
* There might be edge cases where 2 players set a block with events in the same place at the same time
* we have to ask if a change should be shared or not at every voxel change

