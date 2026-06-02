package ru.kessi.common;

import ru.kessi.common.entites.Coordinates;
import ru.kessi.common.entites.Person;
import ru.kessi.common.entites.enums.Color;
import ru.kessi.common.entites.enums.Difficulty;

public class Validator {
    public static boolean validDateForLabWork(java.util.Date date) {
        return true;
    }
    public static boolean validNameForLabWork(String name) {
        return true;
    }
    public static boolean validCoordinatesForLabWork(Coordinates coordinates) {
        return true;
    }
    public static boolean validMinimalPointForLabWork(int minimalPoint) {
        return true;
    }
    public static boolean validPersonalQualitiesMinimumForLabWork(int personalQualitiesMinimum) {
        return true;
    }
    public static boolean validDescriptionForLabWork(String description) {
        return true;
    }
    public static boolean validDifficultyForLabWork(Difficulty difficulty) {
        return true;
    }
    public static boolean validPerson(Person author) {
        return true;
    }
    
    public static boolean validNameForPerson(String name) {
        return true;
    }
    public static boolean validHeightForPerson(double height) {
        return true;
    }
    public static boolean validWeightForPerson(long weight) {
        return true;
    }
    public static boolean validPassportIDForPerson(String passportID) {
        return true;
    }
    public static boolean validHairColorForPerson(Color hairCol) {
        return true;
    }
    
}
