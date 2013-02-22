package fi.essentia.simplecms.tree;

import fi.essentia.simplecms.dao.DocumentDao;
import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        return idToDocument.get(id);
    }

    public void createChildFolder(Long parentId, String name) {
        if (parentId == ROOT_ID) {
            parentId = null;
        }

        DatabaseDocument databaseDocument = new DatabaseDocument();
        databaseDocument.setName(name);
        databaseDocument.setFolder(true);
        databaseDocument.setParentId(parentId);
        documentDao.save(databaseDocument);

        TreeDocument treeDocument = new TreeDocument(databaseDocument);
        TreeDocument parent = parentFromId(parentId);
        parent.addChild(treeDocument);
        treeDocument.setParent(parent);
        idToDocument.put(treeDocument.getId(), treeDocument);
    }

    private TreeDocument parentFromId(Long parentId) {
        if (parentId == null) {
            return root;
        } else {
            return idToDocument.get(parentId);
        }
    }
}
