package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class SaveCommand extends AbstractCommand {
    public SaveCommand() {
        super("save", "сохранить коллекцию в файл");
    }
    
}