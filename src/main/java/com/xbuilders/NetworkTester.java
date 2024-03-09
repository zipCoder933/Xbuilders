/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.PlayerServer;
import com.xbuilders.engine.utils.network.server.Server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class NetworkTester {

    static class Server1 extends Server {

        public Server1() {
            super(true);
        }

        private void print(String str) {
            System.out.println(this.getIpAdress() + ":\t " + str);
        }

        @Override
        public boolean newClient(NetworkSocket newClient) {
            try {
                print("New client: " + newClient);
                newClient.sendData("Hello world!".getBytes());

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
        Server1 s1 = new Server1();
        System.out.println("IP: " + s1.getIpAdress());
        Scanner scanner = new Scanner(System.in);
//        Server1 s2 = new Server1();

        s1.start(8080);
//        s2.start(8080);
        System.out.println("Enter IP to connect");
        s1.addClient(new InetSocketAddress(scanner.nextLine(), 8080));

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
