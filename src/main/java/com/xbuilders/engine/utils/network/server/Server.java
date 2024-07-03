/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

/**
 * @author zipCoder933
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public abstract class Server {

    protected String ipAdress;
    public final ArrayList<NetworkSocket> clients;
    public XBServerSocket socket;
    Thread clientThread;

    public Server() {
        super();
        this.clients = new ArrayList<>();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.ipAdress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            this.ipAdress = null;
        }

    }


    public void startServer(int port) throws IOException {
        socket = new XBServerSocket(port);
        clientThread = new Thread() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    try {
                        NetworkSocket newClient = socket.accept();
                        if (newClient(newClient)) {
                            addClient(newClient);
                        } else {
                            newClient.close();
                        }
                    } catch (SocketException | java.io.EOFException ex) {
                        //if there is a socket exception, it is most likely because the socket was disconnected
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        clientThread.start();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return socket.getPort();
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void onClientDisconnect(NetworkSocket client) {
        System.out.println("Disconnected: " + client.toString());
    }

    public boolean clientAlreadyJoined(String targetHost, String remoteHost) {
        for (NetworkSocket client : clients) {
            if (NetworkUtils.hasSameConnection(
                    client.getHostAddress(), client.getRemoteHostString(),
                    targetHost, remoteHost
            )) {
                return true;
            }
        }
        return false;
    }

    public void sendToAllClients(byte[] data) throws IOException {
        for (NetworkSocket client : clients) {
            client.sendData(data);
        }
    }

    public void close() throws IOException {
        for (NetworkSocket client : clients) {
            client.close();
        }
        clients.clear();
        clientThread.interrupt();
        clientThread = null;
        socket.close();
    }

    public boolean clientAlreadyJoined(NetworkSocket newClient) {
        return clientAlreadyJoined(newClient.getHostAddress(), newClient.getRemoteHostString());
    }

    /**
     * The client is only added to our client-list if we return true, otherwise
     * the client gets disconnected.
     *
     * @param newClient the client
     * @return if the server should accept the client.
     */
    public abstract boolean newClient(NetworkSocket newClient);

    /**
     *
     * @param client the client
     * @param receivedData incoming bytes from the client
     */
    public abstract void recieveDataFromClient(NetworkSocket client, byte[] receivedData);


    protected void addClient(NetworkSocket newClient) {
        Thread clientThread = new Thread(() -> handleClient(newClient));
        clientThread.setPriority(1);
        clientThread.start();
        clients.add(newClient);
    }

    public NetworkSocket addClient(InetSocketAddress address) throws IOException {
        NetworkSocket newClient = socket.addConnection(address);
        addClient(newClient);
        return newClient;
    }

    protected void handleClient(NetworkSocket client) {
        try {
            while (!client.isClosed()) {
                // Assuming you have a method like receiveData() to receiveData messages from the client
                byte[] receivedData = client.receiveData();
                // Process the received data using the provided input handler
                recieveDataFromClient(client, receivedData);
            }
        } catch (SocketException | java.io.EOFException ex) {
            //if there is a socket exception, it is most likely because the socket was disconnected
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        clients.remove(client);
        onClientDisconnect(client);
    }

}