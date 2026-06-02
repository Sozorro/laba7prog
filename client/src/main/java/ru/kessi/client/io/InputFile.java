package ru.kessi.client.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.client.manegers.ComParser;

public class InputFile {
    private static ComParser comParser;
    public static Scanner scannerNow;
    public static boolean readFile = false;
    private static HashSet<File> openFile = new HashSet<>();
    private static String str = "";
    private static String[] col;
    private static String[] command;

    public static void start(File myFile) throws FileNotFoundException {
        readFile = true;
        try (InputStreamReader file = new InputStreamReader(new FileInputStream(myFile))) {
            if(openFile.contains(myFile)) throw new WrongParam("рекурсивный вызов недоступен");
            else openFile.add(myFile);
            comParser = new ComParser();
            int c = file.read();
            while (c != '\n' && c != -1) {
                str += (char) c;
                c = file.read();
            }
            col = str.split(";");
            str = "";
            while (c != -1) {
                if ((c == '\n' || c == '\r') && !str.isEmpty()) {
                    command = str.split(";"); 
                    comParser.interpret(command[0], getParams(command[0], command));
                    str = "";
                }
                else if (c != '\n' && c != '\r') {
                    str += (char) c;
                }
                c = file.read();

            }

            // Обработка последней строки
            if (!str.isEmpty()) {
                command = str.split(";"); 
                comParser.interpret(command[0], getParams(command[0], command));
            }
        } catch (IOException e) {      
            throw new WrongParam("Произошла ошибка ввода, попробуйтё проверить корректность файла"); 
        }  finally {
            openFile.remove(myFile);
            str = "";
            col = new String[0];
            command = new String[0];
            readFile = false;
        }
    }


    public static String[] getParams(String name_command, String... args){
        if(name_command.equals("add")) {
            return Arrays.copyOfRange(args, 2,  15);
        } else if (name_command.equals("update")) {
            return Arrays.copyOfRange(args, 1,  15);
        }  else if (name_command.equals("remove")) {
            return Arrays.copyOfRange(args, 1,  2);
        } else if (name_command.equals("counterByWeight")) {
            return Arrays.copyOfRange(args, 14,  16);
        } else if (name_command.equals("filterStartsWithDescription")) {
            return Arrays.copyOfRange(args, 15,  17);
        }  else { // if (name_command.equals("clear") || name_command.equals("exit") || name_command.equals("help") || name_command.equals("show") || name_command.equals("save")) {
            return null;
        }  
    }

    // id, LabWork(date, name, coordinatesX, coordinatesY, minimalPoint, personalQualitiesMinimum, description, difficulty, Person(name, height, weight, passportID, hairColor))
}
