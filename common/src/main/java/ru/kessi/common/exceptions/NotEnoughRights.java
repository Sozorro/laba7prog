package ru.kessi.common.exceptions;

public class NotEnoughRights extends RuntimeException {
    private String message;
    public NotEnoughRights() {
        super();
    }
    public NotEnoughRights(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage(){
        if (message == "")
        return "Недостаточно прав для совершения действия";
        return message;
    }
    
}
