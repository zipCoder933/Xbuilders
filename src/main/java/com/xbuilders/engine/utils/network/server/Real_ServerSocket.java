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
public class Real_ServerSocket extends Base_ServerSocket {

    ServerSocket serverSocket;

    public Real_ServerSocket(int port) throws IOException {
        super(port);
        serverSocket = new ServerSocket(port);
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
