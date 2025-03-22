package com.xbuilders.engine.server.commands;

import com.xbuilders.engine.server.Difficulty;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Game;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.multiplayer.GameServer;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.utils.ErrorHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCommands {
    private final HashMap<String, Command> commands = new HashMap<>();
    Server gameScene;
    Game game;

    public void registerCommand(Command command) {
        commands.put(command.commandName.toLowerCase(), command);
    }

    private String[] removeFirstN(String[] input, int n) {
        if (input == null || input.length == 0 || n <= 0) {
            return input; // Handle null, empty array, or non-positive n
        }
        if (n >= input.length) {
            return new String[0]; // If n is greater than or equal to the length, return an empty array
        }
        return Arrays.copyOfRange(input, n, input.length);
    }

    public GameCommands(Server gameScene, Game game) {
        this.gameScene = gameScene;
        this.game = game;

        registerCommand(new Command("tickrate", "Sets the random tick likelihood. Usage: tickrate <ticks>")
                .requiresOP(true).executes((parts) -> {
                    if (parts.length >= 1) {
                        Chunk.randomTickLikelyhoodMultiplier = (float) Double.parseDouble(parts[0]);
                        return "Tick rate changed to: " + Chunk.randomTickLikelyhoodMultiplier;
                    }
                    return "Tick rate is " + Chunk.randomTickLikelyhoodMultiplier;
                }));

        registerCommand(new Command("mode",
                "Usage (to get the current mode): mode\n" +
                        "Usage (to change mode): mode <mode> <all (optional)>")
                .requiresOP(true).executes((parts) -> {
                    if (parts.length >= 1) {
                        String mode = parts[0].toUpperCase().trim().replace(" ", "_");

                        boolean sendToAll = (parts.length >= 2 && parts[1].equalsIgnoreCase("all"));
                        try {
                            Server.setGameMode(GameMode.valueOf(mode.toUpperCase()));
                            if (sendToAll && Server.server.isPlayingMultiplayer())
                                Server.server.sendToAllClients(new byte[]{GameServer.CHANGE_GAME_MODE, (byte) Server.getGameMode().ordinal()});
                            return "Game mode changed to: " + Server.getGameMode();

                        } catch (IllegalArgumentException e) {

                            return "No game mode \"" + mode + "\" Valid game modes are "
                                    + Arrays.toString(GameMode.values());

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return "Game mode: " + Server.getGameMode();
                    }
                }));

        registerCommand(new Command("op", "Usage: op <true/false> <player>")
                .requiresOP(true).executes((parts) -> {
                    if (!Server.ownsGame())
                        return "Only the host can change OP status"; //We cant change permissions if we arent the host
                    if (parts.length >= 2) {
                        boolean operator = Boolean.parseBoolean(parts[0]);
                        Player target = Server.server.getPlayerByName(parts[1]);
                        if (target != null) {
                            try {
                                target.sendData(new byte[]{GameServer.CHANGE_PLAYER_PERMISSION, (byte) (operator ? 1 : 0)});
                            } catch (IOException e) {
                                return "Error: " + e;
                            }
                            return "Player " + target.userInfo.name + " has been " + (operator ? "given" : "removed") + " operator privileges";
                        } else {
                            return "Player not found";
                        }
                    }
                    return null;
                }));

        registerCommand(new Command("address", "Returns the server's address")
                .executes((parts) -> Server.server.getIpAdress() + ":" + Server.server.getPort()));

        registerCommand(new Command("msg",
                "Usage: msg <player/all> <message>").executes((parts) -> {
            if (parts.length >= 2) {
                return Server.server.sendChatMessage(parts[0], parts[1]);
            }
            return null;
        }));

        registerCommand(new GiveCommand());

        registerCommand(new Command("time",
                "Usage: time <day/evening/night>")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 1) {
                        if (parts[0].equalsIgnoreCase("morning") || parts[0].equalsIgnoreCase("m")) {
                            Server.setTimeOfDay(0.95f);
                            return null;
                        } else if (parts[0].equalsIgnoreCase("day") || parts[0].equalsIgnoreCase("d")) {
                            Server.setTimeOfDay(0.0f);
                            return null;
                        } else if (parts[0].equalsIgnoreCase("evening") || parts[0].equalsIgnoreCase("e")) {
                            Server.setTimeOfDay(0.25f);
                            return null;
                        } else if (parts[0].equalsIgnoreCase("night") || parts[0].equalsIgnoreCase("n")) {
                            Server.setTimeOfDay(0.5f);
                            return null;
                        }
                    }
                    return null;
                }));

        registerCommand(new Command("teleport",
                "Usage: teleport <player>\nUsage: teleport <x> <y> <z>")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 3) {
                        GameScene.userPlayer.worldPosition.set(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                        return null;
                    }

                    if (parts.length >= 1) {
                        Player target = Server.server.getPlayerByName(parts[0]);
                        if (target != null) {
                            GameScene.userPlayer.worldPosition.set(target.worldPosition);
                            return null;
                        } else {
                            return "Player not found";
                        }
                    }
                    return null;
                }));

        registerCommand(new Command("difficulty",
                "Usage: difficulty <easy/normal/hard>")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 1) {
                        String mode = parts[0].toUpperCase().trim().replace(" ", "_");
                        try {
                            Server.setDifficulty(Difficulty.valueOf(mode.toUpperCase()));
                            if (Server.server.isPlayingMultiplayer())
                                Server.server.sendToAllClients(new byte[]{
                                        GameServer.CHANGE_DIFFICULTY, (byte) Server.getDifficulty().ordinal()});
                            return "Difficulty changed to: " + Server.getDifficulty();
                        } catch (IllegalArgumentException e) {
                            return "Invalid mode \"" + mode + "\" Valid modes are "
                                    + Arrays.toString(Difficulty.values());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return "Difficulty: " + Server.getDifficulty();
                }));

        registerCommand(new Command("list",
                "Lists all connected players")
                .requiresOP(true)
                .executes((parts) -> {
                    StringBuilder str = new StringBuilder(Server.world.players.size() + " players:\n");
                    for (Player client : Server.world.players) {
                        str.append(client.getName()).append(";   ").append(client.getConnectionStatus()).append("\n");
                    }
                    System.out.println("\nPLAYERS:\n" + str);
                    return str.toString();
                }));

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

    public String handleGameCommand(String inputString) {
        try {

            String[] parts = splitWhitespacePreserveQuotes(inputString);
            System.out.println("handleGameCommand: " + Arrays.toString(parts));
            if (parts.length == 0) return null;

            if (parts[0].equalsIgnoreCase("help")) { //Help builtin command
                if (parts.length >= 2) {
                    Command command = commands.get(parts[1].toLowerCase());
                    if (command == null) return "Unknown command.";
                    else return command.commandHelp;
                }
                final StringBuilder out = new StringBuilder("Commands:\n");
                commands.forEach((key, command) -> {
                    out.append(key).append(": ").append(command.commandHelp).append("\n\n");
                });
                return out.toString();
            } else { //Other commands
                Command command = commands.get(parts[0].toLowerCase());
                if (command == null) return "Unknown command. Type 'help' for a list of commands";
                else {
                    parts = removeFirstN(parts, 1);
                    String out = command.runCommand(parts);
                    if (out == null) return command.commandHelp;
                    else return out;
                }
            }

        } catch (Exception e) {
            ErrorHandler.log(e);
            return "Error with command: " + e;
        }
    }


}
