package fi.essentia.simplecms.controllers;

import fi.essentia.simplecms.dao.SqlDocumentDao;
import fi.essentia.simplecms.models.Document;
import fi.essentia.simplecms.tree.DocumentManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
        InputStream is = null;
        try {
            is = document.getData().getBinaryStream();
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
    }
}

