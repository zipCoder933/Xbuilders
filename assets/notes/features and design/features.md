# Information about features
## GAME BLOCK TOOLS
Here is a comprehensive list of all of the tools designed to make it faster to build:
* The block mode needs to be fast to change while also having as many features as possible
* Block mode tools and tool properties can be viewed via a dropdown menu by pressing Q

### tool parameters
A block tool has parameters. These can be toggled via hotkeys or using the dropdown menu

All block tools that use a block boudnary have 2 other parameters
* An option to toggle planar mode (make the boundary flat in X, Y or Z)
* An option to toggle the positioning of the endpoint to sit on ray hit, or ray hit+normal

## BLOCK EVENTS
* I started by allowing block types to choose how they want to exist. Certain conditions around the block would make it not allowed to be there
  * make a way for events to "shutdown" safely
  * find a way to intelligently execute block events on other threads
* when too many block events are called, it pauses the frame.
  * a solution would to be to shuttle all remaining events after the threshold limit per frame is set to a thread pool
    * I might also need to cancel/limit block set and local change events when doing it in bulk, as it would slow things down too

## WATER/FIRE/GRASS PROPAGATION WITH CELLULAR AUTOMATA
* we can use cellular autonoma interface so that the usage is essentially an abstraction of what is really going on

on a separate thread
- find a bunch of nodes to apply cellular autonoma on
- propagate using a BFS
- only the nodes that apply to said propagation process will survive
  - keep the bfs nodes separate for each process, this would also help them run at different speeds while also improving performance
- these nodes keep propagating so that we dont have to index them on each step
- if the distance from the node starting position is beyond a specific distance, index new nodes but keep the old ones
  - a node will be deleted if it is too far from player
- grass, that is dependent on the amount of time since placed, can store a timestamp in its block data?
