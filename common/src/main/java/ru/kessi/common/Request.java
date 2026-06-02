package ru.kessi.common;

import java.io.Serializable;

import ru.kessi.common.commandManager.AbstractCommand;
import ru.kessi.common.entites.LabWork;

public class Request implements Serializable {
    private LabWork labWork;
    private AbstractCommand command;
    private String args;
    //public Object args;
    public Request(AbstractCommand command) {
        this.command = command;
    }
    public Request(AbstractCommand command, LabWork labWork) {
        this.command = command;
        this.labWork = labWork;
    }
    public Request(AbstractCommand command, String args) {
        this.command = command;
        this.args = args;
    }
    public Request(AbstractCommand command, LabWork labWork, String args) {
        this.command = command;
        this.labWork = labWork;
        this.args = args;
    }
    public AbstractCommand getCommand() {
        return command;
    }
    public LabWork getLabWork() {
        return labWork;
    }
    public String getArgs() {
        return args;
    }
}
