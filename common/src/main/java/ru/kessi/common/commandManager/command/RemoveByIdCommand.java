package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class RemoveByIdCommand extends AbstractCommand {
    public RemoveByIdCommand() {
        super("remove", "удалить элемент из коллекции по его id");
    }
}
