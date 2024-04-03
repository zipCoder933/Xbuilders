# Chunk generation performance on the rendering thread:
TLDR: When flying fast through the world, and with a large render distance, the game becomes choppy. This is closely associated with the memory usage going up and down over and over again rapidly. After experimentation, I discovered that the memory bottleneck was the creation of int buffers to be sent to the GPU after the chunk mesh was created.


* the garbage collector is playing a big role in performance issues.
    * I think the GC is the cause of the choppiness that occurs when driving thru big worlds.
    * This is definitely true, when the GC is set to 8GB, there is choppiness that occurs when the GC cleans out, this especially happens when GC is cleaned manually.

## What could be the cause of the memory stacking up?
* Its not the greedy mesher. The mesher only uses about 11kb per compute
    * Deduce why basic terrain uses significantly less memory than complex (default) terrain
* the buffer set is the memory bottleneck
    * The arraylist creation of vertices is so minor that It is insignificant.
    * The real bottleneck is the constant creation of intBuffers, to be sent to the GPU

## How to optimize int buffers that must be constantly created and we don’t know what size they are beforehand?
When you're dealing with unknown or dynamic sizes for data that needs to be sent to the GPU, you can still optimize memory usage with the following strategies:
* Over-Allocation: Allocate a buffer that is larger than what you typically need, and then only fill up as much as required. This can reduce the need for re-allocation if the size varies within a known range.
* Dynamic Resizing: Start with a buffer of a certain size, and if you find that it's not large enough, double the size. This follows a similar approach to what ArrayList does in Java when it needs to grow.
* <s>Buffer Sub-Data: Allocate a large buffer up front and then use glBufferSubData (in OpenGL) for updating parts of the buffer with new mesh data as it becomes available. This way, you can avoid reallocating the entire buffer when new data comes in.</s>
    * This only allows you to update a subset of the VBO, NOT update a subset of the int buffer
* Memory-Mapped Buffers: In some cases, memory-mapped buffers can be used to efficiently handle large or dynamic data sets.
    * A memory mapped buffer is just a shared piece of memory between the CPU and GPU
    * Object Pooling: Implement an object pool that dynamically adjusts the size of the buffers based on the demands of the application. Unused buffers can be kept around for a while in case they are needed again soon.
    * Buffer Orphaning: This is a technique where you essentially tell the GPU to disregard the old buffer and you allocate a new one. It can be done using glBufferData with a null data pointer before updating the buffer with new data. This can sometimes be faster than updating the buffer in place and can avoid stalling the graphics pipeline.
    * Streaming VBOs: Use streaming vertex buffer objects (VBOs) for dynamic data where the buffer contents are updated frequently.

### Using resizable int intBuffer to store vertex data to be sent to the GPU
* <b>I tried this using resizable int array and resizable int buffer. the memory usage is not any better than the old way. the memory still yo-yo's up and down like before.</b>
* I still dont understand the role of buffer.flip and buffer.clear, there are many aspects to this that will help in making the buffer more stable. For example, I Fixed it now, but before, the JVM would crash if i changed the size of the buffer using memory.realloc
