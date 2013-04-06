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
    TreeDocument createFolder(Long parentId, String name);
    TreeDocument createTextFile(Long parentId, String name);
    TreeDocument storeDocument(Long parentId, String fileName, byte[] bytes);
    TreeDocument deleteDocument(Long documentId);
    Collection<TreeDocument> documentsByPath(String path);
    void documentUpdated(TreeDocument document);
}
