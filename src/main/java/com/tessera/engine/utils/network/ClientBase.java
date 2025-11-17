package com.tessera.engine.utils.network;

public abstract class ClientBase {

    public abstract void onConnected(boolean success, Throwable cause, ChannelBase channel);

    public abstract void close();
}
