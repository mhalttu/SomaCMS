package fi.essentia.simplecms.controllers;

import fi.essentia.simplecms.dao.DataDao;
import fi.essentia.simplecms.tree.DocumentManager;
import fi.essentia.simplecms.tree.TreeDocument;
import fi.essentia.simplecms.util.ArchiveHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(method=RequestMethod.GET)
public class LoginController {
    @RequestMapping(value="/login/")
    public String login() {
        return "login";
    }

    @RequestMapping(value="/login/failed")
    public String loginError(Model model) {
        model.addAttribute("failed", true);
        return "login";
    }

    @RequestMapping(value="/logout/")
    public String logout(Model model) {
        model.addAttribute("logout", true);
        return "login";
    }
}