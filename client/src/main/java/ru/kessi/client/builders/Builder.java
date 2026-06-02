package ru.kessi.client.builders;

import java.util.function.Function;

import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;

public class Builder {
    protected final Function<String, String[]> DEFAULT_GETPAR = (s) -> Input.getParams(s).split(" ");

    public <T> T interactInputRetry(Function<String[], T> action, Function<String, String[]> getParam, String[] params, String s) throws WrongParam, WrongAction {
        while(true) {
            try {
                return action.apply(params);
            } catch (WrongParam e) {
                System.out.println(e.getMessage());
                if(InputFile.readFile == false)  {
                    String prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n" + 
                                        "\tЕсли хотите начать создание всей лабораторной работы сначала введите: \"(n)no\" \n" +
                                        "\tЕсли хотите выйти из создания лабораторной введите \"(b)back\"");
                    while (prov == null) {
                        prov = Input.getParams("\tВведите: \"(y)yes\", \"(n)no\" или \"(b)back\"");
                    }
                    if (prov.equals("yes") || prov.equals("y")) {
                        params = getParam.apply(s);
                        continue; // повторить попытку
                    } else if (prov.equals("no") || prov.equals("n")) {
                        throw e; // начать заново (поднимаем исключение)
                    } else {
                        throw new WrongAction(); // выйти
                    }
                } else {
                    System.out.println("\tВведены не все параметры или они некорректно заданы, хотите ввести их ещё раз в интерактивном режиме? \n" );
                    String prov = null;
                    while (prov == null) {
                        prov = Input.getParams("\tВведите: \"(y)yes\" или \"(n)no\" \n");
                    }
                    if (prov.equals("yes") || prov.equals("y")) {
                        params = getParam.apply(s);
                        continue; // повторить попытку
                    } else {
                        throw new WrongAction(); // выйти
                    }
                }
            }
        }

    }
}