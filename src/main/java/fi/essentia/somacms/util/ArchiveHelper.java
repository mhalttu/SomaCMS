package fi.essentia.somacms.util;

import fi.essentia.somacms.tree.DocumentManager;
import fi.essentia.somacms.tree.TreeDocument;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Stores multiple documents to the given folder by reading their contents from a ZIP archive
 */
@Component
public class ArchiveHelper {
    @Autowired private DocumentManager documentManager;

    public void storeDocuments(TreeDocument targetFolder, byte[] bytes) throws IOException {
        List<DocumentEntry> entries = new ArrayList<DocumentEntry>();
        ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry zipEntry = in.getNextEntry();
        while(zipEntry!=null){
            File file = new File(zipEntry.getName());
            if (zipEntry.isDirectory()) {
                entries.add(new DocumentEntry(true, file.getParent(), file.getName(), null));
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                IOUtils.copy(in, out);
                out.close();
                entries.add(new DocumentEntry(false, file.getParent(), file.getName(), out.toByteArray()));
            }
            zipEntry = in.getNextEntry();
        }
        in.closeEntry();
        in.close();

        Collections.sort(entries);
        for (DocumentEntry entry : entries) {
            TreeDocument parent;
            if (entry.path == null) {
                parent = documentManager.documentById(targetFolder.getId());
                if (parent == null) {
                    throw new RuntimeException("Failed to find the target folder");
                }
            } else {
                parent = findParent(targetFolder, entry.path);
            }
            if (parent == null) {
                System.out.println("parent = " + null);
            } else {
                System.out.println("parent.getPath() = " + parent.getPath());
            }
            System.out.println("entry.path = " + entry.path);
            if (parent == null) {
                throw new NullPointerException("Failed to find the parent folder for " + entry.name);
            }

            if (entry.folder) {
                if (parent.childByName(entry.name) == null) {
                    documentManager.createFolder(parent.getId(), entry.name);
                }
            } else {
                documentManager.storeDocument(parent.getId(), entry.name, entry.data);
            }
        }
    }

    public byte[] databaseAsArchive(TreeDocument root) {
        // called by AdminController that has e.g. a method databaseAsArchive() with no parameters except HttpResponse (or perhaps documentId)
        // Initialize the ZipOutputStream or similar
        // Iterate through the children recursively starting from root
        // Write the contents of the document as zip entries into the stream
        // Finally return the byte[] contents of the stream
        return null;
    }


    private TreeDocument findParent(TreeDocument targetFolder, String filePath) {
        if (targetFolder.isRoot()) {
            return documentManager.documentFromPath(filePath);
        } else {
            String rootPath = targetFolder.getPath().substring(1);
            return documentManager.documentFromPath(rootPath + filePath);
        }
    }

    /**
     * Sometimes an archive may first contain the file definition and the folder only afterwards so we'll need to
     * sort them.
     */
    private static class DocumentEntry implements Comparable<DocumentEntry> {
        private boolean folder;
        private String path;
        private String name;
        private byte[] data;

        private DocumentEntry(boolean folder, String path, String name, byte[] data) {
            this.folder = folder;
            this.path = path;
            this.name = name;
            this.data = data;
        }

        @Override
        public int compareTo(DocumentEntry other) {
            if (folder) {
                if (!other.folder) {
                    return -1;
                }
            } else if (other.folder) {
                return 1;
            }

            int pathDiff = pathLength() - other.pathLength();
            if (pathDiff != 0) {
                return pathDiff;
            }

            return name.compareTo(other.name);
        }

        private int pathLength() {
            if (path == null) {
                return 0;
            }
            return path.split("/").length;
        }
    }
}
