package ru.kessi.server.commandServer;


import java.util.TreeSet;

import ru.kessi.common.entites.LabWork;
import ru.kessi.server.managers.CollectionManager;



public class ShowCommand extends ru.kessi.common.commandManager.command.ShowCommand implements ServerCommand {
    @Override
    public String execute(CollectionManager collectionManager, Object args) {
        TreeSet<LabWork> labWorks = collectionManager.getElems();
        String s = "";
        for(LabWork laba : labWorks) {
            s += (laba.toString() + "\n");
        }
        return s;
    }
    
}