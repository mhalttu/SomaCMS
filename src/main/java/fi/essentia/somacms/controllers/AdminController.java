package fi.essentia.somacms.controllers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import fi.essentia.somacms.dao.DataDao;
import fi.essentia.somacms.json.*;
import fi.essentia.somacms.json.Error;
import fi.essentia.somacms.tree.DocumentManager;
import fi.essentia.somacms.tree.TreeDocument;
import fi.essentia.somacms.tree.UnsupportedMimeTypeException;
import fi.essentia.somacms.util.ArchiveHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Takes care of all the administration tasks
 */
@Controller
@RequestMapping(value="/admin/")
@Secured(value = "ROLE_ADMIN")
@Scope("session")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired private DocumentManager documentManager;
    @Autowired private DataDao dataDao;
    @Autowired private ArchiveHelper archiveHelper;

    String message;

    @RequestMapping(method=RequestMethod.GET)
    public String admin() {
        return "redirect:document/0";
    }

    @RequestMapping(value="/document/{id}", method=RequestMethod.GET)
    public String showDocument(@PathVariable Long id, Model model, WebRequest webRequest) {
        TreeDocument document = documentManager.documentById(id);
        if (document == null) {
            throw new ResourceNotFoundException();
        }
        model.addAttribute("contextPath", webRequest.getContextPath());
        model.addAttribute("document", document);
        if (message != null) {
            model.addAttribute("message", message);
            message = null;
        }
        if (document.isFolder()) {
            return "folder";
        } else if (document.isImage()) {
            return "image";
        } else if (document.isText()) {
            byte[] bytes = dataDao.loadData(document.getId());
            model.addAttribute("documentText", new String(bytes));
            return "text";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value="/api/document/{parentId}/folders", method=RequestMethod.POST)
    public @ResponseBody Result createFolder(@PathVariable Long parentId, @RequestParam("name") String name) {
        TreeDocument folder;
        try {
            folder = documentManager.createFolder(parentId, name);
        } catch (DuplicateKeyException e) {
            return new Error("There is already a document with the same name.");
        }
        message = "Folder <b>" + name + "</b> created";
        return new Created(folder.getId());
    }

    @RequestMapping(value="/api/document/{parentId}/documents", method=RequestMethod.POST)
    public @ResponseBody Result createTextFile(@PathVariable Long parentId, @RequestParam("name") String name) {
        try {
            TreeDocument testFile = documentManager.createTextFile(parentId, name);
            return new Created(testFile.getId());
        } catch (UnsupportedMimeTypeException e) {
            return new Error("The file doesn't seem to be a text document.");
        } catch (DuplicateKeyException e) {
            return new Error("There is already a document with the same name.");
        }
    }

    @RequestMapping(value="/api/document/{parentId}/files", method=RequestMethod.POST)
    public @ResponseBody Result uploadFile(@PathVariable Long parentId, @RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        try {
            String contentType = file.getContentType();
            if (contentType.equals("application/zip")) {
                byte[] bytes = file.getBytes();
                archiveHelper.storeDocuments(parentId, bytes);
                return Result.success();
            } else {
                TreeDocument treeDocument = documentManager.storeDocument(parentId, file.getOriginalFilename(), file.getBytes());
                return new Created(treeDocument.getId());
            }
        } catch (RuntimeException e) {
            logger.error("Upload of " + file.getOriginalFilename() + " failed", e);
            return new Error(e.getMessage());
        }
    }

    @RequestMapping(value="/api/document/{documentId}/replace", method=RequestMethod.POST)
    public @ResponseBody Result replace(@PathVariable Long documentId, @RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        try {
            String contentType = file.getContentType();
            if (contentType.equals("application/zip")) {
                return new Error("Updating archives is not supported");
            } else {
                TreeDocument document = documentManager.documentById(documentId);
                String fileName = file.getOriginalFilename();
                if (!document.getName().equals(fileName)) {
                    throw new RuntimeException("Received upload of " + fileName + " that was trying to replace " + document.getName());
                }

                documentManager.storeDocument(document.getParentId(), fileName, file.getBytes());
                message = "File Updated";
                return Result.success();
            }
        } catch (RuntimeException e) {
            logger.error("Update of " + file.getOriginalFilename() + " failed", e);
            return new Error(e.getMessage());
        }
    }


    @RequestMapping(value="/api/document/{documentId}/save", method=RequestMethod.PUT)
    public @ResponseBody Result saveTextDocument(@PathVariable Long documentId, @RequestBody String contents) {
        dataDao.updateData(documentId, contents.getBytes());
        return Result.success();
    }

    @RequestMapping(value="/api/document/{documentId}", method=RequestMethod.DELETE)
    public @ResponseBody Result delete(@PathVariable Long documentId) {
        TreeDocument treeDocument = documentManager.deleteDocument(documentId);
        message = (treeDocument.isFolder() ? "Folder " : "Document") + " <b>" + treeDocument.getName() + "</b> deleted.";
        return Result.success();
    }

    @RequestMapping(value= "/api/search/", method = RequestMethod.GET)
    public @ResponseBody Collection<SearchResult> listDocuments(@RequestParam(value = "query") String query) {
        Collection<TreeDocument> treeDocuments = documentManager.documentsByPath(query);
        return Collections2.transform(treeDocuments, new Function<TreeDocument, SearchResult>() {
            @Override
            public SearchResult apply(TreeDocument treeDocument) {
                return new SearchResult(treeDocument.getId(), treeDocument.getPath());
            }
        });
    }
}