package ru.kessi.client.manegers;

import java.util.HashMap;

import ru.kessi.client.commandClient.AddCommand;
import ru.kessi.client.commandClient.ClearCommand;
import ru.kessi.client.commandClient.ClientCommand;
import ru.kessi.client.commandClient.CountGreaterThanAuthorCommand;
import ru.kessi.client.commandClient.ExecuteScriptCommand;
import ru.kessi.client.commandClient.ExitCommand;
import ru.kessi.client.commandClient.FilterStartsWithDescriptionCommand;
import ru.kessi.client.commandClient.HelpCommand;
import ru.kessi.client.commandClient.HistoryCommand;
import ru.kessi.client.commandClient.InfoCommand;
import ru.kessi.client.commandClient.RemoveByIdCommand;
import ru.kessi.client.commandClient.ShowCommand;
import ru.kessi.client.commandClient.StopCommand;
import ru.kessi.common.Request;
import ru.kessi.common.commandManager.AbstractCommand;
import ru.kessi.common.entites.LabWork;
import ru.kessi.common.exceptions.WrongParam;


public class ComParser {
    private HashMap<String, ClientCommand> commands = new HashMap<>();
    private HashMap<String, AbstractCommand> commandsRequests = new HashMap<>();
    public ComParser() {

        commands.put("add", new AddCommand());
        commands.put("clear", new ClearCommand());
        commands.put("counterByWeight", new CountGreaterThanAuthorCommand());
        commands.put("execute", new ExecuteScriptCommand());
        commands.put("exit", new ExitCommand());
        commands.put("filterStartsWithDescription", new FilterStartsWithDescriptionCommand());
        commands.put("help", new HelpCommand());
        commands.put("history", new HistoryCommand());
        commands.put("info", new InfoCommand());
        commands.put("remove", new RemoveByIdCommand());
        commands.put("show", new ShowCommand());
        commands.put("stop", new StopCommand());
        //commands.put("update", new UpdateCom());

        commandsRequests.put("add", new ru.kessi.common.commandManager.command.AddCommand());
        commandsRequests.put("clear", new ru.kessi.common.commandManager.command.ClearCommand());
        commandsRequests.put("counterByWeight", new ru.kessi.common.commandManager.command.CountGreaterThanAuthorCommand());
        commandsRequests.put("execute", new ru.kessi.common.commandManager.command.ExecuteScriptCommand());
        commandsRequests.put("exit", new ru.kessi.common.commandManager.command.ExitCommand());
        commandsRequests.put("filterStartsWithDescription", new ru.kessi.common.commandManager.command.FilterStartsWithDescriptionCommand());
        commandsRequests.put("help", new ru.kessi.common.commandManager.command.HelpCommand());
        commandsRequests.put("history", new ru.kessi.common.commandManager.command.HistoryCommand());
        commandsRequests.put("info", new ru.kessi.common.commandManager.command.InfoCommand());
        commandsRequests.put("remove", new ru.kessi.common.commandManager.command.RemoveByIdCommand());
        commandsRequests.put("show", new ru.kessi.common.commandManager.command.ShowCommand());
        commandsRequests.put("stop", new ru.kessi.common.commandManager.command.StopCommand());

    }

    public HashMap<String, ClientCommand> getCommands() {
        return commands;
    }

    public Request interpret(String name, String... args) {
        //System.out.println(name);
        ClientCommand command = this.commands.get(name);
        Object res = command.execute(args);
        if (command instanceof AbstractCommand) {
            if(res == null) return new Request(this.commandsRequests.get(name));
            if(res instanceof LabWork) {
                Request r = new Request(this.commandsRequests.get(name), (LabWork) res);
                return r;
            }
            if(res instanceof String) return new Request(this.commandsRequests.get(name), (String) res);
            else throw new WrongParam();
        } else throw new WrongParam();
        
            
    }
    
}
