package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;

public class ClearCommand extends ru.kessi.common.commandManager.command.ClearCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        ComHistory.addCom(name, null);
        return null;
        /*long i = collectionManager.delLabs();
        System.out.println("Коллекция очищена, удалено " + i + " элемент" + (i == 1 ? "" : ((i < 5 || (i >= 20 && i % 10 == 0)) ? "ов" : "а")));
    */
    }
}
