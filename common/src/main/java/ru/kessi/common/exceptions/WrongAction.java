package ru.kessi.common.exceptions;

public class WrongAction extends RuntimeException {
    private String message;
    public WrongAction() {
        super();
    }
    public WrongAction(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage(){
        if (message == "")
        return "Откакт действия";
        return message;
    }
}
