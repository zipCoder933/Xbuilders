package com.xbuilders.engine.server.commands;

import com.xbuilders.Main;
import com.xbuilders.engine.client.settings.ClientSettings;

import java.util.Objects;
import java.util.function.Function;

public class Command {
    public final String commandName;
    public final String commandHelp;

    @FunctionalInterface
    public interface CommandFunction extends Function<String[], String> {
        // No need to declare the abstract method apply(T t) here
        // because Function already declares it and this interface inherits it.

        // You can add default or static methods if needed,
        // but the core requirement of a functional interface is a single abstract method.
    }

    public CommandFunction handle;
    public boolean requiresOp = false;

    public Command(String name, String help) {
        this.commandName = Objects.requireNonNull(name).toLowerCase();
        this.commandHelp = Objects.requireNonNull(help);
    }

    protected String runCommand(String[] input) {
        if (
                (requiresOp
                        && !Main.getServer().isOperator()
                        && !ClientSettings.load().internal_allowOPCommands)
        ) return "You do not have the required permissions";
        if (handle != null) return handle.apply(input);
        return null;
    }

    public Command executes(CommandFunction handle) {
        this.handle = handle;
        return this;
    }

    public Command requiresOP(boolean requiresOp) {
        this.requiresOp = requiresOp;
        return this;
    }
}
