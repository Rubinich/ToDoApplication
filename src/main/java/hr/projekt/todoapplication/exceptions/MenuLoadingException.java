package hr.projekt.todoapplication.exceptions;

public class MenuLoadingException extends RuntimeException {
    public MenuLoadingException(String message) {
        super(message);
    }

  public MenuLoadingException(String message, Throwable cause) {
    super(message, cause);
  }

  public MenuLoadingException(Throwable cause) {
    super(cause);
  }

  public MenuLoadingException() {
  }
}
