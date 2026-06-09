package ru.kessi.client.commandClient;

import org.tinylog.Logger;

import ru.kessi.client.builders.LabWorkBuilder;
import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;

public class UpdateCommand extends ru.kessi.common.commandManager.command.UpdateCommand implements ClientCommand {
    @Override
    public LabWork execute(String... args) {
        try {

            LabWorkBuilder labWorkBuilder = new LabWorkBuilder();
            LabWork laba = null;
            if (args.length == 14) laba = labWorkBuilder.makeLabWork(args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
            else if (InputFile.readFile == false) laba = labWorkBuilder.makeLabWork();
            else {
                Logger.info("\tВведены не все параметры или они некорректно заданы, хотите ввести их ещё раз в интерактивном режиме? \n" );
                String prov = null;
                while (prov == null) {
                    prov = Input.getParams("\tВведите: \"(y)yes\" или \"(n)no\" \n");
                }
                if (prov.equals("yes") || prov.equals("y")) {
                    laba = labWorkBuilder.makeLabWork();
                } else {
                    Logger.info("Комнда была пропущена");
                    throw new WrongAction();
                }
            }
            if(laba == null) throw new WrongAction();

            String[] str;
            if(args == null || args.length == 0 || (InputFile.readFile == false && args.length != 0)) {
                str = Input.getParams("Какой элемент обновить?").split(" ");
                if (str.length != 1) throw new WrongParam("Неверный формат ввода");
            }
            else str = args;

            laba.setId(Long.valueOf(str[0]));
            
            ComHistory.addCom(name, laba.toString());
            return laba;
        } catch (NumberFormatException e) {
            Logger.info("Неверный формат id");
            throw new WrongParam("Неверный формат id");
        } catch (WrongParam e) {
            Logger.info("Из-за ошибки ввода элемент не был обновлён");
            throw e;
        } catch (WrongAction e) {
            Logger.info("Создание было остановлено и элемент не был обновлён");
            throw e;
        }
    }
}
