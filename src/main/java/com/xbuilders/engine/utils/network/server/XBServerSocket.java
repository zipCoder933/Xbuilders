/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author zipCoder933
 */
class XBServerSocket {

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    java.net.ServerSocket serverSocket;
    private int port;

    public XBServerSocket(int port) throws IOException {
        if (port < 1024) {
            throw new IOException("Port number must be higher than 1024");
        }
        this.port = port;
        serverSocket = new java.net.ServerSocket(port);
    }

    public NetworkSocket accept() throws IOException {
        return new NetworkSocket(serverSocket.accept());
    }

    public NetworkSocket addConnection(InetSocketAddress address) throws IOException {
        return new NetworkSocket(address);
    }

    public boolean isClosed() {
        return serverSocket.isClosed();
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
