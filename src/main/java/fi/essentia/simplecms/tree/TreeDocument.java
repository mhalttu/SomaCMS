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
    @Delegate(excludes = ParentId.class) private final DatabaseDocument databaseDocument;
    @Getter @Setter private TreeDocument parent;
    private Map<String, TreeDocument> nameToChild = new TreeMap<String, TreeDocument>();

    public TreeDocument(DatabaseDocument databaseDocument) {
        this.databaseDocument = databaseDocument;
    }

    public void addChild(TreeDocument document) {
        nameToChild.put(document.getName(), document);
    }

    public TreeDocument childByName(String name) {
        return nameToChild.get(name);
    }

    public Collection<TreeDocument> getChildren() {
        return Collections.unmodifiableCollection(nameToChild.values());
    }

    public Long getParentId() {
        Long parentId = databaseDocument.getParentId();
        if (parentId == null) {
            return 0L;
        } else {
            return parentId;
        }
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
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("text/");
    }

    public boolean isViewable() {
        return isFolder() || isImage() || isText();
    }

    public boolean isRoot() {
        return parent == null;
    }

    private interface ParentId {
        Long getParentId();
    }

    public Document getShallowCopy() {
        return new ImmutableDocument(databaseDocument);
    }
}
