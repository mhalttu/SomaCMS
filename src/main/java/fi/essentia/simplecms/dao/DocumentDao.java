package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;
import fi.essentia.simplecms.tree.TreeDocument;

import java.util.List;

public interface DocumentDao {
    public DatabaseDocument findById(long id);
    public long save(DatabaseDocument document);
    void update(Document document);
    List<DatabaseDocument> findByParentId(Long parentId);
    List<DatabaseDocument> findAll();

    void deleteById(Long documentId);


}
