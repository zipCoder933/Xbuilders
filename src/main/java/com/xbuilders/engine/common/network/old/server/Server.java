///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.common.network.old.server;
//
///**
// * @author zipCoder933
// */
//
//import com.xbuilders.engine.common.utils.ErrorHandler;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.function.Supplier;
//
//public abstract class Server<TClient extends NetworkSocket> { //We can define custom network sockets
//
//    protected String ipAdress;
//    public final ArrayList<TClient> clients = new ArrayList<>(); //A connection is a two way connection
//
//    public static final byte[] pingMessage = new byte[]{0};
//    public static final byte[] pongMessage = new byte[]{1};
//    public static final long PING_INTERVAL = 30000;
//
//
//    Thread newClientThread;
//    private Timer pingThread;
//    Supplier<TClient> clientSocketSupplier;
//    java.net.ServerSocket serverSocket;
//    private int serverPort;
//
//    /**
//     * Server accept incoming connection
//     *
//     * @return the new client
//     * @throws IOException
//     */
//    private TClient acceptClient() throws IOException {
//        TClient clientSocket = clientSocketSupplier.get();
//        clientSocket.init(serverSocket.accept());
//        return clientSocket;
//    }
//
//    /**
//     * Adds a new connection
//     *
//     * @param address the address to the client
//     * @return the new client
//     * @throws IOException
//     */
//    private TClient addConnection(InetSocketAddress address) throws IOException {
//        TClient clientSocket = clientSocketSupplier.get();
//        clientSocket.init(address);
//        return clientSocket;
//    }
//
//    public boolean isClosed() {
//        return serverSocket.isClosed();
//    }
//
//
//    public Server(Supplier<TClient> clientSocketSupplier) {
//        super();
//        this.clientSocketSupplier = clientSocketSupplier;
//        init();
//    }
//
//    private void init() {
//        try {
//            InetAddress localHost = InetAddress.getLocalHost();
//            this.ipAdress = localHost.getHostAddress();
//        } catch (UnknownHostException e) {
//            this.ipAdress = null;
//        }
//    }
//
//
//    public void start(int port) throws IOException {
//
//        boolean available = NetworkUtils.available(port);
//        System.out.println("PORT AVAILABLE: " + available);
//
//        if (port < 1024) {
//            throw new IOException("Port number must be higher than 1024");
//        } else if (!available) {
//            throw new IOException("Port " + port + " already in use");
//        }
//        this.serverPort = port;
//        serverSocket = new java.net.ServerSocket(port);
//
//
//        pingThread = new Timer();
//        pingThread.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    for (TClient client : clients) {
//                        if (!client.isClosed()) {
//                            //System.out.println("Sending PING to " + client.toString());
//                            client.sendData(pingMessage);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 10000, PING_INTERVAL);
//
//
//        newClientThread = new Thread() {
//            @Override
//            public void run() {
//                while (serverIsOpen()) {
//                    try {
//                        TClient newClient = (TClient) acceptClient();
//                        if (newClientEvent(newClient)) {
//                            connectToServer(newClient);
//                        } else {
//                            newClient.close();
//                        }
//                    } catch (SocketException | java.io.EOFException ex) {
//                        //if there is a connection exception, it is most likely because the connection was disconnected
//                        //ex.printStackTrace();
//                        System.out.println("Socket closed: (" + ex.getMessage() + ")");
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        newClientThread.start();
//    }
//
//    /**
//     * @return the port
//     */
//    public int getPort() {
//        return serverPort;
//    }
//
//    public String getIpAdress() {
//        return ipAdress;
//    }
//
//    public void clientDisconnectEvent(TClient client) {
//        System.out.println("Disconnected: " + client.toString());
//    }
//
//    public boolean clientAlreadyJoined(String targetHost, String remoteHost) {
//        for (TClient client : clients) {
//            if (NetworkUtils.hasSameConnection(
//                    client.getHostAddress(), client.getRemoteHostString(),
//                    targetHost, remoteHost
//            )) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void sendToAllClients(byte[] data) throws IOException {
//        for (TClient client : clients) {
//            client.sendData(data);
//        }
//    }
//
//    public void close() throws IOException {
//
//        //We must set these to null because once they have been closed, they cant be opened again
//        if (serverSocket != null) serverSocket.close();
//        serverSocket = null;
//
//        if (newClientThread != null) newClientThread.interrupt();
//        newClientThread = null;
//
//        if (pingThread != null) pingThread.cancel();
//        pingThread = null;
//
//        for (TClient client : clients) {
//            if (client != null) client.close();
//        }
//        clients.clear();
//    }
//
//    public boolean serverIsOpen() {
//        return serverSocket != null && !serverSocket.isClosed();
//    }
//
//
//    public boolean clientAlreadyJoined(TClient newClient) {
//        return clientAlreadyJoined(newClient.getHostAddress(), newClient.getRemoteHostString());
//    }
//
//    /**
//     * The client is only added to our client-list if we return true, otherwise
//     * the client gets disconnected.
//     *
//     * @param newClient the client
//     * @return if the localServer should accept the client.
//     */
//    public abstract boolean newClientEvent(TClient newClient);
//
//    /**
//     * @param client       the client
//     * @param receivedData incoming bytes from the client
//     */
//    public abstract void dataFromClientEvent(TClient client, byte[] receivedData);
//
//
//    protected void connectToServer(TClient newClient) {
//        Thread clientDataThread = new Thread(() -> getClientDataLoop(newClient));
//        clientDataThread.setPriority(1);
//        clientDataThread.start();
//        newClient.messageThread = clientDataThread;
//        clients.add(newClient);
//    }
//
//    public TClient connectToServer(InetSocketAddress address) throws IOException {
//        TClient newClient = (TClient) addConnection(address);
//        connectToServer(newClient);
//        return newClient;
//    }
//
//    protected void getClientDataLoop(TClient client) {
//        try {
//            client.getSocket().setKeepAlive(true); //Enable keep alive
//            //client.getSocket().setSoTimeout(10000); // if no data arrives within 10s, a SocketTimeoutException will be thrown.
//
//            while (!client.isClosed()) {
//                // Assuming you have a method like receiveData() to receiveData messages from the client
//                byte[] receivedData = client.receiveData();
//
//                if (Arrays.equals(receivedData, pingMessage)) {
////                    MainWindow.printlnDev("Received PING from " + client.toString());
//                    client.lastPing = System.currentTimeMillis();
//                    client.sendData(pongMessage);
//                    //System.out.println(client.toString() + " last ping: " + client.getSecSinceLastPing());
//                } else if (Arrays.equals(receivedData, pongMessage)) {
//                    client.lastPing = System.currentTimeMillis();
////                    MainWindow.printlnDev("Received PONG from " + client.toString());
//                } else {
//                    // Process the received data using the provided input handler
//                    dataFromClientEvent(client, receivedData);
//                }
//            }
//        } catch (SocketException | java.io.EOFException ex) {
//            //if there is a connection exception, it is most likely because the connection was disconnected
//            System.out.println("Socket closed: (" + ex.getMessage() + ")");
//        } catch (IOException ex) {
//            ErrorHandler.log(ex);
//        }
//        clients.remove(client);
//        System.out.println("CLIENT DISCONNECTED: " + client.toString());
//        if (serverIsOpen()) {
//            boolean reconnected = tryToReconnect(client);
//            if (!reconnected) {
//                //client.close();
//                clientDisconnectEvent(client);
//            }
//        }
//    }
//
//    private boolean tryToReconnect(TClient client) {
//        System.out.println("Not attempting reconnects");
//        return false;//For now, we won't try to reconnect
//
////        try {
////            for (int i = 0; i < 10; i++) {
////                System.out.println("Trying to reconnect to: " + client.toString());
////                TClient newClient = (TClient) addConnection(client.getRemoteSocketAddress());
////                if (!newClient.isClosed()) {
////                    System.out.println("Reconnected to: " + client.toString());
////                    connectToServer(newClient);
////                    return true;
////                }
////                Thread.sleep(1000);
////            }
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            System.out.println("Failed to reconnect to: " + client.toString() + " " + e.getMessage());
////        }
////        return false;
//    }
//
//}