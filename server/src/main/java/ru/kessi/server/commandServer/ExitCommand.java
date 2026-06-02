package ru.kessi.server.commandServer;

import ru.kessi.server.managers.CollectionManager;

public abstract class ExitCommand extends ru.kessi.common.commandManager.command.ExitCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args) {
        return null;
    }
    //scan.close();?
}
