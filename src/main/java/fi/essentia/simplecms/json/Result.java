package fi.essentia.simplecms.json;

/**
 */
public class Result {
    public boolean success;

    public Result(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
