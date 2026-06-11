package ru.kessi.server.commandServer;

import ru.kessi.server.managers.CollectionManager;

public class InfoCommand extends ru.kessi.common.commandManager.command.InfoCommand implements ServerCommand {
    @Override
    public String execute(String login, CollectionManager collectionManager, Object args) {
        return collectionManager.toString();
    }
    
}
