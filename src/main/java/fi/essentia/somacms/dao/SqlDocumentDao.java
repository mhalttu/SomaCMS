package fi.essentia.somacms.dao;

import fi.essentia.somacms.models.DatabaseDocument;
import fi.essentia.somacms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SQL based implementation for storing the metadata
 */
@Repository
public class SqlDocumentDao implements DocumentDao {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertDocument;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        insertDocument = new SimpleJdbcInsert(dataSource).withTableName("document").usingGeneratedKeyColumns("id");
    }

    @Override
    public DatabaseDocument findById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM document WHERE id=?", new BeanPropertyRowMapper<DatabaseDocument>(), id);
    }

    @Override
    public long save(DatabaseDocument databaseDocument) {
        Number key = insertDocument.executeAndReturnKey(new BeanPropertySqlParameterSource(databaseDocument));
        databaseDocument.setId(key.longValue());
        return databaseDocument.getId();
    }

    @Override
    public void update(Document document) {
        jdbcTemplate.update("UPDATE document SET name=?, size=?, parent_id=?, mime_type=?, folder=?, created=?, modified=? WHERE id=?",
                document.getName(), document.getSize(), document.getParentId(), document.getMimeType(), document.isFolder(), document.getCreated(), document.getModified(),
                document.getId());
    }

    @Override
    public List<DatabaseDocument> findByParentId(Long parentId) {
        if (parentId == null) {
            return jdbcTemplate.query("SELECT * FROM document WHERE parent_id IS NULL", BeanPropertyRowMapper.newInstance(DatabaseDocument.class));
        } else {
            return jdbcTemplate.query("SELECT * FROM document WHERE parent_id=?", BeanPropertyRowMapper.newInstance(DatabaseDocument.class), parentId);
        }
    }

    @Override
    public List<DatabaseDocument> findAll() {
        return jdbcTemplate.query("SELECT * FROM document", BeanPropertyRowMapper.newInstance(DatabaseDocument.class));
    }

    @Override public void deleteById(Long documentId) {
        jdbcTemplate.update("DELETE FROM document WHERE id=?", documentId);
    }
}
