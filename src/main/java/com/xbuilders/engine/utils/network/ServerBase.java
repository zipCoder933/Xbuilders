package com.xbuilders.engine.utils.network;

import io.netty.channel.Channel;

public abstract class ServerBase {

    /**
     * Closes the localServer and all client connections.
     */
    public abstract void close();

}
