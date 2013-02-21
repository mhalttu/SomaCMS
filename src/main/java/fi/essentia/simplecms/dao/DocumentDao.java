package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.Document;

import java.util.List;

public interface DocumentDao {
    public Document findById(long id, boolean includeData);
    public long save(Document document);
    List<Document> findByParentId(Long parentId);
}
