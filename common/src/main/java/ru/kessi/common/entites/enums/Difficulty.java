package ru.kessi.common.entites.enums;

import java.io.Serializable;
import java.util.Arrays;

import ru.kessi.common.exceptions.WrongParam;

public enum Difficulty implements Serializable {
    HARD(1),
    VERY_HARD(2),
    INSANE(3);

    private final int num;

    Difficulty(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    public static Difficulty getVal(int num) throws WrongParam{
        return Arrays.stream(values())
            .filter(dif -> dif.getNum() == num)
            .findFirst()
            .orElseThrow(() -> new WrongParam("Некорректное значение: "));
    }
}
