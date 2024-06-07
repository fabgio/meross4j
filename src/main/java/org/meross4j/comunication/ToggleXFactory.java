package org.meross4j.comunication;

import org.meross4j.command.Command;
import org.meross4j.command.ToggleX;

public class ToggleXFactory  extends AbstractFactory{
    @Override
    Command createCommand(String mode) {
        return switch (mode){
            case "on" -> new ToggleX.turnOn();
            case "off" -> new ToggleX.turnOff();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
}
