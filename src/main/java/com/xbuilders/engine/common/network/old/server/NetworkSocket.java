///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.common.network.old.server;
//
//import com.xbuilders.engine.common.network.ChannelBase;
//
//import java.io.IOException;
//import java.net.SocketTimeoutException;
//
///**
// * @author zipCoder933
// */
//public class NetworkSocket {
//
//    public Thread messageThread;
//    ChannelBase connection;
//
//    public String getConnectionStatus() {
//        if (connection != null) {
//            String stats = "ping: " + getSecSinceLastPing() + "s ago"
//                    + ";  connected: " + connection.isConnected()
//                    + ";  closed: " + isClosed();
//            stats += ";  message thread alive: " + messageThread.isAlive();
//            return stats;
//        }
//        return "(no connection)";
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final NetworkSocket other = (NetworkSocket) obj;
//        return this.getHostAddress().equals(other.getHostAddress())
//                && this.getRemoteHostString().equals(other.getRemoteHostString())
//                && this.connection.getPort() == other.connection.getPort();
//    }
//
//    public void close() throws IOException {
//        inputStream.close();
//        outputStream.close();
//        connection.close();
//    }
//
////    public byte[] receiveData() throws IOException {
////        int length = inputStream.readInt();
////        byte[] data = new byte[length];
////        inputStream.readFully(data);
////        return data;
////    }
//
//    /**
//     * Read data from the connection and return it as a byte array.
//     * Blocks until data is available.
//     *
//     * @return
//     * @throws IOException
//     */
//    public byte[] receiveData() throws IOException {
//        int originalTimeout = connection.getSoTimeout(); // Save the current timeout
//        try {
//            int length = inputStream.readInt(); // Read the length of data
//            byte[] data = new byte[length];
//
//            connection.setSoTimeout(10000); // Set timeout for readFully()
//            inputStream.readFully(data); // This will throw SocketTimeoutException if it takes too long
//            return data;
//        } catch (SocketTimeoutException e) {
//            System.out.println("Timeout: readFully took too long");
//            return new byte[]{0};
//        } finally {
//            connection.setSoTimeout(originalTimeout); // Restore the original timeout
//        }
//    }
//
//
//    public void sendData(byte[] data) throws IOException {
//        /**
//         * While it's not strictly required in all cases,
//         * especially if you have a fixed-size message
//         * format or are using a protocol that handles
//         * message framing differently, sending the
//         * length of the data before the actual data is a good
//         * practice to ensure reliable and predictable
//         * communication between the sender and receiver.
//         */
//        outputStream.writeInt(data.length);
//        outputStream.write(data);
//    }
//
//    public void sendData(byte data) throws IOException {
//        outputStream.writeInt(1);
//        outputStream.write(data);
//        //Since this is not a buffered output stream we do NOT need to flush
//    }
//
//    public void sendString(String str) throws IOException {
//        byte[] data = str.getBytes();
//        outputStream.writeInt(data.length);
//        outputStream.write(data);
//    }
//
//    @Override
//    public String toString() {
//        return "NetworkSocket{" + connection + '}';
//    }
//
//}
