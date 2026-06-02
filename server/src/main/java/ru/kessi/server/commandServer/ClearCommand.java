package ru.kessi.server.commandServer;

import ru.kessi.server.managers.CollectionManager;

public class ClearCommand extends ru.kessi.common.commandManager.command.ClearCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args) {
        long i = collectionManager.delLabs();
        return ("Коллекция очищена, удалено " + i + " элемент" + (i == 1 ? "" : ((i < 5 || (i >= 20 && i % 10 == 0)) ? "ов" : "а")));
    }
}
