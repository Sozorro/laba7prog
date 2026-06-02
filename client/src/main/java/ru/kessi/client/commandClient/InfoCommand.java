package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;

public class InfoCommand extends ru.kessi.common.commandManager.command.InfoCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        ComHistory.addCom(name, null);
        return null;
    }
    
}
