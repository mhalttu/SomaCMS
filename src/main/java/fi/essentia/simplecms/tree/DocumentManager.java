package fi.essentia.simplecms.tree;

import fi.essentia.simplecms.controllers.UnauthorizedException;
import fi.essentia.simplecms.dao.DocumentDao;
import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Component
public class DocumentManager {
    public static final long ROOT_ID = 0;
    private TreeDocument root;
    private Map<Long, TreeDocument> idToDocument = new HashMap<Long, TreeDocument>();

    @Autowired DocumentDao documentDao;

    @PostConstruct
    public void initialize() {
        initializeRoot();
        loadChildren();
        linkDocuments();
    }

    private void linkDocuments() {
        for (TreeDocument document : idToDocument.values()) {
            if (document == root) {
                continue;
            }

            TreeDocument parent = parentFromId(document.getParentId());
            document.setParent(parent);
            parent.addChild(document);
        }
    }

    private void loadChildren() {
        List<DatabaseDocument> databaseDocuments = documentDao.findAll();
        for (DatabaseDocument databaseDocument : databaseDocuments) {
            TreeDocument treeDocument = new TreeDocument(databaseDocument);
            idToDocument.put(treeDocument.getId(), treeDocument);
        }
    }

    private void initializeRoot() {
        DatabaseDocument databaseDocument = new DatabaseDocument();
        databaseDocument.setFolder(true);
        databaseDocument.setId(ROOT_ID);
        databaseDocument.setName("");
        root = new TreeDocument(databaseDocument);
        idToDocument.put(ROOT_ID, root);
    }

    public Document documentFromPath(String path) {
        String[] split = path.split("/");
        TreeDocument document = root;
        for (String name : split) {
            document = document.childByName(name);
            if (document == null) {
                return null;
            }
        }
        return document;
    }

    public TreeDocument documentById(Long id) {
        if (id == null) {
            return root;
        }
        return idToDocument.get(id);
    }

    public void createChildFolder(Long parentId, String name) {
        TreeDocument parent = folder(parentId);

        DatabaseDocument databaseDocument = new DatabaseDocument();
        databaseDocument.setName(name);
        databaseDocument.setFolder(true);
        databaseDocument.setParentId(parent.isRoot() ? null : parent.getId());
        documentDao.save(databaseDocument);

        addToTree(databaseDocument, parentId);
    }

    private TreeDocument folder(Long folderId) {
        TreeDocument folder = documentById(folderId);
        if (!folder.isFolder()) {
            throw new UnauthorizedException();
        }
        return folder;
    }

    private TreeDocument parentFromId(Long parentId) {
        if (parentId == null) {
            return root;
        } else {
            return idToDocument.get(parentId);
        }
    }

    public void saveFile(Long parentId, MultipartFile file) throws IOException {
        TreeDocument parent = folder(parentId);

        DatabaseDocument databaseDocument = new DatabaseDocument();
        databaseDocument.setName(file.getOriginalFilename());
        databaseDocument.setParentId(parent.isRoot() ? null : parent.getId());
        byte[] bytes = file.getBytes();
        databaseDocument.setSize(bytes.length);
        databaseDocument.setData(bytes);
        databaseDocument.setMimeType(file.getContentType());
        documentDao.save(databaseDocument);

        addToTree(databaseDocument, parentId);
    }

    private void addToTree(DatabaseDocument databaseDocument, Long parentId) {
        TreeDocument treeDocument = new TreeDocument(databaseDocument);
        TreeDocument parent = parentFromId(parentId);
        parent.addChild(treeDocument);
        treeDocument.setParent(parent);
        idToDocument.put(treeDocument.getId(), treeDocument);
    }

     /*
    private void detectMimeType(File file) {
        try {
            mimeType = TIKA.detect(file);
            if (mimeType.endsWith("jpeg") || mimeType.endsWith("png") || mimeType.endsWith("gif")) {
                image = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    */
}
