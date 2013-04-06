package fi.essentia.somacms.models;


import java.util.Date;

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
