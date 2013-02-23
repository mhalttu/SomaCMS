CREATE TABLE `document_data` (
  `document_id` int(11) NOT NULL,
  `data` mediumblob,
  FOREIGN KEY (`document_id`) REFERENCES `document` (`id`)
)