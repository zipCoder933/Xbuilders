# Important Notes about optimizations
* https://cell-auto.com/optimisation/
* https://www.toptal.com/full-stack/code-optimization

## Summary:
### Optimisation should be the last thing you do.
* Get your program to work properly first, and then profile it to see if it needs further work.
* optimization should always be saved for THE VERY LAST! doing it too early causes you to waste time optimizing things that may not even need optimization, at the expense of time and maintainability.
### make programatic benchmarks and testing algorithms to test for bugs and bottlenecks.
* trying to guess what the bottleneck really is, rarely works
* "profile your code - and concentrate your efforts where they can do some good;"

## From www.toptal.com/full-stack/code-optimization
Let’s start by listening to the advice of the sages as we explore together Jackson’s famous code optimization rules:
1. Don’t do it.
2. (For experts only!) Don’t do it yet.

### But We Still Haven’t Optimized!
Now that your algorithm is implemented, and you have proven its output to be correct, congratulations! You have a baseline!

Finally, it’s time to… optimize, right? Nope, still Don’t do it yet. It’s time to take your baseline and do a nice benchmark. Set a threshold for your expectations around this and stick it in your test suite. Then if something suddenly makes this code slower—even if it still works—you’ll know before it goes out the door.

Still hold off on optimization, until you have a whole piece of the relevant user experience implemented. Until that point, you may be targeting an entirely different part of the code than you need to.

Go finish your app (or component), if you haven’t already, setting all your algorithmic benchmark baselines as you go.

Once this is done, this is a great time to create and benchmark end-to-end tests covering the most common real-world usage scenarios of your system.

Maybe you’ll find that everything is fine.

Or maybe you’ve determined that, in its real-life context, something is too slow or takes too much memory.

OK, Now You Can Optimize

There’s only one way to be objective about it. It’s time to break out flame graphs and other profiling tools. Experienced engineers may or may not guess better more often than novices, but that’s not the point: The only way to know for sure is to profile. This is always the first thing to do in the process of optimizing code for performance.

You can profile during a given end-to-end test to get at what will really make the largest impact. (And later, after deploying, monitoring usage patterns is a great way to stay on top of which aspects of your system are the most relevant to measure in the future.)

Note that you are not trying to use the profiler right to its full depth—you’re looking more for function-level profiling than statement-level profiling, generally, because your goal at this point is only to find out which algorithm is the bottleneck.

**Now that you’ve used profiling to identify your system’s bottleneck, now you can actually attempt to optimize, confident that your optimization is worth doing. You can also prove how effective (or ineffective) your attempt was, thanks to those baseline benchmarks you did along the way.**



## www.cell-auto.com/optimisation/
We should forget about small efficiencies, say about 97% of the time: premature optimization is the root of all evil.
- Donald Knuth

I'm sure you've heard this before, but I have to say it again.
**Optimisation is probably the last thing you should be doing.** Get your program to work properly first, and then profile it, and see if it needs further work.

### The Pareto Principal
This is often known as the 80/20 rule. Some think that should be called the 90/10 rule.
There are various statements of it that relate to computer programming:

* 80% of the bugs lie in 20% of the code;
* 80% of the budget is spent on 20% of the project;
* 80% of the code is written by 20% of the programmers;

...I'm sure you get the idea.
In the context of optimisation we have:
* 80% of the time is spent in 20% of the code;
    * Moral: profile your code - and concentrate your efforts where they can do some good;
* 80% of the improvement comes in with the first 20% of the effort;
    * Moral: give up early - optimisation can rapidly run into diminishing returns;

### Maintenance
Optimisation is the sworn enemy of maintainable, comprehensiblle code. In some cases these aims coincide - but this occurs more by chance than anything else. If you think you (or anyone else) is going to want to maintain, modify or understand your code in the future, don't optimise it too far.

### Meddling
Optimisation can introduce bugs that were not present before. To a first approximation, if your program works, leave it well alone.

### Optimising as-you-go
The general rule of thumb is don't optimise as you go along.
You might think this will avoid a costly rewrite when you find the program does not perform - but in practice optimisation can be applied more effectively and efficiently to an existing working program.

It's harder to predict where the bottlenecks are going to be than it is to use tools to measure what the code is doing once it has been written.

There is sometimes a place for optimising as you go - but it is usually not a good idea.

### Proverbs
* The best is the enemy of the good.
* Working toward perfection may prevent completion.
* Complete it first, then perfect it.
* The part that needs to be perfect is usually small.




# Chunk generation performance on the rendering thread:
TLDR: When flying fast through the world, and with a large render distance, the game becomes choppy. This is closely associated with the memory usage going up and down over and over again rapidly. After experimentation, I discovered that the memory bottleneck was the creation of int buffers to be sent to the GPU after the chunk mesh was created.


* the garbage collector is playing a big role in performance issues.
    * I think the GC is the cause of the choppiness that occurs when driving thru big worlds.
    * This is definitely true, when the GC is set to 8GB, there is choppiness that occurs when the GC cleans out, this especially happens when GC is cleaned manually.

# Memory optimizations
READ THIS FIRST!
https://stackoverflow.com/questions/28615939/what-are-some-best-practices-to-build-memory-efficient-java-applications#:~:text=1%20Layout%20data%20structures%20in%20%28parallel%29%20arrays%20of,objects%2C%20use%20object%20pools%2C%20ThreadLocals%205%20Go%20off-heap

See my onenote page on bytebuffers

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






# Major Game optimizations
https://youtu.be/40JzyaOYJeY?feature=shared
https://youtu.be/5zlfJW2VGLM?feature=shared

## Summary
* Reducing memory is a major optimization and does increase fps
    * Pack all vertex data as much as possible
* Reduce the amount of triangles drawn
    * Use greedy meshing
* https://youtu.be/hf27qsQPRLQ?feature=shared
    * When drawing distant chunks, the FPS can lag. Not because there are too many triangles, but because small triangles can bottleneck the GPU
        * To solve this, use LOD to prevent small triangles.
* Give the GPU a list of all meshes and ask it to draw all of them (render all meshes with only 1 draw call)
    * Use one long buffer to store the data for all chunks and give this buffer to the GPU. This significantly reduces the amount of communication between the CPU and GPU
    * We also need to tell the GPU where we want each mesh to be drawn, see can use SSBOs for that
* https://youtu.be/YNFaOnhaaso?feature=shared
    * It takes 2-3 frames for a new mesh to be sent to the GPU. You can use a special optimization to keep rendering the existing mesh until the new mesh is ready. This prevents fps lag
