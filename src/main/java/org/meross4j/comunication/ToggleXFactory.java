package org.meross4j.comunication;

import org.meross4j.command.Command;
import org.meross4j.command.ToggleX;

public class ToggleXFactory  extends AbstractFactory{
    @Override
    Command createCommandMode(String mode) {
        return switch (mode){
            case "on" -> new ToggleX.turnOn();
            case "off" -> new ToggleX.turnOff();
            case "abilities" -> new ToggleX.abilities();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
}
