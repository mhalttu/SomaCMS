package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
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
    public Document findById(long id, boolean includeData) {
        StringBuilder sql = new StringBuilder("SELECT id, name, size, parent_id, mime_type, folder, created, modified");
        if (includeData) {
            sql.append(", data");
        }
        sql.append(" FROM document WHERE id=?");
        return jdbcTemplate.queryForObject(sql.toString(), new DocumentMapper(includeData), id);
    }

    @Override
    public long save(Document document) {
        return insertDocument.executeAndReturnKey(new BeanPropertySqlParameterSource(document)).longValue();
    }

    @Override
    public List<Document> findByParentId(Long parentId) {
        return jdbcTemplate.queryForList("SELECT id, name, size, parent_id, mime_type, folder, created, modified FROM document WHERE parent_id=?", Document.class, parentId);
    }

    private static class DocumentMapper implements RowMapper<Document> {
        private final boolean includeData;

        public DocumentMapper(boolean includeData) {
            this.includeData = includeData;
        }

        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setId(rs.getLong("id"));
            document.setName(rs.getString("name"));
            document.setSize(rs.getLong("size"));
            document.setParentId(rs.getLong("parent_id"));
            document.setMimeType(rs.getString("mime_type"));
            document.setFolder(rs.getBoolean("folder"));
            document.setCreated(rs.getDate("created"));
            document.setModified(rs.getDate("modified"));
            if (includeData) {
                document.setData(rs.getBlob("data"));
            }
            return document;
        }
    }
}
