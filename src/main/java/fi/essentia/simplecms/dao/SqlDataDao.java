package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Blob;
import java.sql.SQLException;

/**
 *
 */
@Component
public class SqlDataDao implements DataDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveDataForDocument(long documentId, byte[] data) {
        jdbcTemplate.update("INSERT INTO document_data (document_id, data) VALUES(?, ?)", documentId, data);
    }

    @Override
    public byte[] loadDataForDocument(long documentId) {
        Blob blob = jdbcTemplate.queryForObject("SELECT data FROM document_data WHERE document_id=?", Blob.class, documentId);
        try {
            return blob.getBytes(1, (int)blob.length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
