# Blocks, Entities Tools and Items

they need to be reworked to be simpler and to provide the proper functionality

---------------------------------------------------------------------

# Blocks and Entities

All blocks and entities have

- a unique numerical ID (-32,000 to 32,000) (optional for entities)
    - If an entity does not have an ID, it will not be saved in the world
- The id of the **item** that is used to place the block/entity (optional)

### Entities

* Entities dont have an entitylink anymore.
    * Entities containing shared data (animal breeds, etc) can have that data stored as static objects in the class

entities have builtin properties

- position
- direction
- velocity
- health
- max health
- is alive

There are 2 classes of entities. Moving entities and static entities.

* Static entitites (Tile entities) are extactly the same as regular entities. They don't move but are still rendered and
  saved in the world
* Moving entities are the same as static entities but they move.
    * Position handler

### entity simulation (updating)
Entities need to update themselves every tick (https://minecraft.fandom.com/wiki/Tick)
Should they update on the rendering thread, or on a separate thread?
**Entities outside of the simulation distance dont need to be updated or rendered**


### Blocks

blocks have builtin properties

- solid
- opaque
- bouncyness
- hardness
- friction
- light level when setting
- click event (method)
    - TNT is the only block that has a click event that runs on another thread, but in the future, ALL events must
      happen instantaneously
- create event (method)
- destroy event (method)
- collision event (method)

#### block simulation (updating)
They have a random tick event that is called every tick at random (https://minecraft.fandom.com/wiki/Tick)
* This event allows plants to grow, fire to spread, blocks to update their state, etc.
* TODO: See if you can replace the live propagation threads with the tick event




## Mixins for entities and blocks

To implement custom properties like weight across all blocks in Minecraft, including vanilla blocks and blocks from
other mods, mod developers typically use a combination of reflection, mixins, or data packs. Here’s how they can achieve
this:

Mixins allow mods to modify existing classes and add additional functionality or properties without directly altering
the original code.
A mod can create a mixin for the Block class to add a weight property to all blocks globally.
This approach injects custom behavior into all blocks, allowing for universal access to the new property.

Example Mixin Implementation:

```java

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow
    private Material material;

    private float weight;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        // Set a default weight or calculate based on the material.
        this.weight = calculateWeight(material);
    }

    public float getWeight() {
        return weight;
    }
}
```

### String IDs instead of numerical IDs

XBuilders currently uses numerical ID's but String IDs might be more appropriate in the future

#### Why Use String IDs?

- Modding and Customization: String IDs allow for unique identification across mods and resource packs. For example, a
  mod can use modname:item_name to avoid conflicts with other mods.
- Future-Proofing: With string IDs, Minecraft avoids the limitations of numerical IDs, allowing for more items, blocks,
  and entities without clashing or breaking worlds during updates.
- Data Flexibility: String-based IDs are more descriptive and readable, making data handling easier (e.g., in commands,
  NBT tags, and world files).

Since Minecraft 1.13, Minecraft uses a palette system for storing chunk block data in RAM/Disk. This approach is
designed to reduce memory usage by avoiding the need to store full string IDs for every block in a chunk.

#### How It Works:

- Each chunk (16x16x256 blocks) is divided into 16x16x16 block sections, called sub-chunks (or vertical sections). Each
  sub-chunk stores block data in a palette.
- The palette contains a mapping of block states to smaller integer IDs. Instead of storing the full block ID for each
  block (e.g., minecraft:stone), each unique block state in the section is assigned a small integer index (like 0, 1, 2,
  etc.).
- For each block in the sub-chunk, Minecraft stores only the palette index (a small integer) instead of the full block
  ID.

##### Bit-Packing:

In disk and RAM, the blocks are bit-packed when saved to disk, meaning each block in a sub-chunk is stored using only as
many bits as needed, depending on the size of the palette.
For example, if a palette only contains 4 unique block types, only 2 bits per block are needed to represent those
blocks.
This bit-packing reduces the amount of space required to store chunk data on disk.













---------------------------------------------------------------------


# Items

Items are objects that can be stored in inventories
They are used to place blocks and entities but they dont actually exist in the world

### Item:

Represents the type of an item. For example, an Item can be a sword, a pickaxe, etc.
It does not contain any metadata, quantity, or any other state-related information.

properties:

- ID (as a string)
- max durability (as a method)
- is damageable (as a method)
- is stackable (as a method)
- max stack size (as a method)

Example: Items.DIAMOND_SWORD is an Item, which represents the diamond sword itself, not its count or state.

### ItemStack:
Represents an instance of an item with specific data such as:
- quantity
- durability
- NBT (Named Binary Tag) data.

It can hold multiple items of the same type in a stack, hence the name ItemStack.
Example: An ItemStack of 5 diamond swords with specific durability values and enchantments. Each ItemStack can also be
empty or have different quantities of the same item.

### Key Differences:
Item is the base type, the blueprint of an item. It defines properties shared by all items of that type.
ItemStack is the actual "container" holding a specific amount of an item, its state, and additional metadata like
enchantments or custom names.

### About NBT data:
NBT (Named Binary Tag) data on an ItemStack can significantly affect the item's properties, depending on the item
type.
Comparison of NBT and JSON:
- Both NBT and JSON can represent complex, nested data structures.
- They both use a system of keys (or names) and corresponding values to store information.
- JSON is human-readable, while NBT, although not as easily readable in its binary format, can be represented in a similar way using text format for inspection.
- NBT is stored in a binary format, making it faster to read and write compared to the text-based format of JSON.
- NBT supports a wider variety of primitive data types (e.g., byte, short, int, long, float, double, byte array) while JSON primarily supports strings, numbers, booleans, arrays, and objects.
- NBT is generally more compact than JSON when serialized as binary, which makes it more efficient for storage and loading in Minecraft.