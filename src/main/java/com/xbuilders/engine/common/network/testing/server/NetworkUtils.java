/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.common.network.testing.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zipCoder933
 */
public class NetworkUtils {

    public static final int HEADER_LENGTH = 1;
    private static final int MIN_PORT_NUMBER = 0;
    private static final int MAX_PORT_NUMBER = 65535;


    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean available(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static byte[] formatMessage(byte header, String message) {
        byte[] messageArray = message.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[HEADER_LENGTH + messageArray.length];
        result[0] = header;
        System.arraycopy(messageArray, 0, result, HEADER_LENGTH, messageArray.length);
        return result;
    }

    public static byte[] formatMessage(byte header, byte[] message) {
        byte[] result = new byte[HEADER_LENGTH + message.length];
        result[0] = header;
        System.arraycopy(message, 0, result, HEADER_LENGTH, message.length);
        return result;
    }

    public static byte[] getMessage(byte[] message) {
        if (message.length < HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid message length");
        }
        byte[] result = new byte[message.length - HEADER_LENGTH];
        System.arraycopy(message, HEADER_LENGTH, result, 0, result.length);
        return result;
    }

    public static String getMessageAsString(byte[] message) {
        if (message.length < HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid message length");
        }
        byte[] result = new byte[message.length - HEADER_LENGTH];
        System.arraycopy(message, HEADER_LENGTH, result, 0, result.length);
        return new String(result, StandardCharsets.UTF_8);
    }

    public static void getAvailableDevicesOnLocalNetwork() {
        //Search for other computers
        /**
         * If an IP address falls within the ranges of private addresses (e.g.,
         * 192.168.x.x, 172.16.x.x to 172.31.x.x, 10.x.x.x), it is a local IP
         * address.
         *
         * The private IP address range reserved for local networks is from
         * 192.168.0.0 to 192.168.255.255.
         */
        String baseIpAddress = "192.168."; // Change this to match your local network range
        int timeout = 500; // Timeout in milliseconds

        // Create a thread pool with a fixed number of threads
        int numThreads = Runtime.getRuntime().availableProcessors(); // You can adjust this based on your system's capabilities
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int x = 0; x <= 255; x++) {
            for (int y = 0; y <= 255; y++) {
                final String ipAddress = baseIpAddress + x + "." + y;

                // Submit each task to the executor service
                executorService.submit(() -> {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(ipAddress);
                        if (inetAddress.isReachable(timeout)) {
                            System.out.println("Device found: " + ipAddress);
                            // Add your logic for handling the discovered device (e.g., testAndAddClient)
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (java.io.IOException e) {
                        // Ignore, device is not reachable
                    }
                });
            }
        }
        // Shutdown the executor service when all tasks are complete
        executorService.shutdown();
    }

    /**
     * Compares the remote and target hosts of both sockets to tell if both
     * sockets share the same remote and target hosts
     *
     * @param socketA_host1 Socket A, host 1
     * @param socketA_host2 Socket A, host 2
     * @param socketB_host1 Socket B, host 1
     * @param socketB_host2 Socket B, host 2
     * @return if the 2 sockets are the same
     */
    public static boolean hasSameConnection(
            String socketA_host1, String socketA_host2,
            String socketB_host1, String socketB_host2) {

        return (Objects.equals(socketA_host1, socketB_host1) && Objects.equals(socketA_host2, socketB_host2))
                || (Objects.equals(socketA_host1, socketB_host2) && Objects.equals(socketA_host2, socketB_host1));
    }

}
