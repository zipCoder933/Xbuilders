package com.xbuilders.window;

import org.lwjgl.nuklear.NkVec2;

public interface WindowEvents {
    public void windowResizeEvent(int width, int height);

    public void keyEvent(int key, int scancode, int action, int mods);

    public void mouseButtonEvent(int button, int action, int mods) ;

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset);
}
