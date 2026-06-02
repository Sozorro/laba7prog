package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class ClearCommand extends AbstractCommand {
    public ClearCommand() {
        super("clear", "Очистить коллекцию");
    }
}
