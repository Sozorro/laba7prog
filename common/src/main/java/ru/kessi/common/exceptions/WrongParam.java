package ru.kessi.common.exceptions;

public class WrongParam extends RuntimeException {
    private String message = "";
    public WrongParam() {
        super();
    }
    public WrongParam(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage(){
        if (message == "")
        return "Incorrect value";
        return message;
    }
}
