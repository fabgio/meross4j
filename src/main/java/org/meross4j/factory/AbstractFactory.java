package org.meross4j.factory;

import org.meross4j.command.Command;

public abstract class AbstractFactory {
    public abstract Command commandMode(String mode);
}
