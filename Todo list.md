# Do what is easiest
DONT Do what makes the code perfect. Do what is most convinient
Just do the simplest solution. Code simplicity, does not matter!

## Notes
- As far as i can tell, there is no performance overhead from entity positon/state sharing
- **SUPER IMPORTANT:** Optimize liquid mesh by using the greedy mesher whenever possible!!!

### bugs
- The light that is erased sometimes causes an infinite loop of switching between 0 and the sun value? resulting in inproper rendering of the mesh
- **THE LOW FPS AT CERTAIN VIEWING ANGLES IS STILL AN ISSUE**
  - IMPORTANT: Check to make sure performance mode is enabled when testing game performance
  - **Carefully test this release on a previous release to check to see if the performance has changed in any way**
  - See bugs and Performance.md

### features
- Add beavers
- Add trees into terrain
- add sphere tools
- add birds, fish and turtles
- Add player skins
- Add world skybox and add day/night
- Improve liquid propagation to allow for de-propagation

