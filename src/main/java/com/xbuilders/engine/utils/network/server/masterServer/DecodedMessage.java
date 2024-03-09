/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.network.server.masterServer;

import java.util.Arrays;

/**
 *
 * @author zipCoder933
 */
public class DecodedMessage {

    public byte header;
    public String host;
    public byte[] message;

    public DecodedMessage(byte header, String host, byte[] message) {
        this.header = header;
        this.host = host;
        this.message = message;
    }

    @Override
    public String toString() {
        return "DecodedMessage{" + "header=" + header + ", host=" + host + ", message=" + new String(message) + '}';
    }

    public static byte[] concatMessage(byte header, String host, byte[] bytes) {
        // Convert host string to bytes
        byte[] hostBytes = host.getBytes();

        // Calculate the length of the resulting byte array
        int totalLength = 1 + hostBytes.length + 1 + bytes.length; // Add 1 for the newline delimiter

        // Create a new byte array with the calculated length
        byte[] result = new byte[totalLength];

        // Set the header byte at the beginning of the result array
        result[0] = header;

        // Copy host bytes after the header
        System.arraycopy(hostBytes, 0, result, 1, hostBytes.length);

        // Add newline delimiter after host bytes
        result[1 + hostBytes.length] = '\n';

        // Copy the remaining bytes after the newline delimiter
        System.arraycopy(bytes, 0, result, 1 + hostBytes.length + 1, bytes.length);

        return result;
    }

    public static DecodedMessage decodeMessage(byte[] message) {
        // Extract the header byte
        byte header = message[0];

        // Find the index of the newline character
        int newlineIndex = -1;
        for (int i = 1; i < message.length; i++) {
            if (message[i] == '\n') {
                newlineIndex = i;
                break;
            }
        }

        // Extract the host bytes
        byte[] hostBytes = Arrays.copyOfRange(message, 1, newlineIndex);
        String host = new String(hostBytes);

        // Extract the remaining bytes after the newline
        int remainingLength = message.length - newlineIndex - 1;
        byte[] remainingBytes = new byte[remainingLength];
        System.arraycopy(message, newlineIndex + 1, remainingBytes, 0, remainingLength);

        return new DecodedMessage(header, host, remainingBytes);
    }

}
