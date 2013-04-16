package fi.essentia.somacms.tree;

import fi.essentia.somacms.dao.DocumentDao;
import fi.essentia.somacms.models.DatabaseDocument;
import fi.essentia.somacms.models.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentManagerTest {
    private DocumentManagerImpl documentManager;
    private DocumentDao mockDocumentDao;
    private DatabaseDocument root;
    private DatabaseDocument folder;
    private DatabaseDocument child;

    @Before
    public void set() {
        List<DatabaseDocument> documents = initializeDocuments();

        mockDocumentDao = mock(DocumentDao.class);
        documentManager = new DocumentManagerImpl();
        documentManager.documentDao = mockDocumentDao;

        when(mockDocumentDao.findAll()).thenReturn(documents);

        documentManager.initialize();
    }

    private List<DatabaseDocument> initializeDocuments() {
        root = new DatabaseDocument(0, "root", true);
        folder = new DatabaseDocument(1, "folder", true);
        child = new DatabaseDocument(2, "child", false);
        folder.setParentId(root.getId());
        child.setParentId(folder.getId());

        List<DatabaseDocument> documents = new ArrayList<DatabaseDocument>();
        documents.add(root);
        documents.add(folder);
        documents.add(child);
        return documents;
    }

    @Test
    public void documentFromPath() {
        assertEquals(root.getId(), documentManager.documentFromPath("/").getId());
        assertEquals(folder.getId(), documentManager.documentFromPath("/folder/").getId());
        assertEquals(child.getId(), documentManager.documentFromPath("/folder/child").getId());
    }
}
