package ru.kessi.client.builders;

import ru.kessi.common.Validator;

import ru.kessi.common.entites.Person;
import ru.kessi.common.entites.enums.Color;
import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.client.io.InputFile;

public class PersonBuilder extends Builder {
    public Person makePerson() throws WrongParam, WrongAction {
        while (true) {
            try {
                return new Person (
                makeName(),
                makeHeight(),
                makeWeight(),
                makePassportID(),
                makeHairColor()
                );
            } catch (WrongParam | WrongAction e) {
                throw e;
            }
        }
    }

    public Person makePerson(String name, String height, String weight, String passportID, String hairColor) throws WrongAction {
        while (true) {
            try {
                return new Person(
                    makeName(name),
                    makeHeight(height),
                    makeWeight(weight),
                    makePassportID(passportID),
                    makeHairColor(hairColor)
                );
            } catch (WrongParam e) {
                return makePerson();
            } catch (WrongAction e) {
                throw e;
            }
        }
    }
    public Person makePerson(String name, double height, long weight, String passportID, Color hairColor) {
        return new Person (name, height, weight, passportID, hairColor);
    }

    public String makeName(String... args) throws WrongParam, WrongAction {
        String s = "Введите имя: ";
        String ext = "Недопустимое название, проверьте корректность ввода";

        if ((args == null || args.length == 0) && InputFile.readFile == false) {
            args = DEFAULT_GETPAR.apply(s);
        }

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
        }, DEFAULT_GETPAR, args, s);
        return result;
    }

    public double makeHeight(String... args) throws WrongParam, WrongAction {
        String s = "Введите рост: ";
        String ext = "Некорректный рост";

        if ((args == null || args.length == 0) && InputFile.readFile == false) {
            args = DEFAULT_GETPAR.apply(s);
        }

        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                try {
                    double input = Double.valueOf(params[0]);
                    if (Validator.validHeightForPerson(input)) {
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
        }, DEFAULT_GETPAR, args, s);
    }

    public long makeWeight(String... args) throws WrongParam, WrongAction {
        String s = "Введите вес: ";
        String ext = "Некорректный вес";

        if ((args == null || args.length == 0) && InputFile.readFile == false) {
            args = DEFAULT_GETPAR.apply(s);
        }

        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                try {
                    Long input = Long.valueOf(params[0]);
                    if (Validator.validWeightForPerson(input)) {
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
        }, DEFAULT_GETPAR, args, s);
    }

    public String makePassportID(String... args) throws WrongParam, WrongAction {
        String s = "Введите номер паспорта: ";
        String ext = "Некорректный номер паспорта";

        if ((args == null || args.length == 0) && InputFile.readFile == false) {
            args = DEFAULT_GETPAR.apply(s);
        }

        String result = interactInputRetry((params) -> {
            if (params != null && params.length != 0) {
                String input = String.join("", params).toString().trim();
                if (Validator.validPassportIDForPerson(input)) {
                    return input;
                } else {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, DEFAULT_GETPAR, args, s);
        return result;
    }

    public Color makeHairColor(String... args) throws WrongParam, WrongAction {
        String s = ("Введите цвет волос: \n" +
                    "\t Доступные значения: \n \t 1. YELLOW \n \t 2. ORANGE \n \t 3. WHITE");
        String ext = "Некорректный цвет, проверьте корректность ввода";

        if ((args == null || args.length == 0) && InputFile.readFile == false) {
            args = DEFAULT_GETPAR.apply(s);
        }

        return interactInputRetry((params) -> {
            if (params != null && params.length == 1) {
                Color input = null;
                try {
                    int i = Integer.parseInt(params[0]);
                    input = Color.getVal(i);
                } catch (NumberFormatException notNum) {
                    try {
                        input = Color.valueOf(params[0]);
                    } catch (IllegalArgumentException notZnach) {
                        throw new WrongParam(ext);
                    }
                }
                if (Validator.validHairColorForPerson(input)) {
                    return input;
                } else {
                    throw new WrongParam(ext);
                }
            } else {
                throw new WrongParam(ext);
            }
        }, DEFAULT_GETPAR, args, s);
    }
}
