package fi.essentia.somacms.tree;

import fi.essentia.somacms.models.Document;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Value;

import java.util.Date;

/**
 * An immutable representation of the Document object
 */
public class ImmutableDocument implements Document {
    public ImmutableDocument(Document document) {
        this.id = document.getId();
        this.name = document.getName();
        this.size = document.getSize();
        this.parentId = document.getParentId();
        this.mimeType = document.getMimeType();
        this.folder = document.isFolder();
        this.created = document.getCreated();
        this.modified = document.getModified();
    }

    @Getter private long id;
    @Getter private String name;
    @Getter private long size;
    @Getter private Long parentId;
    @Getter private String mimeType;
    @Getter private boolean folder;
    @Getter private Date created;
    @Getter private Date modified;
}
