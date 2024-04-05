* Add Block events of all the blocks in XB2
    * I started by allowing block types to choose how they want to exist. Certain conditions around the block would make it not allowed to be there
    * TODO:Track setting events and local change events
        * Set all track blocks to class TrackPiece
    * TODO:Plant growth
        * block events should run in their own thread unless they are fast
            * make a way for events to "shutdown" safely

* Add bulk block setting like in xb2
* Add water propagation, fire propagation, and grass propagation
* add special mesh for liquids
* add doors and trapdoors as block types with click events
* Add all animal entities
* add all vehicle entities

light:
* add torch and lamp block types
    * add torchlight
* save sunlight and torchlight?
* add smooth mesh lighting
