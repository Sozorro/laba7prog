package ru.kessi.common.entites;

import java.io.Serializable;

import ru.kessi.common.entites.enums.Difficulty;

public class LabWork implements Serializable {
    //объекты коллекции

    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int minimalPoint; //Значение поля должно быть больше 0
    private int personalQualitiesMinimum; //Значение поля должно быть больше 0
    private String description; //Длина строки не должна быть больше 3271, Поле не может быть null
    private Difficulty difficulty; //Поле может быть null
    private Person author; //Поле может быть 
    
    public LabWork (java.util.Date date, String name, Coordinates coordinates, int minimalPoint, int personalQualitiesMinimum,
        String description, Difficulty difficulty, Person author) {
        
        this.creationDate = date;

        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.description = description;
        this.difficulty = difficulty;
        this.author = author;
    }
    
    public void setId(Long idCounter) {
        this.id = idCounter;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public int getMinimalPoint() {
        return minimalPoint;
    }

    public int getPersonalQualitiesMinimum() {
        return personalQualitiesMinimum;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Объект LabWork:\nID: " + id + "\n" +
            "Name: " + name + "\n" +
            "Coordinates: " + coordinates.toString() + "\n" +
            "Creation Date: " + creationDate.toString() + "\n" +
            "Minimal Point: " + minimalPoint + "\n" +
            "Personal Qualities Minimum: " + personalQualitiesMinimum + "\n" +
            "Description: " + description + "\n" +
            "Difficulty: " + difficulty.toString() + "\n" +
            "Author: \n" + author.getTabString(1);
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
