package fi.essentia.simplecms.dao;

import fi.essentia.simplecms.models.DatabaseDocument;
import fi.essentia.simplecms.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
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
 *
 */
@Repository
public class SqlDocumentDao implements DocumentDao {
    private static final String FIELDS = "id, NAME, size, parent_id, mime_type, folder, created, modified";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertDocument;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        insertDocument = new SimpleJdbcInsert(dataSource).withTableName("document").usingGeneratedKeyColumns("id");
    }

    @Override
    public DatabaseDocument findById(long id, Data data) {
        StringBuilder sql = new StringBuilder("SELECT ").append(FIELDS);
        if (data == Data.INCLUDE) {
            sql.append(", data");
        }
        sql.append(" FROM document WHERE id=?");
        return jdbcTemplate.queryForObject(sql.toString(), new DocumentMapper(data), id);
    }

    @Override
    public long save(DatabaseDocument databaseDocument) {
        Number key = insertDocument.executeAndReturnKey(new BeanPropertySqlParameterSource(databaseDocument));
        databaseDocument.setId(key.longValue());
        return databaseDocument.getId();
    }

    @Override
    public List<DatabaseDocument> findByParentId(Long parentId) {
        StringBuilder sql = new StringBuilder("SELECT ").append(FIELDS).append(" FROM document WHERE ");
        if (parentId == null) {
            sql.append("parent_id is null");
            return jdbcTemplate.query(sql.toString(), new DocumentMapper(Data.EXCLUDE));
        } else {
            sql.append("parent_id=?");
            return jdbcTemplate.query(sql.toString(), new DocumentMapper(Data.EXCLUDE));
        }
    }

    @Override
    public List<DatabaseDocument> findAll() {
        return jdbcTemplate.query("SELECT " + FIELDS + " FROM document", new DocumentMapper(Data.EXCLUDE));
    }

    public void loadData(Document document) {
        Blob blob = jdbcTemplate.queryForObject("SELECT data FROM document WHERE id=?", Blob.class, document.getId());
        try {
            document.setData(blob.getBytes(1, (int)blob.length()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DocumentMapper implements RowMapper<DatabaseDocument> {
        private final Data data;

        public DocumentMapper(Data data) {
            this.data = data;
        }

        public DatabaseDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
            DatabaseDocument databaseDocument = new DatabaseDocument();
            databaseDocument.setId(rs.getLong("id"));
            databaseDocument.setName(rs.getString("name"));
            databaseDocument.setSize(rs.getLong("size"));
            long parentId = rs.getLong("parent_id");
            if (!rs.wasNull()) {
                databaseDocument.setParentId(parentId);
            }
            databaseDocument.setMimeType(rs.getString("mime_type"));
            databaseDocument.setFolder(rs.getBoolean("folder"));
            databaseDocument.setCreated(rs.getDate("created"));
            databaseDocument.setModified(rs.getDate("modified"));
            if (data == Data.INCLUDE) {
                databaseDocument.setData(rs.getBytes("data"));
            }
            return databaseDocument;
        }
    }
}
