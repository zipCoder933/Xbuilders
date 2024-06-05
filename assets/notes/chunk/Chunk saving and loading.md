# CHUNK FILE INFORMATION
The chunk file is formatted as follows:
10 bytes of metadata, voxels, entities

## voxels
 * An example of how voxel is written:     ```voxel light  id  id  blockData...  \n```
 * Air is written like:                     ```voxel light id id \n```

## entities
 * An example of how entity is written:     ```entity id id x x y y z z  data... \n```

(The voxel and entity bit at the beginning  of the line is used to prevent the first byte of the id from being confused with newline byte)

# Future optimizations