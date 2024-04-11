package com.xbuilders.engine.world.light;

import java.util.HashMap;

public class TorchChannelSet {
    public HashMap<Byte, Byte> list = new HashMap<Byte, Byte>();

    public TorchChannelSet(short singleChannel) {
        this.list.put((byte) 1, (byte) singleChannel);
    }


    public byte get(byte falloff) {
        if(!list.containsKey(falloff)) {
          return (byte) 0;
        }
        return list.get(falloff);
    }

    public byte getCombinedLight() {
        byte light = 0;
        for (byte falloff : list.keySet()) {
            light += list.get(falloff);
        }
        return light;
    }

}
