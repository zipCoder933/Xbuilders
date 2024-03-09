/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server;

import com.xbuilders.engine.utils.network.server.masterServer.MSS;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class Sim_ServerSocket extends Base_ServerSocket {

    private NetworkSocket masterSocket;
    private String ipAdress;

    private NetworkSocket connectToMaster() throws IOException {
        return new NetworkSocket(new Socket("localhost", getPort()));
    }

    public Sim_ServerSocket(int port, String ipAdress) throws IOException {
        super(port);
        this.ipAdress = ipAdress;
        MSS.initialize();
        MSS.openPort(port);
        masterSocket = connectToMaster();
        masterSocket.sendData(NetworkUtils.formatMessage(MSS.SERVER, ipAdress.getBytes()));
    }

    public NetworkSocket accept() throws IOException {
        String targetAdress = new String(masterSocket.receiveData());
        NetworkSocket socket = connect(MSS.CLIENT_NO_REPLY, targetAdress, ipAdress);
        return socket;
    }

    private NetworkSocket connect(byte tag, String targetAdress, String remoteAdress) throws IOException {
        NetworkSocket newClient = connectToMaster();
        newClient.setFakeHostAndRemoteAddress(targetAdress, remoteAdress);
        newClient.sendData(NetworkUtils.formatMessage(tag, targetAdress + "\n" + remoteAdress));

//        while (true) {
//            byte[] msg = newClient.receiveData();
//            System.out.println("Message: " + Arrays.toString(msg));
//            if (msg.length == 1 && msg[0] == 127) {
//                break;
//            }
//        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Sim_ServerSocket.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println("Ready");

        return newClient;
    }

    public NetworkSocket addConnection(InetSocketAddress address) throws IOException {
        String targetAdress = address.getHostString();
        String remoteAdress = ipAdress;
        return connect(MSS.CLIENT, targetAdress, remoteAdress);
    }

    public boolean isClosed() {
        return masterSocket.isClosed();
    }

    public void close() throws IOException {
        masterSocket.close();
    }

}
