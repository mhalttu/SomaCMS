This is a super simple "CMS". It supports uploading, modifying and requesting files such as images, CSS and HTML. This
is the first version that still lacks some key features.

Setting up the database:
```mysql
CREATE DATABASE simplecms;
GRANT ALL ON simplecms.* TO 'simplecms'@'localhost' IDENTIFIED BY 'simplecms';
```

Backlog in a Priority Order
# Simple authentication using Spring Security
# Usability: Navigate to a file by name
# Usability: Show the current path as "bread crumps"
# Usability: Saving a document without leaving the page
# Usability: Upload progress bar

Nice to Have
# One page application e.g. using AngularJS
# Better user management
# Document version history
