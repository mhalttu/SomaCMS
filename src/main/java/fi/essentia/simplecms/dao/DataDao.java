package fi.essentia.somacms.dao;

import fi.essentia.somacms.models.Document;

/**
 *
 */
public interface DataDao {
    void insertData(long documentId, byte[] data);
    void updateData(long documentId, byte[] data);
    byte[] loadData(long documentId);
}
