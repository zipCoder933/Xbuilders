package com.xbuilders.engine.ui.topMenu;

public class NetworkJoinRequest {
    public boolean hosting;
    public int portVal;
    public String playerName;
    public String ipAdress;

    public NetworkJoinRequest(boolean hosting, int portVal, String playerName, String ipAdress) {
        this.hosting = hosting;
        this.portVal = portVal;
        this.playerName = playerName;
        this.ipAdress = ipAdress;
    }

    @Override
    public String toString() {
        return "NetworkJoin{" +
                "hosting=" + hosting +
                ", portVal=" + portVal +
                ", playerName='" + playerName + '\'' +
                ", ipAdress='" + ipAdress + '\'' +
                '}';
    }
}
