package ru.kessi.server.managers;

import java.util.HashMap;

import ru.kessi.common.commandManager.CommandMetadata;
import ru.kessi.server.commandServer.AddCommand;
import ru.kessi.server.commandServer.ClearCommand;
import ru.kessi.server.commandServer.CountGreaterThanAuthorCommand;
import ru.kessi.server.commandServer.FilterStartsWithDescriptionCommand;
import ru.kessi.server.commandServer.InfoCommand;
import ru.kessi.server.commandServer.RemoveByIdCommand;
import ru.kessi.server.commandServer.ServerCommand;
import ru.kessi.server.commandServer.ShowCommand;

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
        //commands.put("update", new UpdateCom());
    }

    public HashMap<String, ServerCommand> getCommands() {
        return commands;
    }

    public String interpret(CommandMetadata com, Object args) {
        System.out.println(com.getName());
        System.out.println(this.commands.get(com.getName()));
        ServerCommand command = this.commands.get(com.getName());
        return command.execute(collectionManager, args);
    }
    
}
