package fi.essentia.somacms.json;

/**
 * One document matching the search query
 */
public class SearchResult {
    private long id;
    private String path;

    public SearchResult(long id, String path) {
        this.path = path;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }
}
