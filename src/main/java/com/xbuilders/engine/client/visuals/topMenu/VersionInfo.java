package com.xbuilders.engine.client.visuals.topMenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.common.resource.ResourceUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VersionInfo {
    final String versionsJson = "https://raw.githubusercontent.com/zipCoder933/Xbuilders/main/versions.json";
    Gson gson;
    float latestVersion;
    ClientWindow window;
    List<VersionChanges> releases = new ArrayList<>();

    public VersionInfo(ClientWindow window) {
        this.window = window;
        gson = new Gson();
    }

    public void createUpdatePrompt(PopupMessage popupMessage) {
        try {
            checkForUpdates();
            if (isNewerVersionAvailable()) {
                String changes = changesToString();
                popupMessage.confirmation("A new version of XBuilders is out! ", changes +
                        "\n\nWould you like to get the latest version?", () -> {
                    window.minimizeWindow();
                    openInBrowser();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openInBrowser() {
        //Open the browser with the latest version
        String url = "https://github.com/zipCoder933/Xbuilders/releases";
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException e) {
        } catch (URISyntaxException e) {
        }
    }

    public String changesToString() {
        String changes = "";
        for (VersionChanges version : releases) {
            if (version.version > Client.CLIENT_VERSION) changes += version.toString();
        }
        return changes;
    }

    public static class VersionChanges {
        public long version;
        public List<String> changes;

        public VersionChanges(long version) {
            this.version = version;
            this.changes = new ArrayList<>();
        }

        public String toString() {
            String str = "v" + version + " Changes:\n";
            for (String change : changes) {
                str += " * " + change + "\n";
            }
            return str + "\n\n";
        }
    }

    public String toString() {
        String s = "Latest version: " + latestVersion + "\n" + "Versions: " + String.join("\n\t", releases.toString());
        return s;
    }

    public boolean isNewerVersionAvailable() {
        if (latestVersion > Client.CLIENT_VERSION) {
            return true;
        } else return false;
    }

    public void checkForUpdates() {
        String jsonString = null;
        try {
            jsonString = new String(ResourceUtils.downloadFile(versionsJson));
//            jsonString = Files.readString(ResourceUtils.localResource("versions.json").toPath());
        } catch (IOException e) {
            return;
        }
//        System.out.println(jsonString);

        JsonElement jsonElement = gson.fromJson(jsonString, JsonElement.class);
        //Get the version
        latestVersion = jsonElement.getAsJsonObject().get("latest-version").getAsFloat();
        releases.clear();

        //Get changes since this version
        for (JsonElement release : jsonElement.getAsJsonObject().get("releases").getAsJsonArray()) {
            long v = release.getAsJsonObject().get("version").getAsLong();
            VersionChanges version = new VersionChanges(v);
            releases.add(version);
            JsonArray changes = release.getAsJsonObject().get("changes").getAsJsonArray();
            for (JsonElement change : changes) {
                version.changes.add(change.getAsString());
            }
        }

    }
}
