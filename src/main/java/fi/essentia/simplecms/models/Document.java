package fi.essentia.simplecms.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.Date;

/**
 *
 */
@Getter @Setter
public class Document {
    private long id;
    private String name;
    private long size;
    private Long parentId;
    private String mimeType;
    private boolean folder;
    private Date created;
    private Date modified;
    private Blob data;
}
