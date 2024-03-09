/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server.masterServer;

import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.Server;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class MSS {

    public static final byte SERVER = 0;
    public static final byte CLIENT = 1;
    public static final byte CLIENT_NO_REPLY = 6;
    public static final byte SERVER_NEW_CLIENT = 2;
    public static final byte MESSAGE = 3;
    public static final byte CLOSE = 4;

    protected static void print(String str) {
        System.out.println("MASTER: " + str);
    }

    private static Server main = new Server(false) {
        private static final HashMap<Integer, MasterServer> masterServers = new HashMap<>();
        private static int fakeIP_ID = 0;

        @Override
        public boolean newClient(NetworkSocket newClient) {
            return true;
        }

        @Override
        public void recieveDataFromClient(NetworkSocket client, byte[] receivedData) {
            try {
                if (receivedData[0] == 'I') {
                    client.sendData((fakeIP_ID + "").getBytes());
                    fakeIP_ID++;
                } else {
                    int port = Integer.parseInt(new String(receivedData));
                    if (!masterServers.containsKey(port)) {
                        System.out.println("MSS: Adding new port server at " + port);
                        MasterServer server = new MasterServer(port);
                        masterServers.put(port, server);
                    }
                    client.sendData(new byte[]{0});
                }
            } catch (IOException ex) {
                Logger.getLogger(MSS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    public static NetworkSocket MSSSocket = null;

    public static void initialize() throws IOException {
        try {
            main.start(1024);
            System.out.println("MSS: Starting at port 1024");
        } catch (IOException ex) {
            //Server already started
        }
        if (MSSSocket == null) {
            MSSSocket = new NetworkSocket(new Socket("localhost", 1024));
        }
    }

    public static void openPort(int port) throws IOException {
        MSSSocket.sendData(("" + port).getBytes());
        byte[] receiveData = MSSSocket.receiveData(); //Wait until ready
    }

    public static String generateFakeIP() throws IOException {
        MSSSocket.sendData(("I").getBytes());
        int ipId = Integer.parseInt(new String(MSSSocket.receiveData())); //Wait until ready
        return "FakeIP-" + ipId;
    }

}
