CREATE TABLE document_data (
  document_id SERIAL,
  data BYTEA,
  FOREIGN KEY (document_id) REFERENCES document (id) ON DELETE CASCADE
)