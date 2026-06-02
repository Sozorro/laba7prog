package ru.kessi.client.commandClient;

import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.exceptions.WrongParam;

public class FilterStartsWithDescriptionCommand extends ru.kessi.common.commandManager.command.FilterStartsWithDescriptionCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        try {
            String str;
            if((args == null || args.length == 0) && InputFile.readFile == false) str = Input.getParams("Введите подстроку: ");
            else if (args == null || args.length == 0) throw new WrongParam("\tВведены не все параметры или они некорректно заданы, хотите ввести их ещё раз в интерактивном режиме? \n");
            else str = String.join(" ", args);
            ComHistory.addCom(name, str);
            return str;
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат id");
            throw e;
        } catch (WrongParam e) {
            System.out.println(e.getMessage());
            String prov = null;
            while (prov == null) {
                prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n \tИначе введите: \"(n)no\" \n \t");
            }
            if(prov.equals("yes") || prov.equals("y")) {
                return execute(Input.getParams("Введите подстроку: "));
            } else {
                System.out.println("Комнда была пропущена");
            }
            throw e;
        }
    }
}
