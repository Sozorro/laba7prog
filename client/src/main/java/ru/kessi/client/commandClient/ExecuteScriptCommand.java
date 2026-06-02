package ru.kessi.client.commandClient;

import java.io.File;
import java.io.FileNotFoundException;

import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;

public class ExecuteScriptCommand extends ru.kessi.common.commandManager.command.ExecuteScriptCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        try {
            String str = ""; ///Users/kessi.kissa/Desktop/jlab/lab5/file1.csv
            if(args == null || args.length == 0) str = Input.getParams("Какой файл вы хотите запустить?");
            else if(args.length != 1) throw new WrongParam("Неверное имя файла, проверьте корректность ввода и отсутствие пробелов в названии");
            else str = args[0];
            File myFile = new File(str);
            InputFile.start(myFile);
            ComHistory.addCom(name, str);
            return null;
        } catch (FileNotFoundException e) {
            System.out.println("Данный файл не найден");
            String prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n \tИначе введите: \"(n)no\" \n \t");
            if(prov != null && (prov.equals("yes") || prov.equals("y"))) {
                return execute();
            }
            throw new WrongParam("Недействительный файл");
        } catch (WrongParam e) {
            System.out.println(e.getMessage());
            String prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n \tИначе введите: \"(n)no\" \n \t");
            if(prov != null && (prov.equals("yes") || prov.equals("y"))) {
                return execute();
            }
            throw e;
        }
    }
    @Override
    public String toString() {
        return "command 'execute'";
    }
}
