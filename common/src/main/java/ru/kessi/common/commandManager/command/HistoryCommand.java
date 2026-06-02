package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class HistoryCommand extends AbstractCommand {
    public HistoryCommand() {
        super("history", "Вывести 7 последних команд");
    }
    
}
