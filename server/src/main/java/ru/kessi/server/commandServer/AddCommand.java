package ru.kessi.server.commandServer;

import org.tinylog.Logger;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.managers.CollectionManager;

public class AddCommand extends ru.kessi.common.commandManager.command.AddCommand implements ServerCommand {
    @Override
    public String execute(String login, CollectionManager collectionManager, Object args){
        try { 
            if(args != null && args instanceof LabWork) {
                return collectionManager.addLab(login, (LabWork) args);
            } else throw new WrongParam("Ошибка в элементе коллекции такой объект невозможно добавить");
        } catch (Exception e) {
            Logger.error(e);
            Logger.info("Произошла непредвиденная ошибка. Создание элемента было остановлено и он не был добавлен в коллекцию");
            throw e;
        }
    }
}
