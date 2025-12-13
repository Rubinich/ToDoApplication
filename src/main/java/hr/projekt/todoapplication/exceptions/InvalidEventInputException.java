package hr.projekt.todoapplication.exceptions;

public class InvalidEventInputException extends Exception {
    public InvalidEventInputException(String message) {
        super(message);
    }

    public InvalidEventInputException() {
    }

    public InvalidEventInputException(Throwable cause) {
        super(cause);
    }

    public InvalidEventInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
