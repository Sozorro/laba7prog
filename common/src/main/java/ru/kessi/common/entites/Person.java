package ru.kessi.common.entites;

import java.io.Serializable;

import ru.kessi.common.entites.enums.Color;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private double height; //Значение поля должно быть больше 0
    private long weight; //Значение поля должно быть больше 0
    private String passportID; //Поле может быть null
    private Color hairColor; //Поле не может быть null

    public Person (String name, double height, long weight, String passportID, Color hairColor) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID;
        this.hairColor = hairColor;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public long getWeight() {
        return weight;
    }

    public String getPassportID() {
        return passportID;
    }

    public Color getHairColor() {
        return hairColor;
}

    @Override
    public String toString() {
        return "Объект Person:\nName: " + name + "\n" +
            "Height: " + height + "\n" +
            "Weight: " + weight + "\n" +
            "Passport ID: " + passportID + "\n" +
            "Hair Color: " + hairColor.toString();
    }

    public String getTabString(int num) {
        String[] s = this.toString().split("\n");
        for(int i = 0; i < num; i++) {
            for(int j = 0; j < s.length; j++) {
                s[j] = "\t" + s[j];
            }
        }
        for(int j = 0; j < s.length; j++) {
            s[j] = s[j] + "\n";
        }
        return String.join("", s);
    }
}
