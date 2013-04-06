package fi.essentia.somacms.json;

/**
 * Tells the client that a document has been created
 */
public class Created extends Result {
    long documentId;

    public Created(long documentId) {
        super(true);
        this.documentId = documentId;
    }

    public long getDocumentId() {
        return documentId;
    }
}
