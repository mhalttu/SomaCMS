package fi.essentia.simplecms.json;

/**
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
