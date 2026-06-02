package ru.kessi.client.commandClient;

import ru.kessi.client.io.Input;
import ru.kessi.client.io.InputFile;
import ru.kessi.client.manegers.ComHistory;
import ru.kessi.common.exceptions.WrongAction;
import ru.kessi.common.exceptions.WrongParam;

public class CountGreaterThanAuthorCommand extends ru.kessi.common.commandManager.command.CountGreaterThanAuthorCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        try {
            String[] str;
            if(args == null || args.length == 0) {
                if(InputFile.readFile == false) str = Input.getParams("введите вес").split(" ");
                else {
                    System.out.println("Не введены параметры, необходимые для выполнения команды, хотите ввести их в интерактивном режиме?");
                    String prov = Input.getParams("\tВведите: \"(y)yes\" \n \tИли: \"no\" и тогда команда будет пропущена");
                    while(prov == null) {
                        prov = Input.getParams("\tВведите: \"(y)yes\" \n \tИли: \"no\" и тогда команда будет пропущена");
                    }
                    if(prov.equals("yes") || prov.equals("y")) {
                        str = Input.getParams("введите вес").split(" ");
                    }
                    System.out.println("Комнда была пропущена");
                    throw new WrongAction();
                }
            }
            else str = args;
            String ext = "Неверный формат, проверьте корректность ввода";
            if(str.length > 1 || str.length == 0) {
                throw new WrongParam(ext);
            }
            //PersonBuilder personBuilder = new PersonBuilder();
            //Person author = personBuilder.makePerson("", 0.0, Long.valueOf(str[0]), "", Color.WHITE);
            ComHistory.addCom(name, str[0]);
            return str[0];
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат веса");
            throw e;
        } catch (WrongParam e) {
            System.out.println(e.getMessage());
            String prov = Input.getParams("\tЕсли хотите попробовать ещё раз введите: \"(y)yes\" \n \tИначе введите: \"(n)no\" \n \t");
            if(prov != null && (prov.equals("yes") || prov.equals("y"))) {
                return execute();
            }
            throw e;
        }
    }
}
