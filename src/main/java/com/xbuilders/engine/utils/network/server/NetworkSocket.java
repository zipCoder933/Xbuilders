/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zipCoder933
 */
public class NetworkSocket {

    public NetworkSocket init(Socket socket) throws IOException {
        this.socket = socket;
        this.fakeHostAddress = null;
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        buffOutputStream = new BufferedOutputStream(outputStream);
        return this;
    }

    public NetworkSocket init(InetSocketAddress addr) throws IOException {
        this.socket = new Socket();
        socket.connect(addr);
        this.fakeHostAddress = null;
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        buffOutputStream = new BufferedOutputStream(outputStream);
        return this;
    }

    private Socket socket;
    protected long lastPing;

    public long getMsSinceLastPing() {
        return System.currentTimeMillis() - lastPing;
    }

    public int getSecSinceLastPing() {
        return (int) ((double) System.currentTimeMillis() - (double) lastPing / 1000);
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Here are a few reasons why using a BufferedOutputStream can be beneficial for performance:
     * <p>
     * Reduced System Calls: BufferedOutputStream reduces the number of system calls by buffering data in memory before writing it to the underlying output stream. This can be more efficient, especially when dealing with small writes.
     * Batch Writing: BufferedOutputStream allows you to batch multiple writes together before flushing the data to the underlying stream. This can improve performance by reducing the overhead of individual write operations.
     * Controlled Flushing: You have control over when the data is actually written to the underlying stream by explicitly calling flush() or when the buffer is full.
     * In general, if you are writing small chunks of data frequently, wrapping a DataOutputStream in a BufferedOutputStream can provide performance benefits.
     */
    public BufferedOutputStream buffOutputStream;
    public DataOutputStream outputStream;
    public DataInputStream inputStream;
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
        /**
         * While it's not strictly required in all cases,
         * especially if you have a fixed-size message
         * format or are using a protocol that handles
         * message framing differently, sending the
         * length of the data before the actual data is a good
         * practice to ensure reliable and predictable
         * communication between the sender and receiver.
         */
        outputStream.writeInt(data.length);
        outputStream.write(data);
    }

    public void sendData(byte data) throws IOException {
        outputStream.writeInt(1);
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
