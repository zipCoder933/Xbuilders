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
import java.util.function.Supplier;

public abstract class Server<ClientSocket extends NetworkSocket> { //We can define custom network sockets

    protected String ipAdress;
    public final ArrayList<ClientSocket> clients = new ArrayList<>(); //A socket is a two way connection

    Thread clientThread;
    Supplier<ClientSocket> clientSocketSupplier;


    java.net.ServerSocket serverSocket;
    private int serverPort;


    private ClientSocket acceptClient() throws IOException { //Server accept incoming connection
        ClientSocket clientSocket = clientSocketSupplier.get();
        clientSocket.init(serverSocket.accept());
        return clientSocket;
    }

    private ClientSocket addConnection(InetSocketAddress address) throws IOException { //Server adds new connection
        ClientSocket clientSocket = clientSocketSupplier.get();
        clientSocket.init(address);
        return clientSocket;
    }

    public boolean isClosed() {
        return serverSocket.isClosed();
    }


    public Server(Supplier<ClientSocket> clientSocketSupplier) {
        super();
        this.clientSocketSupplier = clientSocketSupplier;
        init();
    }

    private void init() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.ipAdress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            this.ipAdress = null;
        }
    }


    public void start(int port) throws IOException {

        if (port < 1024) {
            throw new IOException("Port number must be higher than 1024");
        }
        this.serverPort = port;
        serverSocket = new java.net.ServerSocket(port);


        clientThread = new Thread() {
            @Override
            public void run() {
                while (serverIsOpen()) {
                    try {
                        ClientSocket newClient = (ClientSocket) acceptClient();
                        if (newClientEvent(newClient)) {
                            connectToServer(newClient);
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
        return serverPort;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void onClientDisconnect(ClientSocket client) {
        System.out.println("Disconnected: " + client.toString());
    }

    public boolean clientAlreadyJoined(String targetHost, String remoteHost) {
        for (ClientSocket client : clients) {
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
        for (ClientSocket client : clients) {
            client.sendData(data);
        }
    }

    public void close() throws IOException {

        if (serverSocket != null) serverSocket.close();
        serverSocket = null;

        if (clientThread != null) clientThread.interrupt();
        clientThread = null;

        for (ClientSocket client : clients) {
            if (client != null) client.close();
        }
        clients.clear();
    }

    public boolean serverIsOpen() {
        return serverSocket != null && !serverSocket.isClosed();
    }


    public boolean clientAlreadyJoined(ClientSocket newClient) {
        return clientAlreadyJoined(newClient.getHostAddress(), newClient.getRemoteHostString());
    }

    /**
     * The client is only added to our client-list if we return true, otherwise
     * the client gets disconnected.
     *
     * @param newClient the client
     * @return if the server should accept the client.
     */
    public abstract boolean newClientEvent(ClientSocket newClient);

    /**
     * @param client       the client
     * @param receivedData incoming bytes from the client
     */
    public abstract void dataFromClientEvent(ClientSocket client, byte[] receivedData);


    protected void connectToServer(ClientSocket newClient) {
        Thread clientThread = new Thread(() -> handleClient(newClient));
        clientThread.setPriority(1);
        clientThread.start();
        clients.add(newClient);
    }

    public ClientSocket connectToServer(InetSocketAddress address) throws IOException {
        ClientSocket newClient = (ClientSocket) addConnection(address);
        connectToServer(newClient);
        return newClient;
    }

    protected void handleClient(ClientSocket client) {
        try {
            while (!client.isClosed()) {
                // Assuming you have a method like receiveData() to receiveData messages from the client
                byte[] receivedData = client.receiveData();
                // Process the received data using the provided input handler
                dataFromClientEvent(client, receivedData);
            }
        } catch (SocketException | java.io.EOFException ex) {
            //if there is a socket exception, it is most likely because the socket was disconnected
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        clients.remove(client);
        if (serverIsOpen()) onClientDisconnect(client);
    }

}