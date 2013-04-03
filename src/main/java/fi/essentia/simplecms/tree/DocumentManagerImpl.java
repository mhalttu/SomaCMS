package fi.essentia.simplecms.tree;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fi.essentia.simplecms.controllers.UnauthorizedException;
import fi.essentia.simplecms.dao.DataDao;
import fi.essentia.simplecms.dao.DocumentDao;
import fi.essentia.simplecms.models.DatabaseDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 *
 */
@Component
public class DocumentManagerImpl implements DocumentManager {
    private Tika tika = new Tika();
    public static final long ROOT_ID = 0;
    private TreeDocument root;
    private Map<Long, TreeDocument> idToDocument = new HashMap<Long, TreeDocument>();

    @Autowired DocumentDao documentDao;
    @Autowired DataDao dataDao;

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

    @Override public TreeDocument documentFromPath(String path) {
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

    @Override public TreeDocument documentById(Long id) {
        if (id == null) {
            return root;
        }
        return idToDocument.get(id);
    }

    @Override public void createFolder(Long parentId, String name) {
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

    @Override
    public void storeDocument(Long parentId, String fileName, byte[] bytes) {
        String mimeType = tika.detect(bytes, fileName);
        // TODO This method should should be transactional..
        TreeDocument parent = folder(parentId);
        TreeDocument document = parent.childByName(fileName);
        if (document == null) {
            DatabaseDocument databaseDocument = new DatabaseDocument();
            databaseDocument.setName(fileName);
            databaseDocument.setParentId(parent.isRoot() ? null : parent.getId());
            databaseDocument.setSize(bytes.length);
            databaseDocument.setMimeType(mimeType);
            documentDao.save(databaseDocument);
            addToTree(databaseDocument, parentId);

            dataDao.insertData(databaseDocument.getId(), bytes);
        } else {
            document.setModified(new Date());
            document.setSize(bytes.length);
            document.setMimeType(mimeType);
            documentDao.update(document);

            dataDao.updateData(document.getId(), bytes);
        }
    }

    @Override
    public void deleteDocument(Long documentId) {
        TreeDocument document = documentById(documentId);
        if (document.isRoot()) {
            throw new UnauthorizedException();
        }

        List<TreeDocument> children = new ArrayList<TreeDocument>(document.getChildren());
        for (TreeDocument child : children) {
            deleteDocument(child.getId());
        }

        documentDao.deleteById(documentId);
        document.getParent().removeChild(document);
        idToDocument.remove(documentId);
    }

    @Override
    public Collection<TreeDocument> documentsByPath(final String path) {
        return Collections2.filter(idToDocument.values(), new Predicate<TreeDocument>() {
            @Override
            public boolean apply(TreeDocument treeDocument) {
                return StringUtils.containsIgnoreCase(treeDocument.getPath(), path);
            }
        });
    }

    private void addToTree(DatabaseDocument databaseDocument, Long parentId) {
        TreeDocument treeDocument = new TreeDocument(databaseDocument);
        TreeDocument parent = parentFromId(parentId);
        parent.addChild(treeDocument);
        treeDocument.setParent(parent);
        idToDocument.put(treeDocument.getId(), treeDocument);
    }
}
