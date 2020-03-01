package clime.exceptions;

public class CliMeUsageException extends Exception {

    public CliMeUsageException(String message) {
        super(message);
    }

    public CliMeUsageException(String message, Throwable cause) {
        super(message, cause);
    }
}
