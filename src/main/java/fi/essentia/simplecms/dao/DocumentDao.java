package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;

import java.util.List;

public interface DocumentDao {
    enum Data {
        INCLUDE,
        EXCLUDE
    }

    public DatabaseDocument findById(long id, Data data);
    public long save(DatabaseDocument document);
    List<DatabaseDocument> findByParentId(Long parentId);

    List<DatabaseDocument> findAll();
    void loadData(Document document);
}
