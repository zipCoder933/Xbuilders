# STEPS TO XB2
Generally, the only things I have left to getting to the XB2 game is:
1. torchlight
    - sun and torch saving in chunk
        - (OPTIONAL, BUT IMPORTANT): save chunks in 2x2x2 chunk reigons
2. water/fire/grass propagation with cellular autonoma
3. adding all entities, block events and terrains
    - trees
    - ADD ALL block events including TNT
    - all animals, vehicles and banners
    - adding doors, trapdoors and fence gates as blocks
4. block setting tools (copy/paste/create boundary/ spheres)
5. a conversion tool to convert xb2 worlds to xb3

## BLOCK EVENTS
* I started by allowing block types to choose how they want to exist. Certain conditions around the block would make it not allowed to be there
* TODO:Track setting events and local change events
    * Set all track blocks to class TrackPiece
* TODO:Plant growth
    * block events should run in their own thread unless they are fast
        * make a way for events to "shutdown" safely

## WATER/FIRE/GRASS PROPAGATION
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