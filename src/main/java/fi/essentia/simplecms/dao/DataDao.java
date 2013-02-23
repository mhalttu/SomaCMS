package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.Document;

/**
 *
 */
public interface DataDao {
    void saveDataForDocument(long documentId, byte[] data);
    byte[] loadDataForDocument(long documentId);
}
