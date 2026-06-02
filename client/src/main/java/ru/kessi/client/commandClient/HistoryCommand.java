package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.exceptions.WrongParam;

public class HistoryCommand extends ru.kessi.common.commandManager.command.HistoryCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        if(args != null && args.length != 0) {
            throw new WrongParam("Введены лишние параметры");
        }
        if(ComHistory.getSize() <= 7) {
            ComHistory.printHistory();
        } else {
            for(int i = ComHistory.getSize() - 8; i < ComHistory.getSize();i++) {
                ComHistory.getCom(i);
            }
        }
        ComHistory.addCom(name, null);
        return null;
    }
}
