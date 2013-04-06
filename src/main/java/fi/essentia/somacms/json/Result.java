package fi.essentia.somacms.json;

/**
 * Generic result sent to the client. Can either be a success or failure.
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
