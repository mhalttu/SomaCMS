package fi.essentia.somacms.json;

/**
 * Tells the client that an error occurred
 */
public class Error extends Result {
    private String explanation;

    public Error(String explanation) {
        super(false);
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }
}
