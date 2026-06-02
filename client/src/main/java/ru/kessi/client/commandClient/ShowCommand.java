package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;

public class ShowCommand extends ru.kessi.common.commandManager.command.ShowCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        ComHistory.addCom(name, null);
        return null;
    }
    
}