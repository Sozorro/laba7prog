package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class HelpCommand extends AbstractCommand {
    public HelpCommand() {
        super("help", "Вывести справку по доступным командам");
    }
    
}
