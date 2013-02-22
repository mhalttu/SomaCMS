package fi.essentia.simplecms.controllers;

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
    @Autowired private DocumentManager documentManager;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String admin() {
        return "redirect:/admin/folder/0/";
    }

    @RequestMapping(value="/folder/{id}/", method=RequestMethod.GET)
    public String showFolder(@PathVariable Long id, Model model) {
        TreeDocument document = documentManager.documentById(id);
        if (!document.isFolder()) {
            throw new ResourceNotFoundException();
        }
        model.addAttribute("document", document);
        return "admin";
    }

    @RequestMapping(value="/folder/{parentId}/new", method=RequestMethod.POST)
    public @ResponseBody String newFolder(@PathVariable Long parentId, @RequestParam("name") String name) {
        documentManager.createChildFolder(parentId, name);
        return "success";
    }

    @RequestMapping(value="/folder/{parentId}/upload", method=RequestMethod.POST)
    public @ResponseBody String uploadFile(@PathVariable Long parentId, @RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        documentManager.saveFile(parentId, file);
        return "{\"success\":true}";
    }
}