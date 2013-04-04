SimpleCMS (for Java)
=========

Yet another CMS. Why?
---------------------
I needed to find the simplest possible CMS running on the Java stack. There are lot of nice, simple solutions e.g. for
PHP but I couldn't find anything simple enough for Java.

I wanted to learn web development, so this project was as good an excuse as any.

What is SimpleCMS
-----------------
It's almost misleading to call SimpleCMS a content management system. You can upload images and text documents. You can
edit the text documents using an in-line editor. Finally, you can view those documents in the browser.

Core Values
-----------
1. Simplicity
2. Usability

Setup
-----
1. Check the configuration at arc/main/resources/simplecms.properties
2. Setup the database, e.g.
```mysql
CREATE DATABASE simplecms;
GRANT ALL ON simplecms.* TO 'simplecms'@'localhost' IDENTIFIED BY 'simplecms';
```
3. Run locally: mvn jetty-run
4. Navigate to http://localhost:8080/admin/

TODO
----
* Spring Security
* Javadoc
* Saving document without leaving the page
* Uploading a new version of the same file
* Add screenshots here

Things I'm Not Too Proud Of
---------------------------
* No unit tests. Shame on me.
* Some of the Thymeleaf constructs feel a bit awkward
* Too much code duplication on the html pages

Improvement Ideas
-----------------
* Convert it to a single-page application e.g. using AngularJS
* Implement a better user management
* Add version tracking for documents
* Usability: Show newly uploaded documents (or added folders) with an animation
