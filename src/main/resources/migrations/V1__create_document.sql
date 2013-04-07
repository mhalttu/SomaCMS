CREATE TABLE document (
  id SERIAL,
  name varchar(256),
  size int,
  parent_id int,
  mime_type varchar(64),
  folder boolean,
  created timestamp DEFAULT CURRENT_TIMESTAMP,
  modified timestamp,
  PRIMARY KEY (id),
  UNIQUE (parent_id, name),
  FOREIGN KEY (parent_id) REFERENCES document (id)
);