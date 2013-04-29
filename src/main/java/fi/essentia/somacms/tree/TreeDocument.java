package fi.essentia.somacms.tree;

import com.google.common.collect.Collections2;
import fi.essentia.somacms.models.DatabaseDocument;
import fi.essentia.somacms.models.Document;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Wraps the normal database document better support keeping the document metadata in memory.
 */
public class TreeDocument implements Document {
    public static final long ROOT_ID = 0;

    @Delegate private final DatabaseDocument databaseDocument;
    @Getter @Setter private TreeDocument parent;

    private Map<String, TreeDocument> nameToChild = new HashMap<String, TreeDocument>();
    private SortedSet<TreeDocument> children = new TreeSet<TreeDocument>(new TreeDocumentComparator());

    public TreeDocument(DatabaseDocument databaseDocument) {
        this.databaseDocument = databaseDocument;
    }

    public synchronized void addChild(TreeDocument document) {
        nameToChild.put(document.getName(), document);
        children.add(document);
    }

    public synchronized TreeDocument childByName(String name) {
        return nameToChild.get(name);
    }

    public synchronized Collection<TreeDocument> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public String getPath() {
        List<Document> pathElements = getBreadcrumbs();
        StringBuilder path = new StringBuilder("/");
        for (int i=1; i<pathElements.size()-1; i++) {
            path.append(pathElements.get(i).getName());
            path.append("/");
        }

        if (!isRoot()) {
            path.append(getName());
            if (isFolder()) {
                path.append("/");
            }
        }
        return path.toString();
    }

    public List<Document> getBreadcrumbs() {
        LinkedList<Document> pathElements = new LinkedList<Document>();

        TreeDocument document = this;
        while (!document.isRoot()) {
            pathElements.addFirst(document);
            document = document.getParent();
        }
        pathElements.addFirst(document);
        return pathElements;
    }

    public String getThumbail() {
        if (isFolder()) {
            return "/resources/images/folder.png";
        }
        if (isImage()) {
            return getPath();
        }

        return "/resources/images/document.png";
    }

    public boolean isRoot() {
        return getId() == ROOT_ID;
    }

    public synchronized void removeChild(Document document) {
        TreeDocument removedDocument = nameToChild.remove(document.getName());
        if (removedDocument != null) {
            children.remove(removedDocument);
        }
    }

    public synchronized Document getShallowCopy() {
        if (isFolder()) {
            return new ImmutableFolder(this);
        } else {
            return new ImmutableDocument(databaseDocument);
        }
    }

    private static class TreeDocumentComparator implements Comparator<TreeDocument> {
        @Override
        public int compare(TreeDocument first, TreeDocument second) {
            if (first.isFolder()) {
                if (!second.isFolder()) {
                    return -1;
                } else {
                    return first.getName().compareTo(second.getName());
                }
            } else {
                if (second.isFolder()) {
                    return 1;
                } else {
                    return first.getName().compareTo(second.getName());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TreeDocument(\"" + getName() + "\")";
    }
}
