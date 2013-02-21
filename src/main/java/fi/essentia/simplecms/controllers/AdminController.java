package fi.essentia.simplecms.controllers;

import fi.essentia.simplecms.dao.DocumentDao;
import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Markus Halttunen
 */
@Controller
@RequestMapping(value="/admin")
public class AdminController {
    @Autowired private DocumentDao documentDao;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String listFiles(Model model) {
        return listFiles(null, model);
    }

    @RequestMapping(value="/folder/{id}", method=RequestMethod.GET)
    public String listFiles(@PathVariable Long id, Model model) {
        List<Document> documents = documentDao.findByParentId(id);
        model.addAttribute("documents", documents);
        return "admin";
    }

    @RequestMapping(value="/new", method=RequestMethod.POST)
    public String newFolder(@RequestParam("name") String name, Model model) {
        return newFolder(null, name, model);
    }

    @RequestMapping(value="/folder/{parentId}/new", method=RequestMethod.POST)
    public String newFolder(@PathVariable Long parentId, @RequestParam("name") String name, Model model) {
        Document document = new Document();
        document.setName(name);
        document.setFolder(true);
        document.setParentId(parentId);
        documentDao.save(document);
        return listFiles(parentId, model);
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam(value="qqfile", required=true) MultipartFile file) throws IOException {
        //fileManager.save(file);
        return "{success:\"true\"}";
    }
}