package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.Document;

/**
 *
 */
public interface DataDao {
    void insertData(long documentId, byte[] data);
    void updateData(long documentId, byte[] data);
    byte[] loadData(long documentId);
}
