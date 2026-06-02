package ru.kessi.server.builders;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ru.kessi.common.Validator;
import ru.kessi.common.entites.Coordinates;
import ru.kessi.common.entites.LabWork;
import ru.kessi.common.entites.Person;
import ru.kessi.common.entites.enums.Difficulty;
import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;

public class LabWorkBuilder extends Builder {

    public LabWork makeLabWork() throws WrongAction { // интерактивный ввод всех полей, кроме времени
        while (true) {
            try {
                return new LabWork(
                    new java.util.Date(),
                    makeName(),
                    makeCoordinates(),
                    makeMinimalPoint(),
                    makePersonalQualitiesMinimum(), 
                    makeDescription(),
                    makeDifficulty(), 
                    makePerson()
                );
            } catch (WrongParam e) {
                continue;
            } catch (WrongAction e) {
                throw e;
            }
        }
    }

    public LabWork makeLabWork(String date, String name, String coordinatesX, String coordinatesY, String minimalPoint, String personalQualitiesMinimum,
        String description, String difficulty, String... author) throws WrongAction { //установка всех полей
        while (true) {
            try {
                return new LabWork(
                    makeDate(date),
                    makeName(name),
                    makeCoordinates(coordinatesX, coordinatesY), 
                    makeMinimalPoint(minimalPoint), 
                    makePersonalQualitiesMinimum(personalQualitiesMinimum), 
                    makeDescription(description),
                    makeDifficulty(difficulty), 
                    makePerson(author)
                );
            } catch (WrongParam e) {
                return makeLabWork();
            } catch (WrongAction e) {
                throw e;
            }
        }
    }

    public LabWork makeLabWork(java.util.Date date, String name, Coordinates coordinates, int minimalPoint, int personalQualitiesMinimum,
        String description, Difficulty difficulty, Person author) { //насильная установка :)
        return new LabWork(
            date, 
            name, 
            coordinates, 
            minimalPoint, 
            personalQualitiesMinimum,
            description, 
            difficulty, 
            author
        );        
    }

    public java.util.Date makeDate(String... args) throws WrongParam, WrongAction {
        String s = "Введите дату в формате \"dd.MM.yyyy\": ";
        String ext = "Неверный формат даты, проверьте корректность ввода";


        java.util.Date date = interactInputRetry((params) -> {
            if(params != null && params.length == 1) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                    java.util.Date makeDate = formatter.parse(params[0].toString());
                    if (Validator.validDateForLabWork(makeDate)) {
                        return makeDate;
                    } else {
                        throw new WrongParam(ext);
                    }
                } catch (ParseException e) {
                    throw new WrongParam("Ошибка парсинга, проверьте корректность ввода");
                }
            } else {
                throw new WrongParam(ext);
            } 
        }, args, s);
        return date;
    }

    public String makeName(String... args) throws WrongParam, WrongAction {
        String s = "Введите название лабораторной: ";
        String ext = "Недопустимое название, проверьте корректность ввода";


        String result = interactInputRetry((params) -> {
            if (params != null && params.length != 0) {
                String input = String.join("", params).toString().trim();
                if (Validator.validNameForLabWork(input)) {
                    return input;
                } else {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
        return result;
    }

    public Coordinates makeCoordinates(String... args) throws WrongParam, WrongAction {
        String s = "Введите координаты х и у (в одну строку через пробел или по очереди сначала x, затем у): ";
        String ext = "Неверный формат координат, проверьте корректность ввода";

        Coordinates result = interactInputRetry((params) -> {
            String[] coords = new String[2];
            if (params != null && params.length == 2) {
                try {
                    coords[0] = params[0];
                    coords[1] = params[1];
                    Coordinates coord = new Coordinates(Float.valueOf(coords[0]), Float.valueOf(coords[1]));
                    if (Validator.validCoordinatesForLabWork(coord)) {
                        return coord;
                    } else {
                        throw new WrongParam(ext);
                    }
                } catch (NumberFormatException e) {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
        return result;
    }   

    public int makeMinimalPoint(String... args) throws WrongParam, WrongAction {
        String s = "Введите значение makeMinimalPoint: ";
        String ext = "Некорректное значение makeMinimalPoint";


        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                try {
                    Integer input = Integer.valueOf(params[0]);
                    if (Validator.validMinimalPointForLabWork(input)) {
                        return input;
                    } else {
                        throw new WrongParam(ext);
                    }
                } catch (NumberFormatException e) {
                    throw new WrongParam(ext);
                } 
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
    }

    public int makePersonalQualitiesMinimum(String... args) throws WrongParam, WrongAction {
        String s = "Введите минимальные личные качества: ";
        String ext = "Некорректные личные качества";


        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                try {
                    Integer input = Integer.valueOf(params[0]);
                    if (Validator.validPersonalQualitiesMinimumForLabWork(input)) {
                        return input;
                    } else {
                        throw new WrongParam(ext);
                    }
                } catch (NumberFormatException e) {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
    }

    public String makeDescription(String... args) throws WrongParam, WrongAction {
        String s = "Введите описание: ";
        String ext = "Некорректное описание";


        return interactInputRetry((params) -> {
            if (params != null && params.length != 0) {
                String input = String.join("", params).toString().trim();
                if (Validator.validDescriptionForLabWork(input)) {
                    return input;
                } else {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
    }

    public Difficulty makeDifficulty(String... args) throws WrongParam, WrongAction {
        String s = ("Введите сложность работы или выберете цифру: \n" +
                    "\tДоступные значения: \n \t1. HARD \n \t2. VERY_HARD \n \t3. INSANE");
        String ext = "Неверный формат сложности, проверьте корректность ввода";
        

        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                Difficulty input = null;
                try {
                    int i = Integer.parseInt(params[0]);
                    input = Difficulty.getVal(i);
                } catch (NumberFormatException notNum) {
                    try {
                        input = Difficulty.valueOf(params[0]);
                    } catch (IllegalArgumentException notZnach) {
                        throw new WrongParam(ext);
                    }
                }
                if (Validator.validDifficultyForLabWork(input)) {
                    return input;
                } else {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, args, s);
    }


    public Person makePerson(String... args) throws WrongParam, WrongAction {
        PersonBuilder personBuilder = new PersonBuilder();
        Person person = null;
        if (args.length == 5) person = personBuilder.makePerson(args[0], args[1], args[2], args[3], args[4]);
        else person = personBuilder.makePerson();
        return person;
    }
}