package com.xbuilders.engine.multiplayer;

public class NetworkJoinRequest {
    public boolean hosting;
    public int toPortVal;
    public String playerName;
    public int fromPortVal;
    public String hostIpAdress;

    public NetworkJoinRequest(boolean hosting, int fromPortVal, int portVal, String playerName, String ipAdress) {
        this.hosting = hosting;
        this.fromPortVal = fromPortVal;
        this.toPortVal = portVal;
        this.playerName = playerName;
        this.hostIpAdress = ipAdress;
    }

    @Override
    public String toString() {
        return "NetworkJoin{" +
                "hosting=" + hosting +
                ", portVal=" + toPortVal +
                ", playerName='" + playerName + '\'' +
                ", ipAdress='" + hostIpAdress + '\'' +
                '}';
    }
}
