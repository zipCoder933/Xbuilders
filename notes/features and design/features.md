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

