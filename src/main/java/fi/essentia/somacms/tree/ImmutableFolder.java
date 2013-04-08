package fi.essentia.somacms.tree;

import fi.essentia.somacms.models.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Extension of an ImmutableDocument that contains the immediate children as ImmutableDocuments
 */
public class ImmutableFolder extends ImmutableDocument {
    private Map<Long,ImmutableDocument> children;

    public ImmutableFolder(TreeDocument document) {
        super(document);
        children = new HashMap<Long, ImmutableDocument>();
        for (TreeDocument treeDocument : document.getChildren()) {
            children.put(treeDocument.getId(), new ImmutableDocument(treeDocument));
        }
    }

    public Map<Long, ImmutableDocument> getChildren() {
        return Collections.unmodifiableMap(children);
    }
}
