package fi.essentia.simplecms.tree;

import fi.essentia.simplecms.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface DocumentManager {
    TreeDocument documentFromPath(String path);
    TreeDocument documentById(Long id);
    void createFolder(Long parentId, String name);
    long createTextFile(Long parentId, String name);
    void storeDocument(Long parentId, String fileName, byte[] bytes1);
    TreeDocument deleteDocument(Long documentId);
    Collection<TreeDocument> documentsByPath(String path);
}
