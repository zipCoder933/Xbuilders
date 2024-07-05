/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

import com.xbuilders.engine.player.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

/**
 *
 * @author zipCoder933
 */
public class NetworkSocket {

    private void init() throws IOException {
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    public Player player;
    private Socket socket;
    public DataOutputStream outputStream;
    public DataInputStream inputStream;

    public NetworkSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.fakeHostAddress = null;
        init();
    }

    public NetworkSocket(InetSocketAddress addr) throws IOException {
        this.socket = new Socket();
        socket.connect(addr);
        this.fakeHostAddress = null;
        init();
    }

    private String fakeHostAddress;
    private InetSocketAddress fakeRemoteAdress;

    public void setFakeHostAndRemoteAddress(String fakeHostAddress, String fakeRemoteAddress) {
        this.fakeHostAddress = fakeHostAddress;
        InetSocketAddress addr2 = (InetSocketAddress) socket.getRemoteSocketAddress();
        fakeRemoteAdress = new InetSocketAddress(fakeRemoteAddress, addr2.getPort());
    }

    public String getHostAddress() {
        return fakeHostAddress != null ? fakeHostAddress : socket.getInetAddress().getHostAddress();
    }

    public String getRemoteHostString() {
        return getRemoteSocketAddress().getHostString();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return fakeRemoteAdress != null
                ? fakeRemoteAdress
                : (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NetworkSocket other = (NetworkSocket) obj;
        return this.getHostAddress().equals(other.getHostAddress())
                && this.getRemoteHostString().equals(other.getRemoteHostString())
                && this.socket.getPort() == other.socket.getPort();
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public byte[] receiveData() throws IOException {
        int length = inputStream.readInt();
        byte[] data = new byte[length];
        inputStream.readFully(data);
        return data;
    }

    public void sendData(byte[] data) throws IOException {
        outputStream.writeInt(data.length);
        outputStream.write(data);
    }

    public void sendString(String str) throws IOException {
        byte[] data = str.getBytes();
        outputStream.writeInt(data.length);
        outputStream.write(data);
    }

    @Override
    public String toString() {
        return "NetworkSocket{"
                + (fakeRemoteAdress != null ? "fakeHost=" + fakeHostAddress : "")
                + (fakeRemoteAdress != null ? ", fakeRemoteAddr=" + fakeRemoteAdress.getHostString() : "")
                + ", socket=" + socket + '}';
    }

}
