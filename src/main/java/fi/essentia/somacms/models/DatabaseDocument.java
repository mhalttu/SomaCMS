package fi.essentia.somacms.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;

/**
 * Concrete representation of the document metadata.
 */
public class DatabaseDocument implements Document {
    @Getter @Setter private long id;
    @Getter @Setter private String name;
    @Getter @Setter private long size;
    @Getter @Setter private Long parentId;
    @Getter @Setter private String mimeType;
    @Getter @Setter private boolean folder;
    @Getter @Setter private Date created;
    @Getter @Setter private Date modified;

    public DatabaseDocument() {
    }

    public DatabaseDocument(long id, String name, boolean folder) {
        this.id = id;
        this.name = name;
        this.folder = folder;
    }

    public boolean isImage() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isText() {
        if (isFolder()) {
            return false;
        }

        String mimeType = getMimeType();
        return mimeType.startsWith("text/") ||
                mimeType.equals("application/xml") ||
                mimeType.equals("application/xhtml+xml") ||
                mimeType.equals("application/javascript");
    }

    public boolean isViewable() {
        return isFolder() || isImage() || isText();
    }
}
