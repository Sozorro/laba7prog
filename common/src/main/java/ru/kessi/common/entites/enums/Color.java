package ru.kessi.common.entites.enums;

import java.io.Serializable;
import java.util.Arrays;

import ru.kessi.common.exceptions.WrongParam;

public enum Color implements Serializable {
    YELLOW(1),
    ORANGE(2),
    WHITE(3);


    private final int num;

    Color(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    public static Color getVal(int num) throws WrongParam{
        return Arrays.stream(values())
            .filter(col -> col.getNum() == num)
            .findFirst()
            .orElseThrow(() -> new WrongParam("Некорректное значение: "));
    }
}