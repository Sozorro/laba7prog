package ru.kessi.client.commandClient;

import ru.kessi.client.builders.LabWorkBuilder;
import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongAction;

public class AddCommand extends ru.kessi.common.commandManager.command.AddCommand implements ClientCommand {
    @Override
    public LabWork execute(String... args){
        try { 
            LabWorkBuilder labWorkBuilder = new LabWorkBuilder();
            LabWork laba = null;
            if (args.length == 13) laba = labWorkBuilder.makeLabWork(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
            else if (InputFile.readFile == false) laba = labWorkBuilder.makeLabWork();
            else {
                System.out.println("\tВведены не все параметры или они некорректно заданы, хотите ввести их ещё раз в интерактивном режиме? \n" );
                String prov = null;
                while (prov == null) {
                    prov = Input.getParams("\tВведите: \"(y)yes\" или \"(n)no\" \n");
                }
                if (prov.equals("yes") || prov.equals("y")) {
                    laba = labWorkBuilder.makeLabWork();
                } else {
                    System.out.println("Комнда была пропущена");
                    throw new WrongAction();
                }
            }
            if(laba == null) throw new WrongAction();
            ComHistory.addCom(name, laba.toString());
            return laba;
            //java.util.Date date, String name, Coordinates coordinates, int minimalPoint, int personalQualitiesMinimum,
            //String description, Difficulty difficulty, Person author
            //date, name, coordinatesX, coordinatesY, minimalPoint, personalQualitiesMinimum, description, difficulty, Person(name, height, weight, passportID, hairColor
        
        } //catch (WrongParam e) {
         //   System.out.println("Ошибка ввода. Элемент не был добавлен");
        //} 
        catch (WrongAction e) {
            System.out.println("Создание элемента было остановлено и он не был добавлен в коллекцию");
            throw e;
        } catch (Exception e) {
            System.out.println("Произошла непредвиденная ошибка. Создание элемента было остановлено и он не был добавлен в коллекцию");
            throw e;
        }
    }
}
