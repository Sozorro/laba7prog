package ru.kessi.server.builders;

import java.util.function.Function;

import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;

public class Builder {

    public <T> T interactInputRetry(Function<String[], T> action, String[] params, String s) throws WrongParam, WrongAction {
        while(true) {
            try {
                return action.apply(params);
            } catch (WrongParam e) {
                System.out.println(e.getMessage());
                System.out.println("\tВведены не все параметры или они некорректно заданы \n" );
            }
        }

    }
}