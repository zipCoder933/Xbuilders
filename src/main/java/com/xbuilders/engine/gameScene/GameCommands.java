package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.multiplayer.PlayerClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCommands {
    public final HashMap<String, String> commandHelp;
    GameScene gameScene;
    Game game;

    public GameCommands(GameScene gameScene, Game game) {
        this.gameScene = gameScene;
        this.game = game;

        commandHelp = new HashMap<>();
        commandHelp.put("msg", "Usage: msg <player/all> <message>");
        commandHelp.put("gamemode", "Usage (to get the current mode): gamemode\n" +
                "Usage (to change mode): gamemode <mode>");
        commandHelp.put("op", "Usage: op <true/false> <player>");
        commandHelp.put("help", "Usage: help <command>");
        commandHelp.put("time", "Usage: time <day/evening/night>");
        commandHelp.put("players", "Lists all connected players");
        commandHelp.put("teleport", "Usage: teleport <player>" +
                "\nUsage: teleport <x> <y> <z>");
        commandHelp.put("address", "Returns the server's address");
        if (game.getCommandHelp() != null) commandHelp.putAll(game.getCommandHelp());
    }

    private static String[] splitWhitespacePreserveQuotes(String input) {
        ArrayList<String> parts = new ArrayList<>();

        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1)); // Add double-quoted string without quotes
            } else if (matcher.group(2) != null) {
                parts.add(matcher.group(2)); // Add single-quoted string without quotes
            } else {
                parts.add(matcher.group()); // Add unquoted word
            }
        }

        return parts.toArray(new String[0]);
    }

    public String handleGameCommand(String command) {
        String[] parts = splitWhitespacePreserveQuotes(command);
        System.out.println("handleGameCommand: " + Arrays.toString(parts));
        if (parts.length > 0) {
            try {
                switch (parts[0].toLowerCase()) {
                    case "address" -> {
                        return gameScene.server.getIpAdress() + ":" + gameScene.server.getPort();
                    }
                    case "help" -> {
                        String out = "Available commands:\n";
                        for (Map.Entry<String, String> entry : commandHelp.entrySet()) {
                            if (entry.getValue().contains("\n")) {
                                String[] lines = entry.getValue().split("\n");
                                for (String line : lines) {
                                    out += entry.getKey() + "\t    " + line + "\n";
                                }
                            } else out += entry.getKey() + "\t    " + entry.getValue() + "\n";
                        }
                        return out;
                    }
                    case "players" -> {
                        String str = "" + gameScene.server.clients.size() + " players:\n";
                        for (PlayerClient client : gameScene.server.clients) {
                            str += client.player.name + "\n";
                        }
                        return str;
                    }
                    case "msg" -> {
                        if (parts.length > 2) {
                            return gameScene.server.sendChatMessage(parts[1], parts[2]);
                        } else return commandHelp.get("msg");
                    }
                    case "time" -> {
                        if (!GameScene.isOperator()) return null;
                        if (parts.length == 2) {
                            //It doesnt matter if we had 2 players with different time
//                            if(!server.isHosting() && server.isPlayingMultiplayer()) return "You cannot change time";
                            if (parts[1].toLowerCase().equals("day") || parts[1].toLowerCase().equals("morning")) {
                                gameScene.setTimeOfDay(0.0f);
                                return null;
                            } else if (parts[1].toLowerCase().equals("evening")) {
                                gameScene.setTimeOfDay(0.25f);
                                return null;
                            } else if (parts[1].toLowerCase().equals("night")) {
                                gameScene.setTimeOfDay(0.5f);
                                return null;
                            } else return commandHelp.get("time");
                        } else return commandHelp.get("time");
                    }
                    case "teleport" -> {
                        if (!GameScene.isOperator()) return null;

                        if (parts.length == 2) {
                            PlayerClient target = gameScene.server.getPlayerByName(parts[1]);
                            if (target != null) {
                                gameScene.player.worldPosition.set(target.player.worldPosition);
                                return null;
                            } else {
                                return "Player not found";
                            }
                        } else if (parts.length > 3) {
                            if (!gameScene.server.isPlayingMultiplayer() || gameScene.server.isHosting()) {
                                gameScene.player.worldPosition.set(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                            } else {
                                return "You cannot teleport";
                            }
                        } else return commandHelp.get("teleport");
                    }
                    case "gamemode" -> {
                        if (!GameScene.isOperator()) return null;

                        if (parts.length == 1) {
                            return "Game mode: " + GameScene.getGameMode();
                        } else if (parts.length == 2) {
                            String mode = parts[1].toUpperCase().trim().replace(" ", "_");
                            try {
                                GameScene.setGameMode(GameMode.valueOf(mode.toUpperCase()));
                                if (gameScene.server.isPlayingMultiplayer())
                                    gameScene.server.sendToAllClients(new byte[]{GameServer.CHANGE_GAME_MODE, (byte) GameScene.getGameMode().ordinal()});
                                return "Game mode changed to: " + GameScene.getGameMode();
                            } catch (IllegalArgumentException e) {
                                return "Unknown game mode: " + mode;
                            }
                        } else return commandHelp.get("gamemode");
                    }
                    case "op" -> {
                        if (!GameScene.ownsGame() || !GameScene.isOperator())
                            return null; //We cant change permissions if we arent the host
                        if (parts.length == 3) {
                            boolean operator = Boolean.parseBoolean(parts[1]);
                            PlayerClient target = gameScene.server.getPlayerByName(parts[2]);
                            if (target != null) {
                                target.sendData(new byte[]{GameServer.CHANGE_PLAYER_PERMISSION, (byte) (operator ? 1 : 0)});
                                return "Player " + target.player.name + " has been " + (operator ? "given" : "removed") + " operator privileges";
                            } else {
                                return "Player not found";
                            }
                        } else return commandHelp.get("permission");
                    }
                    default -> {
                        String out = game.handleCommand(parts);
                        if (out != null) {
                            return out;
                        }
                    }
                }
            } catch (Exception e) {
                return "Error handling command \"" + parts[0].toLowerCase() + "\": " + e.getMessage();
            }
        }
        return "Unknown command. Type 'help' for a list of commands";
    }

}