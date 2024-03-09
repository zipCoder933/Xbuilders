/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

/**
 *
 * @author zipCoder933
 */
import com.xbuilders.engine.utils.network.server.masterServer.MSS;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Server {

    protected String ipAdress;
    public final ArrayList<NetworkSocket> clients;
    public Base_ServerSocket serverSocket;
    boolean simulation;

    Thread clientThread;

    public Server(boolean simulation) {
        super();
        this.simulation = simulation;
        this.clients = new ArrayList<>();
        if (simulation) {
            NetworkSocket start;
            try {
                MSS.initialize();
                ipAdress = MSS.generateFakeIP();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                ipAdress = null;
            }

        } else {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                this.ipAdress = localHost.getHostAddress();
            } catch (UnknownHostException e) {
                this.ipAdress = null;
            }
        }
    }

    /**
     * @return the port
     */
    public int getPort() {
        return serverSocket.getPort();
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
        serverSocket.close();
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

    public void start(int port) throws IOException {
        if (simulation) {
            serverSocket = new Sim_ServerSocket(port, ipAdress);
        } else {
            serverSocket = new Real_ServerSocket(port);
        }

        clientThread = new Thread() {
            @Override
            public void run() {
                while (!serverSocket.isClosed()) {
                    try {
                        NetworkSocket newClient = serverSocket.accept();
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

    protected void addClient(NetworkSocket newClient) {
        Thread clientThread = new Thread(() -> handleClient(newClient));
        clientThread.setPriority(1);
        clientThread.start();
        clients.add(newClient);
    }

    public NetworkSocket addClient(InetSocketAddress address) throws IOException {
        NetworkSocket newClient = serverSocket.addConnection(address);
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

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.zipcoder.networktest.network;
//
//import com.zipcoder.networktest.network.masterServer.MSS;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author zipCoder933
// */
//public abstract class SimServer extends BaseServer {
//
//    NetworkSocket masterSocket;
//    Thread messageThread;
//    NetworkSocket masterMainSocket;
//
//    public SimServer() throws IOException {
//        super();
//     
//    }
//
//    @Override
//    public final void start(int port) throws IOException {
//        super.start(port);
//        masterMainSocket.sendData(("" + getPort()).getBytes());
//        masterMainSocket.receiveData();//Wait until ready
//
//        masterSocket = new NetworkSocket(new Socket("localhost", getPort()));
//        masterSocket.sendData(NetworkUtils.formatMessage(SERVER, ipAdress.getBytes()));
//
//        //This is our connection to the master masterSocket.
//        //It should only be disconnected if we are closing our masterSocket
//        messageThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (!masterSocket.isClosed()) {
//                        while (!masterSocket.isClosed()) {
//                            byte[] receivedData = masterSocket.receiveData();
//                            MSS.DecodedMessage msg = MSS.decodeMessage(receivedData);
//
//                            if (msg.header == MSS.SERVER_NEW_CLIENT) {
//                                NetworkSocket newClient = getClient(new InetSocketAddress(msg.host, getPort()));
//                                newClient.sendData(NetworkUtils.formatMessage(CLIENT_NO_REPLY,
//                                        newClient.getHostAddress() + "\n" + ipAdress));
//                              
//                                if (newClient(newClient)) {
//                                    clients.add(newClient);
//                                } else {
//                                    newClient.close();//master will search for clients that share conneciton as this one and delete thems
//                                }
//                            } else if (msg.header == MSS.MESSAGE) {
//                                for (NetworkSocket client : clients) {
//                                    if (client.getHostAddress().equals(msg.host)) {
//                                        recieveDataFromClient(client, msg.message);
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } catch (IOException ex) {
//                    //If the master server no longer is running, we can no longer keep running ourselves
//                    System.out.println("Master server disconnected!");
//                    ex.printStackTrace();
//                    Runtime.getRuntime().halt(0);
//                }
//            }
//        };
//        messageThread.start();
//    }
//
//    @Override
//    public NetworkSocket addClient(InetSocketAddress address) throws IOException {
//        NetworkSocket newClient = getClient(address);
//        newClient.sendData(NetworkUtils.formatMessage(CLIENT,
//                address.getHostString() + "\n" + ipAdress));
//        clients.add(newClient);
//        return newClient;
//    }
//
//    private NetworkSocket getClient(InetSocketAddress address) throws IOException {
//        NetworkSocket newClient = new NetworkSocket(new Socket("localhost", getPort()));
//        newClient.setFakeHostAndRemoteAddress(address.getHostString(), ipAdress);
//        return newClient;
//    }
//
//    @Override
//    public void close() throws IOException {
//        super.close();
//        messageThread.interrupt();
//        messageThread = null;
//        masterSocket.close();
//    }
//
//}
