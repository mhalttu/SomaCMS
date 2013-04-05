package fi.essentia.simplecms.json;

/**
 */
public class Result {
    public boolean success;

    Result(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public static Result success() {
        return new Result(true);
    }
}
