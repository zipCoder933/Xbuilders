/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 *
 * @author zipCoder933
 */
abstract class Base_ServerSocket {

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    private int port;

    public Base_ServerSocket(int port) throws IOException {
        if (port < 1024) {
            throw new IOException("Port number must be higher than 1024");
        }
        this.port = port;
    }

    public abstract NetworkSocket accept() throws IOException;

    public abstract NetworkSocket addConnection(InetSocketAddress address) throws IOException;

    public abstract boolean isClosed();

    public abstract void close() throws IOException;
}
