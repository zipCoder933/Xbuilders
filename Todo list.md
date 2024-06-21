# Optimizing XBuilders 2
In order to use Xbuilders 3, the following criteria must be met
* World flythrough stutter Isnt a problem
* There is An FPS problem but Im pretty sure its not related to the mesh rendering

* Would it be faster to

   * XB3	fix the bugs in the world block setter
      * Will be harder but I dont think it will be as hard as fixing opengl errors
   * XB3	add block tools,
      * Should be fairly easy since I have all the structure set up
   * XB3	Add minecarts, vehicles, etc as well as migrate data to XB3
      * XB2 would be easier than this, but I can include XB3 as a different program
   
Than to
   * XB2	Figure out how to fix JOGL Opengl errors
   * XB2	Fix any crashes, put mesh handling on main thread

# Todo list
1. FIX BLOCK PIPELINE BUGS FIRST!!!
   * **Play the game TO catch errors that appear**
   * Fix the listed bugs
2. Implement occlusion culling
   * FPS and chunk traveling performance isnt as big of a concern as I thought. Plus it is a lot better than xbuilders 2, so I think i should just leave this issue alone
4. Make a launcher that can launch XB2 or XB3
5. Fix any bugs that arise