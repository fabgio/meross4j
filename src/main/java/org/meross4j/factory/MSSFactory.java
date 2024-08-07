package org.meross4j.factory;

import org.meross4j.command.Command;
import org.meross4j.command.MSSCommand;

public class MSSFactory extends AbstractFactory {
    @Override
    public Command commandMode(String mode) {
        return switch (mode){
            case "on" -> new MSSCommand.turnOn();
            case "off" -> new MSSCommand.turnOff();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
}
