package fi.essentia.simplecms.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 *
 */
@Component
@RequestMapping(value="/", method= RequestMethod.GET)
public class CmsController {
    /*
    @Autowired private FileManager fileManager;

    @RequestMapping(value="/**", method=RequestMethod.GET)
    public void get(HttpServletResponse response, HttpServletRequest request) {
        String path = null;
        try {
            path = URLDecoder.decode(request.getRequestURI().substring(1), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        FileEntry fileEntry = fileManager.get(path);
        response.setContentType(fileEntry.getMimeType());
        InputStream is = null;
        try {
            is = fileEntry.openStream();
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }
    */
}
