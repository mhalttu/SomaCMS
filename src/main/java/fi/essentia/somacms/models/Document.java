package fi.essentia.somacms.models;


import java.util.Date;

/**
 * Represents all the metadata of a document, i.e. everything else but the actual data.
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
