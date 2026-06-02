package ru.kessi.server.commandServer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.TreeSet;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.managers.CollectionManager;

public class SaveCommand extends ru.kessi.common.commandManager.command.SaveCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args){
        try {
            String str = "dop_doc/collection.csv";
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(str), "UTF-8")) {
                TreeSet<LabWork> collection = collectionManager.getElems();
                //CsvSaver.saveCollection(collection, writer);
                String head = "name; id; date; name; coordinatesX; coordinatesY; minimalPoint; personalQualitiesMinimum; description; difficulty;name; height; weight; passportID; hairColor;intparam;stringparam";
                writer.write(head + "\n");
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                for (LabWork laba : collection) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("add").append(";");
                    sb.append(laba.getId()).append(";");
                    sb.append(formatter.format(laba.getCreationDate())).append(";"); 
                    sb.append(laba.getName()).append(";");
                    sb.append(laba.getCoordinates().getX()).append(";");
                    sb.append(laba.getCoordinates().getY()).append(";");
                    sb.append(laba.getMinimalPoint()).append(";");
                    sb.append(laba.getPersonalQualitiesMinimum()).append(";");
                    sb.append(laba.getDescription()).append(";");
                    sb.append(laba.getDifficulty().toString()).append(";");
                    sb.append(laba.getAuthor().getName()).append(";"); 
                    sb.append(laba.getAuthor().getHeight()).append(";");
                    sb.append(laba.getAuthor().getWeight()).append(";");
                    sb.append(laba.getAuthor().getPassportID()).append(";");
                    sb.append(laba.getAuthor().getHairColor()).append(";");
                    sb.append(";");
                    sb.append("\n");
                    writer.write(sb.toString());

                }
                writer.flush();
            } catch (IOException e) {
                throw new WrongParam("Ошибка ввода");
            }
            return ("Коллекция сохранена в файл");
        } catch (Exception e) {
            System.out.println("Произошла непредвиденная ошибка. Создание элемента было остановлено и он не был добавлен в коллекцию");
            throw e;
        }
    
    }
}