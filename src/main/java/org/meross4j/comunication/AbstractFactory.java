package org.meross4j.comunication;

import org.meross4j.command.Command;

abstract class AbstractFactory {
    abstract Command createCommandMode(String mode);
}
