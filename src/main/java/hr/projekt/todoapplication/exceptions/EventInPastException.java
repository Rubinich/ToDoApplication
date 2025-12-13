package hr.projekt.todoapplication.exceptions;

public class EventInPastException extends RuntimeException{
    public EventInPastException() {
    }

    public EventInPastException(Throwable cause) {
        super(cause);
    }

    public EventInPastException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventInPastException(String message) {
        super(message);
    }
}
