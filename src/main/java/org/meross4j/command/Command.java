package org.meross4j.command;

public interface Command {
    byte[] createCommandType(String type);
}
