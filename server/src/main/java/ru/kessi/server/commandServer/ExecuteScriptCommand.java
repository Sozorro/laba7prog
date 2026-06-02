package ru.kessi.server.commandServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.builders.LabWorkBuilder;
import ru.kessi.server.managers.CollectionManager;
import ru.kessi.server.managers.ComParser;

public class ExecuteScriptCommand extends ru.kessi.common.commandManager.command.ExecuteScriptCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args){
        try {
            String str = "dop_doc/collection.csv";

            File myFile = new File(str);
            try (InputStreamReader file = new InputStreamReader(new FileInputStream(myFile))) {
                ComParser comParser = new ComParser(collectionManager);
                String[] col;
                String[] command;
                LabWorkBuilder labWorkBuilder = new LabWorkBuilder();
                AddCommand addCommand = new AddCommand();

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

                        LabWork laba = labWorkBuilder.makeLabWork(command[2], command[3], command[4], command[5], command[6], command[7], command[8], command[9], command[10], command[11], command[12], command[13], command[14]);
                        addCommand.execute(collectionManager, laba);

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

                    LabWork laba = labWorkBuilder.makeLabWork(command[2], command[3], command[4], command[5], command[6], command[7], command[8], command[9], command[10], command[11], command[12], command[13], command[14]);
                    addCommand.execute(collectionManager, laba);
                }
                return "korrect";
            } catch (IOException e) {      
                throw new WrongParam("Произошла ошибка ввода, попробуйтё проверить корректность файла"); 
            }
        } catch (Exception e) {      
            throw new WrongParam("Произошла ошибка"); 
        }
        
    }
}