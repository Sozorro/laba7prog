package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;

public class ExitCommand extends ru.kessi.common.commandManager.command.ExitCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        ComHistory.addCom(name, null);
        return null;
    }
    @Override
    public String toString() {
        return "exit";
    }
    //scan.close();?
}
