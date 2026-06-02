package ru.kessi.client.io;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.tinylog.Logger;

import ru.kessi.client.manegers.ComParser;
import ru.kessi.common.Request;
import ru.kessi.common.exceptions.WrongParam;

public class Input {
    private static ComParser comParser;
    public static Scanner scannerNow;
    
    public static Request start(Scanner scanner) {
        comParser = new ComParser();
        scannerNow = scanner;
        while (true) {
            try {
                System.out.println("----------------------");
                System.out.println("Ведите команду. Если хотите выйти введите: 'exit'. Для справки введите: help");
                String s = readNextLine();
                if (s == null) continue;
                String[] command = s.split(" ", 2);
                if(!comParser.getCommands().containsKey(command[0])) {
                    System.out.println("Неверная команда");
                    continue;
                }
                if(command.length == 1) {
                    return comParser.interpret(command[0]);
                }
                else {
                    return comParser.interpret(command[0], command[1]);
                }
            } catch (NoSuchElementException e) {
                // 👈 EOF — корректно сигнализируем об окончании ввода
                Logger.info("Ввод завершён (EOF). Возврат управления.");
                return null;
                
            } catch (WrongParam e) {
                System.out.println(e.getMessage());
                // Продолжаем цикл ввода
                
            } catch (Exception e) {
                Logger.error(e, "Неожиданная ошибка при вводе");
                System.out.println("Произошла ошибка. Попробуйте ещё раз.");
            }
        }
    }
    private static String readNextLine() throws WrongParam { 
        String s = scannerNow.nextLine().trim();
        while (s.equals("") || s == null) {
            System.out.println("Ожидание строки");
            s = scannerNow.nextLine().trim();
        }
        return s;
    }

    public static String getParams(String s, String... args) throws WrongParam { 
        System.out.println(s);
        for(String arg : args) {
            System.out.println(arg);
        }
        String str = readNextLine().strip();
        return str;
    }
}