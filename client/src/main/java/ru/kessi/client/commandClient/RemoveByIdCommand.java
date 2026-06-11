package ru.kessi.client.commandClient;

import org.tinylog.Logger;

import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;

public class RemoveByIdCommand extends ru.kessi.common.commandManager.command.RemoveByIdCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        try {
            String[] str;
            if (args.length == 1) str = args;
            else if (InputFile.readFile == false) str = Input.getParams("Какой элемент удалить?").split(" ");
            else {
                throw new WrongParam("\tВведены не все параметры или они некорректно заданы, хотите ввести их ещё раз в интерактивном режиме? \n");
            }
            ComHistory.addCom(name, str[0]);
            return str[0];
        } catch (NumberFormatException e) {
            Logger.info("Неверный формат id");
            throw new WrongParam("Неверный формат id");
        } catch (WrongParam e) {
            Logger.error(e);
            String prov = null;
            while (prov == null) {
                prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n \tИначе введите: \"(n)no\" \n \t");
            }
            if(prov.equals("yes") || prov.equals("y")) {
                return execute(Input.getParams("Какой элемент удалить?").split(" "));
            } else {
                Logger.info("Комнда была пропущена");
            }
            throw e;
        } catch (WrongAction e) {
            Logger.info("Комнда была остановлена");
            throw e;
        }
    }
}
