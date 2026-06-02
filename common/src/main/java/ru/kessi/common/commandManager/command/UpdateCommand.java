package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class UpdateCommand extends AbstractCommand {
    public UpdateCommand() {
        super("update", "обновить значение элемента коллекции, id которого равен заданному");
    }
}
