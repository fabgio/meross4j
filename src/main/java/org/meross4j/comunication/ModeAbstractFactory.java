package org.meross4j.comunication;

import org.meross4j.command.Command;

abstract class ModeAbstractFactory {
    abstract Command createCommand(String mode);
}
