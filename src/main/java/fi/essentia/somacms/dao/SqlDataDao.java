package fi.essentia.somacms.dao;

import fi.essentia.somacms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * SQL based implementation for storing the document contents
 */
@Component
public class SqlDataDao implements DataDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertData(long documentId, byte[] data) {
        jdbcTemplate.update("INSERT INTO document_data (document_id, data) VALUES(?, ?)", documentId, data);
    }

    @Override
    public void updateData(long documentId, byte[] data) {
        jdbcTemplate.update("UPDATE document_data SET data=? WHERE document_id=?", data, documentId);
    }

    @Override
    public byte[] loadData(long documentId) {
        return jdbcTemplate.queryForObject("SELECT data FROM document_data WHERE document_id=?", byte[].class, documentId);
    }
}
