CREATE TABLE `document` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256),
  `size` int(11),
  `parent_id` int(11),
  `mime_type` varchar(64),
  `folder` int(1),
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp,
  `data` blob,
  PRIMARY KEY (`id`),
  UNIQUE (`name`),
  FOREIGN KEY (`parent_id`) REFERENCES `document` (`id`)
) AUTO_INCREMENT=1