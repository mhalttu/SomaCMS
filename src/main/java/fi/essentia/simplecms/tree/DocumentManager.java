package fi.essentia.simplecms.tree;

import fi.essentia.simplecms.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 */
public interface DocumentManager {
    Document documentFromPath(String path);
    TreeDocument documentById(Long id);
    void createFolder(Long parentId, String name);
    void createDocument(Long parentId, MultipartFile file) throws IOException;
}
