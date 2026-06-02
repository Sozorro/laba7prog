package ru.kessi.client.commandClient;

import ru.kessi.client.manegers.ComHistory;
import ru.kessi.client.manegers.ComParser;
import ru.kessi.common.commandManager.CommandMetadata;
import ru.kessi.common.exceptions.WrongParam;

public class HelpCommand extends ru.kessi.common.commandManager.command.HelpCommand implements ClientCommand {
    @Override
    public String execute(String... args) {
        try{
            if(args != null && args.length != 0) {
                throw new WrongParam("Введены лишние параметры");
            }
            ComParser pars = new ComParser();
            System.out.println("Список доступных команд: ");
            for(ClientCommand com : pars.getCommands().values()) {
                if (com instanceof CommandMetadata) {
                    CommandMetadata metadata = (CommandMetadata) com;
                    System.out.printf("%s: %s %n", metadata.getName(), metadata.getDescription());
                }
            }
            ComHistory.addCom(name, null);
            return null;
        }  catch (WrongParam e) {
            System.out.println("Ошибка ввода.");
            throw e;
        }
    }
    @Override
    public String toString() {
        return "command 'help'";
    }
}
