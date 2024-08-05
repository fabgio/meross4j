package org.meross4j.factory;

import org.meross4j.command.Command;
import org.meross4j.command.ToggleXCommand;

public class ToggleXFactory  extends AbstractFactory {
    @Override
    public Command commandMode(String mode) {
        return switch (mode){
            case "on" -> new ToggleXCommand.turnOn();
            case "off" -> new ToggleXCommand.turnOff();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
}
