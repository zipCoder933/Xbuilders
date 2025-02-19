package com.xbuilders.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamUtils {

    public static ByteBuffer toByteBuffer(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
    }

    public static ByteBuffer toDirectByteBuffer(InputStream inputStream) throws IOException {
        ReadableByteChannel channel = Channels.newChannel(inputStream);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8192);

        while (channel.read(byteBuffer) != -1) {
            if (!byteBuffer.hasRemaining()) {
                ByteBuffer newBuffer = ByteBuffer.allocateDirect(byteBuffer.capacity() * 2);
                byteBuffer.flip();
                newBuffer.put(byteBuffer);
                byteBuffer = newBuffer;
            }
        }

        byteBuffer.flip(); // Prepare for reading
        return byteBuffer;
    }
}