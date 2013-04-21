package fi.essentia.somacms.dao;

/**
 * Interface that exposes only read operations to the DataDao so that we don't even accidentally use it to bypass
 * DocumentManager.
 */
public interface ReadOnlyDataDao {
    byte[] loadData(long documentId);
}
