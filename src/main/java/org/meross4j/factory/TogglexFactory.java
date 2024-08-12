package org.meross4j.factory;

import org.meross4j.command.Command;
import org.meross4j.command.TogglexCommand;

public class TogglexFactory extends AbstractFactory {
    @Override
    public Command commandMode(String mode) {
        return switch (mode){
            case "ON" -> new TogglexCommand.turnOn();
            case "OFF" -> new TogglexCommand.turnOff();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
}
