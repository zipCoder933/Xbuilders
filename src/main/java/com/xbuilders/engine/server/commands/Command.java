package com.xbuilders.engine.server.commands;

import com.xbuilders.engine.server.Server;

import java.util.function.Consumer;
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
        this.commandName = name.toLowerCase();
        this.commandHelp = help;
    }

    protected String runCommand(String[] input) {
        if(requiresOp && !Server.isOperator()) return null;
        if(handle!=null) return handle.apply(input);
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
