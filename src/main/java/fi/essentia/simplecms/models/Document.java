package fi.essentia.simplecms.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Date;

/**
 *
 */
public interface Document {
    long getId();
    String getName();
    long getSize();
    Long getParentId();
    String getMimeType();
    boolean isFolder();
    Date getCreated();
    Date getModified();
}
