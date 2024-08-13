package org.meross4j.factory;

import org.meross4j.command.Command;

public interface AbstractFactory {
    Command commandMode(String mode);
}
