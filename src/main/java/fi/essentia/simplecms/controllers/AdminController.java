package fi.essentia.simplecms.controllers;

import fi.essentia.simplecms.dao.DataDao;
import fi.essentia.simplecms.tree.DocumentManager;
import fi.essentia.simplecms.tree.TreeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Markus Halttunen
 */
@Controller
@RequestMapping(value="/admin")
public class AdminController {
    public static final String SUCCESS = "{\"success\":true}";
    @Autowired private DocumentManager documentManager;
    @Autowired private DataDao dataDao;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String admin() {
        return "redirect:/admin/document/0";
    }

    @RequestMapping(value="/document/{id}", method=RequestMethod.GET)
    public String showFolder(@PathVariable Long id, Model model) {
        TreeDocument document = documentManager.documentById(id);
        model.addAttribute("document", document);
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

    @RequestMapping(value="/document/{parentId}/folders", method=RequestMethod.POST)
    public @ResponseBody String folders(@PathVariable Long parentId, @RequestParam("name") String name) {
        documentManager.createFolder(parentId, name);
        return SUCCESS;
    }

    @RequestMapping(value="/document/{parentId}/files", method=RequestMethod.POST)
    public @ResponseBody String uploadFile(@PathVariable Long parentId, @RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        documentManager.createDocument(parentId, file);
        return SUCCESS;
    }

    @RequestMapping(value="/document/{documentId}", method=RequestMethod.PUT)
    public @ResponseBody String saveTextDocument(@PathVariable Long documentId, @RequestParam("contents") String contents) {
        dataDao.updateData(documentId, contents.getBytes());
        return SUCCESS;
    }

    @RequestMapping(value="/document/{documentId}", method=RequestMethod.DELETE)
    public @ResponseBody String delete(@PathVariable Long documentId) {
        documentManager.deleteDocument(documentId);
        return SUCCESS;
    }
}