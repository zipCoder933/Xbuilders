/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window.render;

import java.io.File;
import java.io.IOException;

/**
 * the generic shader class (built on top of shader base)
 * @author zipCoder933
 */
public class Shader extends ShaderBase {

    public Shader(File Vert, File Frag) throws IOException {
        init(Vert, Frag);
    }

    public Shader(String Vert, String Frag) throws IOException {
        init(Vert, Frag);
    }

    @Override
    public void bindAttributes() {
    }
    
    

}
