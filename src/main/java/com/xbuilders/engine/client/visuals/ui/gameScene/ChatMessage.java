package com.xbuilders.engine.client.visuals.ui.gameScene;

class ChatMessage {
    public ChatMessage(String value) {
        this.value = value;
        time = System.currentTimeMillis();
    }

    public String value;
    public long time;
}
