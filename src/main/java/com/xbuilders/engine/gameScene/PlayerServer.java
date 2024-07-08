///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.utils.network;
//
//import com.xbuilders.engine.player.Player;
//import com.xbuilders.engine.utils.network.server.NetworkSocket;
//import com.xbuilders.engine.utils.network.server.NetworkUtils;
//import com.xbuilders.engine.utils.network.server.Server;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author zipCoder933
// */
//public class PlayerServer extends Server {
//
//    public static final byte CONNECT_TO_IPs = -128;
//    public static final byte PLAYER_INFO = -127;
//    private boolean isHosting;
//    Player thisPlayer;
//    HashMap<NetworkSocket, Player> players;
//
//
//    public PlayerServer(Player thisPlayer) {
//        players = new HashMap<>();
//        this.thisPlayer = thisPlayer;
//        System.out.println(getIpAdress());
//    }
//
//    public NetworkSocket getClientByName(String name) {
//        for (HashMap.Entry<NetworkSocket, Player> entry : players.entrySet()) {
//            if (entry.getValue().name.equals(name)) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }
//
//
//    @Override
//    public boolean newClientEvent(NetworkSocket newClient) {
//        return clientJoined(newClient, isHosting);
//    }
//
//    @Override
//    public void dataFromClientEvent(NetworkSocket client, byte[] receivedData) {
//        clientData(client, receivedData, isHosting);
//    }
//
//    @Override
//    public void onClientDisconnect(NetworkSocket client) {
//        clientDisconnected(client);
//    }
//
//    public void clientDisconnected(NetworkSocket client) {
//        print(playerName(client) + " was disconnected");
//        players.remove(client);
//    }
//
//    public interface ServerNewClientFunction {
//
//        void handle(NetworkSocket newClient, Player newPlayer) throws IOException;
//    }
//
//    public interface ServerDataFunction {
//
//        void handle(NetworkSocket client, Player player,
//                byte header, byte[] data) throws IOException;
//    }
//
//    private ServerDataFunction clientData;
//    private ServerNewClientFunction clientJoined;
//
//    public boolean clientJoined(NetworkSocket newClient, boolean isHosting) {
//        if (clientAlreadyJoined(newClient)) {
//            return false;//Reject players that have already connected
//        }
//        try {
//            Player player = new Player();
//            player.loadInfoFromBytes(newClient.receiveData());
//            players.put(newClient, player);
//            newClient.sendData(thisPlayer.infoToBytes());
//            print("Client joined. Info exchange results: " + player.toString());
//            printi("Connected clients: " + players.values());
//            if (clientJoined != null) {
//                clientJoined.handle(newClient, player);
//            }
//
//            if (isHosting && clients.size() > 0/*0 because the client has not yet been added*/) {
//                //Send ips to all clients
//                print("Sending IPs to other players");
//                String[] ipAdresses = new String[clients.size() + 1];
//                ipAdresses[0] = newClient.getRemoteSocketAddress().getHostString();
//                for (int i = 0; i < clients.size(); i++) {
//                    ipAdresses[i + 1] = clients.get(i).getRemoteSocketAddress().getHostString();
//                }
//                byte[] msg = NetworkUtils.formatMessage(CONNECT_TO_IPs, String.join("\n", ipAdresses));
//                sendToAllClients(msg);
//            }
//        } catch (IOException ex) {
//         ex.printStackTrace();
//        }
//        return true;
//
//    }
//
//    public void clientData(NetworkSocket client, byte[] receivedData, boolean isHosting) {
//        byte header = receivedData[0];
//        print("Message \t Header: " + header + ", Data: " + new String(receivedData));
//
//        try {
//            switch (header) {
//                case PLAYER_INFO -> {
//                    Player player = players.get(client);
//                    boolean newPlayer = false;
//                    if (player == null) { //If this player does not exist yet
//                        player = new Player();
//                        players.put(client, player);
//                        newPlayer = true;
//                    }
//                    player.loadInfoFromBytes(receivedData);
//                    print((newPlayer ? "New " : "") + "Player Info: " + player.toString());
//                    if (newPlayer) {
//                        if (clientJoined != null) {
//                            clientJoined.handle(client, player);
//                        }
//                        printi("Connected clients: " + players.values());
//                    }
//                }
//                case CONNECT_TO_IPs -> {
//                    String[] ipAdresses = NetworkUtils.getMessageAsString(receivedData).split("\n");
//                    print("Connect To IP: " + Arrays.toString(ipAdresses));
//                    for (String adress : ipAdresses) {
//                        if (getIpAdress().equals(adress)
//                                || clientAlreadyJoined(adress, getIpAdress())) {
//                            continue;//Dont re-connect to players that are already connected to us
//                        }
//                        print("\tConnecting to " + adress);
//                        connectToServer(new InetSocketAddress(adress, getPort()));
//                    }
//                }
//                default -> {
//                    if (clientData != null) {
//                        clientData.handle(client, thisPlayer, header, receivedData);
//                    }
//                }
//            }
//        } catch (IOException ex) {
//           ex.printStackTrace();
//        }
//    }
//
//    private String playerName(NetworkSocket client) {
//        return (players.get(client) == null ? "?" : players.get(client).name);
//    }
//
//    public boolean verbose = true;
//
//    private void print(String str) {
//        if (verbose) {
//            printi(str);
//        }
//    }
//
//    private void printi(String str) {
//        System.out.println("NETWORK \t" + str);
//    }
//
//    public void hostGame(int port) throws IOException {
//        print("Hosting game at " + port);
//        isHosting = true;
//        start(port);
//    }
//
//
//    public void connectToGame(String ipAdress, int port) throws IOException {
//        print("Joining " + ipAdress + " at " + port);
//        start(port);
//        isHosting = false;
//        connectToServer(new InetSocketAddress(ipAdress, port));
//    }
//}
