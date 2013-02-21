TODO
# CSS styling
# Spring Security
# Open a file
# Replace image
# Create new directory
# Upload file
# Edit document
# Save document
# Minify fine uploader


create table document (id int not null auto_increment primary key, name varchar(256), size int, parent_id int, created timestamp default now(), modified timestamp, foreign key (parent_id) references document(id));