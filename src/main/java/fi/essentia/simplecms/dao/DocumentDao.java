package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;

import java.util.List;

public interface DocumentDao {
    public DatabaseDocument findById(long id);
    public long save(DatabaseDocument document);
    List<DatabaseDocument> findByParentId(Long parentId);
    List<DatabaseDocument> findAll();
}
