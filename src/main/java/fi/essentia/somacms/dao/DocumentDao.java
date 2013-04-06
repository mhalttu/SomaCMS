package fi.essentia.somacms.dao;

import fi.essentia.somacms.models.DatabaseDocument;
import fi.essentia.somacms.models.Document;
import fi.essentia.somacms.tree.TreeDocument;

import java.util.List;

public interface DocumentDao {
    public DatabaseDocument findById(long id);
    public long save(DatabaseDocument document);
    void update(Document document);
    List<DatabaseDocument> findByParentId(Long parentId);
    List<DatabaseDocument> findAll();

    void deleteById(Long documentId);


}
