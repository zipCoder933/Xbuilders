# Do what is easiest
DON'T Do what makes the code perfect. Do what is most convinient
Just do the simplest solution. Code simplicity, does not matter!

### bugs
- The light that is erased sometimes causes an infinite loop of switching between 0 and the sun value? resulting in inproper rendering of the mesh
- **THE LOW FPS AT CERTAIN VIEWING ANGLES IS STILL AN ISSUE**
  - IMPORTANT: Check to make sure performance mode is enabled when testing game performance
  - **Carefully test this release on a previous release to check to see if the performance has changed in any way**
  - See bugs and Performance.md

### features
- add birds
- Add fire
- add waypoints
- Add bouncy "substances"
- Add player skins
- Add world skybox
- add day/night

## Performance
- As far as i can tell, there is no performance overhead from entity positon/state sharing

### Is there a performance overhead from using try/catch too many times?
https://stackoverflow.com/questions/16451777/is-it-expensive-to-use-try-catch-blocks-even-if-an-exception-is-never-thrown/18938171#18938171
https://itexpertly.com/does-try-catch-affect-performance-in-java/
https://stackoverflow.com/questions/10169671/java-overhead-of-entering-using-try-catch-blocks

**there is no significant difference** between using/not using a try-catch block, with or without throwing exception in a block or not.
The only performance overhead comes when an exception is actually thrown