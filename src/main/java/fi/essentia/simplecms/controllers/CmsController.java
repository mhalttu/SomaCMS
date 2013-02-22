package fi.essentia.simplecms.controllers;

import fi.essentia.simplecms.dao.SqlDocumentDao;
import fi.essentia.simplecms.models.Document;
import fi.essentia.simplecms.tree.DocumentManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;

/**
 *
 */
@Component
@RequestMapping(value="/", method= RequestMethod.GET)
public class CmsController {
    @Autowired private SqlDocumentDao documentDao;
    @Autowired private DocumentManager documentManager;

    @RequestMapping(value="/**", method=RequestMethod.GET)
    public void get(HttpServletResponse response, HttpServletRequest request) throws SQLException, IOException {
        String path = URLDecoder.decode(request.getRequestURI().substring(1), "UTF8");
        Document document = documentManager.documentFromPath(path);
        if (document == null) {
            throw new ResourceNotFoundException();
        }
        if (document.isFolder()) {
            throw new UnauthorizedException();
        }

        documentDao.loadData(document);
        response.setContentType(document.getMimeType());
        IOUtils.write(document.getData(), response.getOutputStream());
        response.flushBuffer();
    }
}

