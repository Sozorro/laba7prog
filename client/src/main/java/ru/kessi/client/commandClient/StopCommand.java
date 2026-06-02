package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;

public class StopCommand extends ru.kessi.common.commandManager.command.StopCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        ComHistory.addCom(name, null);
        return null;
    }
    @Override
    public String toString() {
        return "stop";
    }
    //scan.close();?
}
