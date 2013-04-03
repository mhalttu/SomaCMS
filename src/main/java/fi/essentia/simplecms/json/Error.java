package fi.essentia.simplecms.json;

/**
 */
public class Error extends Result {
    private String message;

    public Error(String message) {
        super(false);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
