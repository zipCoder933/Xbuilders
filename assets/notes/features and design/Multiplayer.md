## block multiplayer

## Automated processes

* block events are automated processes.
* What happens when the player moves out of reach ?
* If we were playing locally, we would end the event
* We change ownership to the next closest player
* If no players are near, end the event
* The process is only transmitted by the player who owns it

⠀
## 

## ENTITY MOVEMENT:

Each player sends their coordinates to each other. And simultaneously receives data to update their own processes

* Only the items that are interacting with said player are sent to neighboring players (i.e. in view, within range, or not occluded)
* We take turns sending data every n seconds. We go player A, than B than C and back to A in sequential order

⠀
## 

## Live propagation

* Lets just send nodes like we would if they were set by the player
* (thats it. The only important thing is that we only send blocks that changed for us)
* This will be similar to entity movement in that its a race condition and we can have equal load distribution
* ALL propagation information should be encoded in the voxels.
* We might need to merge water propagation with block events somehow if we added red-stone

⠀
# Sending voxel changes

* The server has a different list of changes for each player. When that player has seen the changes, the item in their list is erased
  * If the a player is out of range of some changes, check every 1s to see if any changes could be sent to that player
* When the player leaves, (and every X seconds), we send ALL changes
  * This is to prevent changes from being forgotten by other players because they never saw them
* If we receive block data that is out of bounds, we store it in a cache to be loaded when we approach those changes

⠀
* We will send the changed blocks that go through the pipeline to the server
  * If a block was sent by another user, dont start off other effects unless we know that they can’t create a infinite loop
  * Only send block changes that actually changed
  * If we send just the block changes, a large amount of work has to be done for other players loading those changes (99.9% of that work is sunlight propagation)
    * Start by checking to see how long depropagation takes
    * Decide if we should send chunks over if that would work better
  * There seems to be a performance overhead now that we are trying to prevent the host from missing information
* Should Instantaneous effects be sent from the player that set a block?
  * Light?
    * No
  * Instantaneous block events?
    * Yes
    * some instantaneous effects should be ok as long as they only notify the others if a block change has actually been made

⠀
## Voxel data message syntax

B XXXX YYYY ZZZZ II DDD

B XXXX YYYY ZZZZ II DDD

B XXXX YYYY ZZZZ II DDD

|   B   |   The initial byte  |
|---|---|
|   X,Y,Z  |    The world coordinates  |
|   D   |   The block data  |
|   I  |   The block ID  |

What If we are just sending the block data?

* Answer: you don’t just send block data.

⠀
# Joining the world

1. When we join a world, we create a world that can get overridden when we join the game. If we want to contribute to the world, it has to be online
   1. We name the world something unique
   2. Hide the play buttons
   3. prevent the joined players from saving the world, otherwise the other players might make changes that the host doesn’t know about and that could cause problems.
      1. The joined player might cause changes that don’t get overridden

⠀
1. If the host leaves, everyone else must leave too. The host cant be missing out on changes from other players
2. For players that already have a copy of the world:
   1. we only have to send the chunks from the host that were modified after the last multiplayer game was hosted.

⠀
## Copying worlds between computers

1. If we just want a copy of the world for ourselves, we can make a copy of the world
2. This also requires renaming feature

⠀
## We can’t fix conflicts for a major reason:

• If player A joined and than player B joined with changes, now player B has a world that is different from player A

• The host can’t even update the changes because it is already loaded