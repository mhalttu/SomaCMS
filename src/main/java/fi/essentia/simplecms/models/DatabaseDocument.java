package fi.essentia.simplecms.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.ArrayList;
import java.sql.Date;

/**
 *
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
    @Getter @Setter private Blob data;
}
