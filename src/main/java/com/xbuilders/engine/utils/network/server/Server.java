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
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public abstract class Server<ClientSocket extends NetworkSocket> { //We can define custom network sockets

    protected String ipAdress;
    public final ArrayList<ClientSocket> clients = new ArrayList<>(); //A socket is a two way connection

    public static final byte[] pingMessage = new byte[]{0};
    public static final byte[] pongMessage = new byte[]{1};
    public static final long PING_INTERVAL = 10000;


    Thread newClientThread;
    Timer pingThread = new Timer();
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

        boolean available = NetworkUtils.available(port);
        System.out.println("PORT AVAILABLE: " + available);

        if (port < 1024) {
            throw new IOException("Port number must be higher than 1024");
        } else if (!available) {
            throw new IOException("Port " + port + " already in use");
        }
        this.serverPort = port;
        serverSocket = new java.net.ServerSocket(port);


        pingThread.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (ClientSocket client : clients) {
                        if (!client.isClosed()) {
                            //System.out.println("Sending PING to " + client.toString());
                            client.sendData(pingMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 10000, PING_INTERVAL);


        newClientThread = new Thread() {
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
                        //ex.printStackTrace();
                        System.out.println("Socket closed: (" + ex.getMessage() + ")");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        newClientThread.start();
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

        if (newClientThread != null) newClientThread.interrupt();
        newClientThread = null;

        if (pingThread != null) pingThread.cancel();
        pingThread = null;

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
        Thread clientDataThread = new Thread(() -> getClientDataLoop(newClient));
        clientDataThread.setPriority(1);
        clientDataThread.start();
        clients.add(newClient);
    }

    public ClientSocket connectToServer(InetSocketAddress address) throws IOException {
        ClientSocket newClient = (ClientSocket) addConnection(address);
        connectToServer(newClient);
        return newClient;
    }

    protected void getClientDataLoop(ClientSocket client) {
        try {
            while (!client.isClosed()) {
                // Assuming you have a method like receiveData() to receiveData messages from the client
                byte[] receivedData = client.receiveData();

                if (Arrays.equals(receivedData, pingMessage)) {
                    //System.out.println("Received PING from " + client.toString());
                    client.lastPing = System.currentTimeMillis();
                    client.sendData(pongMessage);
                } else if (Arrays.equals(receivedData, pongMessage)) {
                    //System.out.println("Received PONG from " + client.toString());
                } else {
                    // Process the received data using the provided input handler
                    dataFromClientEvent(client, receivedData);
                }
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