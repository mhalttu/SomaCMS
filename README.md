SimpleCMS (Java)
=========

Yet another CMS. Why?
---------------------
I needed to find the simplest possible CMS running on the Java stack. There are lot of simple solutions e.g. for PHP
but I couldn't find anything simple enough for Java.

I wanted to teach myself web development so this project was as good an excuse as any.

What is it?
-----------
It's almost misleading to call SimpleCMS a content management system. You can upload images and text documents. You can
edit the text documents using an in-line editor. Finally, you can view those documents in the browser.

Core Values
-----------
1. Simplicity
2. Usability

Setup
-----
1. Setup the database
```mysql
CREATE DATABASE simplecms;
GRANT ALL ON simplecms.* TO 'simplecms'@'localhost' IDENTIFIED BY 'simplecms';
```
2. Run locally: mvn jetty-run
3. Navigate to http://localhost:8080/admin/

TODO
----
1. Javadoc
2. Saving document without leaving the page
3. Uploading a new version of the same file

Improvement Ideas
-----------------
* One page application e.g. using AngularJS
* Better user management
* Version history for the documents
* Usability: Show newly uploaded documents (or added folders) with an animation
