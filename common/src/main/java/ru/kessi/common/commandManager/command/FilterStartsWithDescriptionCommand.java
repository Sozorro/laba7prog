package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class FilterStartsWithDescriptionCommand extends AbstractCommand {
    public FilterStartsWithDescriptionCommand() {
        super("filterStartsWithDescription", "вывести элементы, значение поля description которых начинается с заданной подстроки");
    }
    
}
