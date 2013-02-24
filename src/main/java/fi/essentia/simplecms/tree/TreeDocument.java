package fi.essentia.simplecms.tree;

import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 *
 */
public class TreeDocument implements Document {
    @Delegate private final DatabaseDocument databaseDocument;
    @Getter @Setter private TreeDocument parent;

    private Map<String, TreeDocument> nameToChild = new HashMap<String, TreeDocument>();
    private SortedSet<TreeDocument> children = new TreeSet<TreeDocument>(new TreeDocumentComparator());

    public TreeDocument(DatabaseDocument databaseDocument) {
        this.databaseDocument = databaseDocument;
    }

    public void addChild(TreeDocument document) {
        nameToChild.put(document.getName(), document);
        children.add(document);
    }

    public TreeDocument childByName(String name) {
        return nameToChild.get(name);
    }

    public Collection<TreeDocument> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public String getPath() {
        LinkedList<String> pathElements = new LinkedList<String>();
        TreeDocument document = this;
        while (!document.isRoot()) {
            pathElements.addFirst(document.getName());
            document = document.getParent();
        }
        return "/" + StringUtils.join(pathElements, "/");
    }

    public String getThumbail() {
        if (isFolder()) {
            return "/resources/folder.png";
        }
        if (isImage()) {
            return getPath();
        }

        return "/resources/document.png";
    }

    public boolean isImage() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isText() {
        if (isFolder()) {
            return false;
        }

        String mimeType = getMimeType();
        return mimeType.startsWith("text/") || mimeType.equals("application/xml") || mimeType.equals("application/xhtml+xml");
    }

    public boolean isViewable() {
        return isFolder() || isImage() || isText();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void removeChild(Document document) {
        TreeDocument removedDocument = nameToChild.remove(document.getName());
        children.remove(removedDocument);
    }

    public Document getShallowCopy() {
        return new ImmutableDocument(databaseDocument);
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
}
