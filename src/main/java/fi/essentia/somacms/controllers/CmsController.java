package fi.essentia.somacms.controllers;

import fi.essentia.somacms.dao.DataDao;
import fi.essentia.somacms.dao.SqlDocumentDao;
import fi.essentia.somacms.models.Document;
import fi.essentia.somacms.tree.DocumentManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;

/**
 * Exposes the contents of the CMS via normal web requests
 */
@Component
@RequestMapping(value="/", method= RequestMethod.GET)
public class CmsController {
    @Autowired private DocumentManager documentManager;
    @Autowired private DataDao dataDao;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String get() {
        return "index";
    }

    @RequestMapping(value="/**", method=RequestMethod.GET)
    public void get(HttpServletResponse response, HttpServletRequest request, WebRequest webRequest) throws SQLException, IOException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String resourcePath = requestURI.substring(contextPath.length()+1);
        String path = URLDecoder.decode(resourcePath, "UTF8");
        Document document = documentManager.documentFromPath(path);
        if (document == null) {
            throw new ResourceNotFoundException();
        }
        if (document.isFolder()) {
            throw new UnauthorizedException();
        }
        if (webRequest.checkNotModified(document.getModified().getTime())) {
            return;
        }

        byte[] bytes = dataDao.loadData(document.getId());
        response.setContentType(document.getMimeType());
        IOUtils.write(bytes, response.getOutputStream());
        response.flushBuffer();
    }
}

