package ru.kessi.common.commandManager.command;

import ru.kessi.common.commandManager.AbstractCommand;

public class StopCommand extends AbstractCommand {
    public StopCommand() {
        super("stop", "Остановить сервер");
    }
}
