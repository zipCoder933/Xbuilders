package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.utils.network.fake.FakeChannel;
import io.netty.channel.ChannelFuture;

public class ChannelFutureBase {
    ChannelFuture nettyFuture;
    FakeChannel simulatedFuture;

    public ChannelFutureBase(ChannelFuture nettyFuture) {
        this.nettyFuture = nettyFuture;
    }

    public ChannelFutureBase(FakeChannel simulatedFuture) {
        this.simulatedFuture = simulatedFuture;
    }

    private boolean isReal() {
        return nettyFuture != null;
    }

    public boolean isSuccess() {
        if(isReal()) return nettyFuture.isSuccess();
        return true;
    }

    public Throwable cause() {
        if(isReal()) return nettyFuture.cause();
        return null;
    }
}
