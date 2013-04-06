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
    private Map<Long, TreeDocument> idToDocument = new HashMap<Long, TreeDocument>();
    private TreeDocument root;

    @Autowired DocumentDao documentDao;
    @Autowired DataDao dataDao;


    @PostConstruct
    public void initialize() {
        loadDocuments();
        initializeRoot();
        linkDocuments();
    }

    private void initializeRoot() {
        root = idToDocument.get(TreeDocument.ROOT_ID);
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

    private void loadDocuments() {
        List<DatabaseDocument> databaseDocuments = documentDao.findAll();
        for (DatabaseDocument databaseDocument : databaseDocuments) {
            TreeDocument treeDocument = new TreeDocument(databaseDocument);
            idToDocument.put(treeDocument.getId(), treeDocument);
        }
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
        return idToDocument.get(id);
    }

    @Override public TreeDocument createFolder(Long parentId, String name) {
        return createDocument(parentId, name, true);
    }

    private TreeDocument createDocument(Long parentId, String name, boolean folder) throws UnsupportedMimeTypeException {
        TreeDocument parent = folder(parentId);
        DatabaseDocument databaseDocument = new DatabaseDocument();
        databaseDocument.setName(name);
        databaseDocument.setFolder(folder);
        if (!folder) {
            String mimeType = tika.detect(name);
            databaseDocument.setMimeType(mimeType);
            if (!databaseDocument.isText()) {
                throw new UnsupportedMimeTypeException();
            }
        }
        databaseDocument.setParentId(parent.getId());

        documentDao.save(databaseDocument);
        return addToTree(databaseDocument, parentId);
    }

    @Override
    public TreeDocument createTextFile(Long parentId, String name) {
        TreeDocument document = createDocument(parentId, name, false);
        dataDao.insertData(document.getId(), new byte[0]);
        return document;
    }

    private TreeDocument folder(Long folderId) {
        TreeDocument folder = documentById(folderId);
        if (!folder.isFolder()) {
            throw new UnauthorizedException();
        }
        return folder;
    }

    private TreeDocument parentFromId(Long parentId) {
        return idToDocument.get(parentId);
    }

    @Override
    public TreeDocument storeDocument(Long parentId, String fileName, byte[] bytes) {
        String mimeType = tika.detect(bytes, fileName);
        // TODO This method should should be transactional..
        TreeDocument parent = folder(parentId);
        TreeDocument document = parent.childByName(fileName);
        if (document == null) {
            DatabaseDocument databaseDocument = new DatabaseDocument();
            databaseDocument.setName(fileName);
            databaseDocument.setParentId(parent.getId());
            databaseDocument.setSize(bytes.length);
            databaseDocument.setMimeType(mimeType);
            documentDao.save(databaseDocument);
            document = addToTree(databaseDocument, parentId);

            dataDao.insertData(databaseDocument.getId(), bytes);
        } else {
            document.setModified(new Date());
            document.setSize(bytes.length);
            document.setMimeType(mimeType);
            documentDao.update(document);

            dataDao.updateData(document.getId(), bytes);
        }
        return document;
    }

    @Override
    public TreeDocument deleteDocument(Long documentId) {
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
        return document;
    }

    @Override
    public Collection<TreeDocument> documentsByPath(final String path) {
        return Collections2.filter(idToDocument.values(), new Predicate<TreeDocument>() {
            @Override
            public boolean apply(TreeDocument treeDocument) {
                return StringUtils.containsIgnoreCase(treeDocument.getPath(), path) && treeDocument.isViewable() && !treeDocument.isRoot();
            }
        });
    }

    @Override
    public void documentUpdated(TreeDocument document) {
        document.setModified(new Date());

    }

    private TreeDocument addToTree(DatabaseDocument databaseDocument, Long parentId) {
        TreeDocument treeDocument = new TreeDocument(databaseDocument);
        TreeDocument parent = parentFromId(parentId);
        parent.addChild(treeDocument);
        treeDocument.setParent(parent);
        idToDocument.put(treeDocument.getId(), treeDocument);
        return treeDocument;
    }
}
