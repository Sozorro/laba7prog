package ru.kessi.server.commandServer;

import org.tinylog.Logger;

import ru.kessi.common.entites.Person;
import ru.kessi.common.entites.enums.Color;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.builders.PersonBuilder;
import ru.kessi.server.managers.CollectionManager;

public class CountGreaterThanAuthorCommand extends ru.kessi.common.commandManager.command.CountGreaterThanAuthorCommand implements ServerCommand {
    @Override
    public String execute(String login, CollectionManager collectionManager, Object args) {
        try {
            if(args != null && args instanceof String) {
                PersonBuilder personBuilder = new PersonBuilder();
                Person author = personBuilder.makePerson("", 0.0, Long.valueOf((String) args), "", Color.WHITE);
                return ("Кол-во элементов, вес которых больше, чем " + ((String) args) + ": " + collectionManager.findElemsHeavierPerson(author).size());
            } else throw new WrongParam("Ошибка в элементе коллекции такой объект невозможно добавить");
        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
    }
}
