package fi.essentia.somacms.tree;

import fi.essentia.somacms.models.DatabaseDocument;
import fi.essentia.somacms.models.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class TreeDocumentTest {
    private TreeDocument treeDocument;
    private TreeDocument root;
    private TreeDocument parent;
    private TreeDocument child;

    @Before
    public void setup() {
        DatabaseDocument databaseDocument = new DatabaseDocument(1, "document", true);
        treeDocument = new TreeDocument(databaseDocument);

        root = new TreeDocument(new DatabaseDocument(TreeDocument.ROOT_ID, "root", true));
        parent = new TreeDocument(new DatabaseDocument(3, "parent", true));
        parent.setParent(root);
        treeDocument.setParent(parent);

        child = new TreeDocument(new DatabaseDocument(2, "child", false));
        treeDocument.addChild(child);
    }

    @Test
    public void root() {
        assertEquals(TreeDocument.ROOT_ID, root.getId());
        assertTrue(root.isRoot());
        assertFalse(treeDocument.isRoot());
    }

    @Test
    public void children() {
        assertEquals(child, treeDocument.childByName(child.getName()));
        assertNull(treeDocument.childByName("otherChild"));

        assertEquals(1, treeDocument.getChildren().size());
        assertEquals(child.getName(), treeDocument.getChildren().iterator().next().getName());
    }

    @Test
    public void breadcrumps() {
        List<Document> breadcrumbs = treeDocument.getBreadcrumbs();
        assertEquals(3, breadcrumbs.size());
        assertEquals(root, breadcrumbs.get(0));
        assertEquals(parent, breadcrumbs.get(1));
        assertEquals(treeDocument, breadcrumbs.get(2));
    }

    @Test
    public void path() {
        assertEquals("/" + parent.getName() + "/" + treeDocument.getName() + "/", treeDocument.getPath());
        treeDocument.setFolder(false);
        assertEquals("/" + parent.getName() + "/" + treeDocument.getName(), treeDocument.getPath());
    }

    @Test
    public void removeChild() {
        treeDocument.removeChild(parent);
        assertEquals(1, treeDocument.getChildren().size());

        treeDocument.removeChild(child);
        assertEquals(0, treeDocument.getChildren().size());
    }

    @Test
    public void childSorting() {
        TreeDocument xFolder = new TreeDocument(new DatabaseDocument(4, "x-folder", true));
        TreeDocument xChild = new TreeDocument(new DatabaseDocument(5, "x-child", false));
        TreeDocument anotherChild = new TreeDocument(new DatabaseDocument(6, "another child", false));

        treeDocument.addChild(xFolder);
        treeDocument.addChild(xChild);
        treeDocument.addChild(anotherChild);

        List<Document> children = new ArrayList<Document>(treeDocument.getChildren());
        assertArrayEquals(new Document[]{xFolder, anotherChild, child, xChild}, children.toArray());
    }
}
