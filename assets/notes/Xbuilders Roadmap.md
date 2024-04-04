* Block events
    * I started by allowing block types to choose how they want to exist. Certain conditions around the block would make it not allowed to be there
    * TODO:Track setting events and local change events
        * Set all track blocks to class TrackPiece
    * TODO:Plant growth
        * block events should run in their own thread unless they are fast
            * make a way for events to "shutdown" safely

    * if a block local change or set event creates a chain reaction, (test this)
        * this issue would happen if in said method, the block made a change to another that caused it to fire said event on someone else
            * a solution is to prevent local change events from running in blocks if the block event was added via a local change event
                * the easiest way to do this is to have one player.setBlock() method for local change events and one without that
                    * this would be fine assuming block events only use that method
            * (probbably better:) another solution is to prevent block events from running more than X frames in a row
                * after the Nth frame of event actions, make the next frame not have actions to stop the domino effect

Todo list:
* Add bulk block setting like in xb2
* Allow user to climb ladder
    * To do this, we need to make a way for block types to be labeled as "climbable"
* Add water propagation, fire propagation, and grass propagation
* add special mesh for liquids
* add doors and trapdoors as block types with click events

Entities
* Add all animal entities
* add all vehicle entities

light:
* add torch and lamp block types
    * add torchlight
* save sunlight and torchlight?
* add smooth mesh lighting
