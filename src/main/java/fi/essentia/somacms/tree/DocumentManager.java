package fi.essentia.somacms.tree;

import fi.essentia.somacms.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Takes care of all the operations related to the document metadata and contents.
 */
public interface DocumentManager {
    TreeDocument documentFromPath(String path);
    TreeDocument documentById(Long id);
    TreeDocument createFolder(Long parentId, String name);
    TreeDocument createTextFile(Long parentId, String name);
    TreeDocument storeDocument(Long parentId, String fileName, byte[] bytes);
    TreeDocument deleteDocument(Long documentId);
    Collection<TreeDocument> documentsByPath(String path);
}
