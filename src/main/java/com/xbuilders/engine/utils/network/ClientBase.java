package com.xbuilders.engine.utils.network;

import io.netty.channel.ChannelFuture;

public abstract class ClientBase {

    public abstract void onConnected(boolean success, Throwable cause, ChannelBase channel);

    public abstract void close();
}
