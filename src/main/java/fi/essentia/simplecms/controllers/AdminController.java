package fi.essentia.simplecms.controllers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fi.essentia.simplecms.dao.DataDao;
import fi.essentia.simplecms.json.*;
import fi.essentia.simplecms.json.Error;
import fi.essentia.simplecms.tree.DocumentManager;
import fi.essentia.simplecms.tree.TreeDocument;
import fi.essentia.simplecms.tree.UnsupportedMimeTypeException;
import fi.essentia.simplecms.util.ArchiveHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

/**
 * All the administration logic for the
 */
@Controller
@RequestMapping(value="/admin/")
@Secured(value = "ROLE_ADMIN")
@Scope("session")
public class AdminController {

    @Autowired private DocumentManager documentManager;
    @Autowired private DataDao dataDao;
    @Autowired private ArchiveHelper archiveHelper;

    String message;

    @RequestMapping(method=RequestMethod.GET)
    public String admin() {
        return "redirect:document/0";
    }

    @RequestMapping(value="/document/{id}", method=RequestMethod.GET)
    public String showFolder(@PathVariable Long id, Model model, WebRequest webRequest) {
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
        TreeDocument folder = documentManager.createFolder(parentId, name);
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
        }
    }

    @RequestMapping(value="/api/document/{parentId}/files", method=RequestMethod.POST)
    public @ResponseBody Result uploadFile(@PathVariable Long parentId, @RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType.equals("application/zip")) {
            byte[] bytes = file.getBytes();
            archiveHelper.storeDocuments(parentId, bytes);
            return Result.success();
        } else {
            TreeDocument treeDocument = documentManager.storeDocument(parentId, file.getOriginalFilename(), file.getBytes());
            return new Created(treeDocument.getId());
        }
    }

    @RequestMapping(value="/api/document/{documentId}", method=RequestMethod.PUT)
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