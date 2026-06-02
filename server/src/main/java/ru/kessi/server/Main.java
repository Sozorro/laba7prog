package ru.kessi.server;


import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.tinylog.Logger;

import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.network.Server;

public class Main {
    public static Scanner scannerNow;
    public static void main(String[] args) {
        Logger.info("Запуск програмы создания сервера");

        /*Logger.info("Приложение запущено, env={}", System.getProperty("env"));

        Logger.trace("Трассировка");
        Logger.debug("Отладка");
        Logger.info("Запуск приложения");
        Logger.warn("Внимание: {} параметр");
        Logger.error("Критическая ошибка");*/

        scannerNow = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("----------------------");
                System.out.println("Для запуска сервера введите 'start':");
                String s = readNextLine();
                if (s == null) continue;
                if (s.equals("start")) {
                    Server server = new Server("localhost", 1234);
                    server.start();
                    break;
                    //System.exit(0);
                }
            } catch (NoSuchElementException e) { //if end file(ctrl+D)
                scannerNow = new Scanner(System.in);
            } catch (WrongParam e) { //if end file(ctrl+D)
                System.out.println(e.getMessage());
                scannerNow = new Scanner(System.in);
            }
        }
        //CollectionManager collectionManager = new CollectionManager();
        //Input.start(collectionManager);
    }

    private static String readNextLine() throws WrongParam { 
        try {
            String s = scannerNow.nextLine().trim();
            while (s.equals("") || s == null) {
                System.out.println("ожидание строки");
                s = scannerNow.nextLine().trim();
            }
            return s;
        } catch (NoSuchElementException e) { //if end file(ctrl+D)
            throw new WrongParam("Недопустимые символы для ввода");
        }
        
    }
        ArrayList<String> arr = new ArrayList<>();

}
