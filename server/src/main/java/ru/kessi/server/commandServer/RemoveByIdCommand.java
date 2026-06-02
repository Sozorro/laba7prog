package ru.kessi.server.commandServer;


import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.managers.CollectionManager;

public class RemoveByIdCommand extends ru.kessi.common.commandManager.command.RemoveByIdCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args) {
        try {
            if(args != null && args instanceof String) {
                return collectionManager.delLab(Long.parseLong((String) args));
            } else throw new WrongParam();
        } catch (NumberFormatException e) {
            return ("Неверный формат id");
        } catch (Exception e) {
            return ("Комнда была остановлена");
        }
    }
}
