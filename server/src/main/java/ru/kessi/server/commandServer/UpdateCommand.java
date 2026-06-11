package ru.kessi.server.commandServer;

import org.tinylog.Logger;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.managers.CollectionManager;

public class UpdateCommand extends ru.kessi.common.commandManager.command.UpdateCommand implements ServerCommand {
    @Override
    public String execute(String login, CollectionManager collectionManager, Object args){
        try { 
            if(args != null && args instanceof LabWork) {
                LabWork laba = (LabWork) args;
                return collectionManager.updateLab(login, laba);
            } else throw new WrongParam("Ошибка в элементе коллекции, такой объект невозможно добавить");
        } catch (Exception e) {
            Logger.error(e, "Ошибка при обновлении");
            Logger.info("Произошла непредвиденная ошибка. Элемент не был добавлен в коллекцию");
            throw e;
        }
    }
}
