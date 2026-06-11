package ru.kessi.server.managers;

import java.util.HashMap;

import ru.kessi.common.commandManager.CommandMetadata;
import ru.kessi.common.exceptions.WrongParam;
import ru.kessi.server.commandServer.AddCommand;
import ru.kessi.server.commandServer.ClearCommand;
import ru.kessi.server.commandServer.CountGreaterThanAuthorCommand;
import ru.kessi.server.commandServer.FilterStartsWithDescriptionCommand;
import ru.kessi.server.commandServer.InfoCommand;
import ru.kessi.server.commandServer.RemoveByIdCommand;
import ru.kessi.server.commandServer.ServerCommand;
import ru.kessi.server.commandServer.ShowCommand;
import ru.kessi.server.commandServer.UpdateCommand;

public class ComParser {
    private HashMap<String, ServerCommand> commands = new HashMap<>();
    CollectionManager collectionManager;
    public ComParser(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        commands.put("add", new AddCommand());
        commands.put("clear", new ClearCommand());
        commands.put("counterByWeight", new CountGreaterThanAuthorCommand());
        //commands.put("exit", new ExitCom());
        commands.put("filterStartsWithDescription", new FilterStartsWithDescriptionCommand());
        commands.put("info", new InfoCommand());
        commands.put("remove", new RemoveByIdCommand());
        commands.put("show", new ShowCommand());
        //commands.put("stop", new StopCom());
        commands.put("update", new UpdateCommand());
    }

    public HashMap<String, ServerCommand> getCommands() {
        return commands;
    }

    public String interpret(String login, CommandMetadata com, Object args) {
        try {
            //Logger.info(com.getName());
            //Logger.info(this.commands.get(com.getName()));
            ServerCommand command = this.commands.get(com.getName());
            return command.execute(login, collectionManager, args);
        } catch (WrongParam e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Произошла ошибка при выполнении команды, попробуйте позже";
        }
        
    }
    
}
