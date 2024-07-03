/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.tests;

import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author zipCoder933
 */
public class NetworkTester {

    static class MyServer extends Server {

        final String serverName;

        public MyServer(String serverName2) {
            super();
            serverName = serverName2;
        }

        private void print(String str) {
            System.out.println(serverName + ":\t " + str);
        }

        @Override
        public boolean newClient(NetworkSocket newClient) {
            try {
                print("New client: " + newClient);
                newClient.sendString("(From "+serverName+"), Welcome \"" + newClient.getHostAddress() + "\"!");
            } catch (IOException ex) {
                Logger.getLogger(NetworkTester.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

        @Override
        public void recieveDataFromClient(NetworkSocket client, byte[] receivedData) {
            print("Data: " + new String(receivedData));
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * In simulation we cannot have 2 servers with the same ip adress and the same port, so in order to get around
         * this, each server has its own port, and servers connect to each other's ports with the same IP adresss
         */

        Scanner scanner = new Scanner(System.in);

        MyServer s1 = new MyServer("S1");
        s1.print(s1.getIpAdress());

        MyServer s2 = new MyServer("S2");
        s2.print(s2.getIpAdress());

        final int server1Port = 8080;
        final int server2Port = 8081;

        s1.startServer(server1Port);//This servers port is 8081
        s2.startServer(server2Port);//This servers port is 8080

        Thread.sleep(1000);
        s1.print("Connecting to S2");
        s1.addClient(new InetSocketAddress(s2.getIpAdress(), server2Port));

        Thread.sleep(1000);
        s2.print("Connecting to S1");
        s2.addClient(new InetSocketAddress(s1.getIpAdress(), server1Port));

//
//        System.out.println("Network tester");
//        Player p1 = new Player("Player"+System.currentTimeMillis());
//
//        PlayerServer s = new PlayerServer(p1, true);
//        System.out.println("IP: " + s.getIpAdress());
//        System.out.println("Ip adress or host?");
//        String ip = scanner.nextLine();
//        if (ip.equalsIgnoreCase("host")) {
//            s.connectToGameAsMaster(8080);
//        } else {
//            s.connectToGame(ip, 8080);
//        }
//        s.clientJoinedEvent((newClient, newPlayer) -> {
//            System.out.println("New client: " + newPlayer);
//        });
//        s.clientDataEvent((client, player, header, data) -> {
//            System.out.println("Data from " + player + ": " + new String(data));
//        });
    }
}
