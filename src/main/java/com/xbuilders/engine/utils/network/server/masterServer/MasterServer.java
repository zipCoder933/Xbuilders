/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server.masterServer;

import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import static com.xbuilders.engine.utils.network.server.masterServer.MSS.print;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * TODO: If a client joins and immediately sends a message, the message may come
 * through before the recipient has even finished accepting() the new client
 *
 * I think i will have to test the code in real life to see if this is a thing I
 * need to fix, or if i should just keep it this way
 *
 * @author zipCoder933
 */
class MasterServer {

    //The server sockets are only to be notified by new clients
    public HashMap<String, NetworkSocket> servers;
    public final ArrayList<NetworkSocket> clients;

    public ServerSocket serverSocket;
    private Socket clientSocket;//Used to find clients
    Thread clientThread;

    //The clients are to be notified if a message is sent, or a disconnect occurs with the other associated socket 
    public MasterServer(int port) throws IOException {
        super();
        servers = new HashMap<>();
        clients = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        clientThread = new Thread() {
            @Override
            public void run() {
                while (!serverSocket.isClosed()) {
                    try {
                        clientSocket = serverSocket.accept();
                        NetworkSocket newClient = new NetworkSocket(clientSocket);

                        if (newClient(newClient)) {
                            clients.add(newClient);
                            Thread clientThread = new Thread(() -> handleClient(newClient));
                            clientThread.setPriority(1);
                            clientThread.start();
                        }
                    } catch (SocketException ex) {
                        //if there is a socket exception, it is most likely because the socket was disconnected
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        clientThread.start();
    }

//    HashSet<NetworkSocket> readyClients = new HashSet<>();
//    private void clientReady(NetworkSocket client2) {
//        Iterator<NetworkSocket> iterator = readyClients.iterator();
//        while (iterator.hasNext()) {
//            NetworkSocket client1 = iterator.next();
//
//            //Remove the other client that is paired with this one
//            if (client1 != client2
//                    && NetworkUtils.hasSameConnection(
//                            client2.getHostAddress(), client2.getRemoteHostString(),
//                            client1.getHostAddress(), client1.getRemoteHostString())) {
//                clients.remove(client1);
//                clients.remove(client2);
//                print("\nCLIENTS: " + client2 + "\n AND " + client1 + " ARE READY");
//                try {
//                    client1.sendData(new byte[]{127});
//                    client2.sendData(new byte[]{127});
//                } catch (IOException ex) {
//                    Logger.getLogger(MasterServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                return;
//            }
//        }
//        readyClients.add(client2);
//    }

    protected void handleClient(NetworkSocket client) {
        try {
//            clientReady(client);
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
        handleClosedClient(client);
    }

    public boolean newClient(NetworkSocket newClient) {
        try {
            byte[] data = newClient.receiveData();
            if (data[0] == MSS.SERVER) {
                String serverIP = NetworkUtils.getMessageAsString(data);
                print("New server: " + serverIP);
                servers.put(serverIP, newClient);
                return false;//We only keep clients in our client list
            } else {
                String[] str = NetworkUtils.getMessageAsString(data).split("\n");
                String targetAdress = str[0];
                String remoteAdress = str[1];
                newClient.setFakeHostAndRemoteAddress(targetAdress, remoteAdress);

                NetworkSocket server = servers.get(targetAdress);
                if (server != null) {
                    if (server.isClosed()) {
                        handleClosedServer(targetAdress);
                        print("New client to closed server " + targetAdress);
                        return false;
                    } else {
                        print("New client to server " + targetAdress);
                        if (data[0] != MSS.CLIENT_NO_REPLY) {
                            print("\tSending to " + targetAdress);
                            server.sendData(remoteAdress.getBytes());
                        }
                        return true;
                    }
                } else {
                    print("New client to unknown server " + targetAdress);
                    return false;
                }
            }
        } catch (IOException ex) {
            print("Client dropped");
            return false;
        } catch (Exception ex) {
            print("Client dropped");
            ex.printStackTrace();
            return false;
        }
    }

    public void recieveDataFromClient(NetworkSocket fromClient, byte[] receivedData) {
//        print("data " + fromClient + ", " + new String(receivedData));
//Used to relay messages from one socket to another
        try {
            for (NetworkSocket toClient : clients) {
//                print("\tClient: " + toClient);
                if (toClient.getRemoteHostString().equals(fromClient.getHostAddress())) {
                    print("Relaying message: " + new String(receivedData));
                    toClient.sendData(receivedData);
                    break;
                }
            }
        } catch (IOException ex) {
        }
    }

    private void handleClosedServer(String key) {
        servers.remove(key);
    }

    private void handleClosedClient(NetworkSocket closing) {
        try {
            clients.remove(closing);
            for (int i = 0; i < clients.size(); i++) {
                NetworkSocket client = clients.get(i);

                //Remove the other client that is paired with this one
                if (NetworkUtils.hasSameConnection(
                        closing.getHostAddress(), closing.getRemoteHostString(),
                        client.getHostAddress(), client.getRemoteHostString())) {
                    clients.remove(i);
                    client.close();
                    return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MasterServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
