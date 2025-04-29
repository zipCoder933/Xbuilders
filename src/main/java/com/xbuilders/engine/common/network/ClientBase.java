package com.xbuilders.engine.common.network;

public abstract class ClientBase {

    public abstract void onConnected(boolean success, Throwable cause, ChannelBase channel);

    public abstract void close();
}
