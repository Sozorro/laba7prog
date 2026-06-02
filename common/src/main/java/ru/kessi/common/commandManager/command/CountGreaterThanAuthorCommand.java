package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class CountGreaterThanAuthorCommand extends AbstractCommand {
    public CountGreaterThanAuthorCommand() {
        super("counterByWeight", "Вывести кол-во элементов, вес которых в поле \"author\"  больше заданного");
    }
}
