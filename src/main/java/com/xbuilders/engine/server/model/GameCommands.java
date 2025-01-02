package com.xbuilders.engine.server.model;

import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.server.model.items.item.Item;
import com.xbuilders.engine.server.model.items.item.ItemStack;
import com.xbuilders.engine.server.multiplayer.GameServer;
import com.xbuilders.engine.server.model.players.Player;

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
        commandHelp.put("mode", "Usage (to get the current mode): mode\n" +
                "Usage (to change mode): mode <mode>");
        commandHelp.put("op", "Usage: op <true/false> <player>");
        commandHelp.put("give", "Usage: give <item> <quantity (optional)>");
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
                        String str = "" + gameScene.world.players.size() + " players:\n";
                        for (Player client : gameScene.world.players) {
                            str += client.getName() + ";   " + client.getConnectionStatus() + "\n";
                        }
                        System.out.println("\nPLAYERS:\n" + str);
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
                            if (parts[1].equalsIgnoreCase("day") || parts[1].equalsIgnoreCase("morning")) {
                                GameScene.setTimeOfDay(0.0f);
                                return null;
                            } else if (parts[1].equalsIgnoreCase("evening")) {
                                GameScene.setTimeOfDay(0.25f);
                                return null;
                            } else if (parts[1].equalsIgnoreCase("night")) {
                                GameScene.setTimeOfDay(0.5f);
                                return null;
                            } else return commandHelp.get("time");
                        } else return commandHelp.get("time");
                    }
                    case "teleport" -> {
                        if (!GameScene.isOperator()) return null;

                        if (parts.length == 2) {
                            Player target = gameScene.server.getPlayerByName(parts[1]);
                            if (target != null) {
                                gameScene.userPlayer.worldPosition.set(target.worldPosition);
                                return null;
                            } else {
                                return "Player not found";
                            }
                        } else if (parts.length > 3) {
                            gameScene.userPlayer.worldPosition.set(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                        } else return commandHelp.get("teleport");
                    }
                    case "mode" -> {
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
                        } else return commandHelp.get("mode");
                    }
                    case "give" -> {
                        if (!GameScene.isOperator()) return null;
                        if (parts.length <= 3) {
                            try {
                                String itemID = formatGetItemID(parts[1]);
                                int quantity = parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 1;
                                Item item = Registrys.getItem(itemID);
                                if (item == null) return "Unknown item: " + itemID;
                                else GameScene.userPlayer.inventory.acquireItem(new ItemStack(item, quantity));
                                return "Given " + quantity + " " + item.name;
                            } catch (Exception e) {
                                return "Invalid";
                            }
                        } else return commandHelp.get("give");
                    }
                    case "op" -> {
                        if (!GameScene.ownsGame() || !GameScene.isOperator())
                            return null; //We cant change permissions if we arent the host
                        if (parts.length == 3) {
                            boolean operator = Boolean.parseBoolean(parts[1]);
                            Player target = gameScene.server.getPlayerByName(parts[2]);
                            if (target != null) {
                                target.sendData(new byte[]{GameServer.CHANGE_PLAYER_PERMISSION, (byte) (operator ? 1 : 0)});
                                return "Player " + target.userInfo.name + " has been " + (operator ? "given" : "removed") + " operator privileges";
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
                e.printStackTrace();
                return "Error handling command \"" + parts[0].toLowerCase() + "\": " + e.getMessage();
            }
        }
        return "Unknown command. Type 'help' for a list of commands";
    }

    private String formatGetItemID(String part) {
        part = part.toLowerCase().trim().replace(" ", "_");
        if (!part.startsWith("xbuilders:")) part = "xbuilders:" + part;
        return part;
    }

}
