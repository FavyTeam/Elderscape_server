CREATE TABLE IF NOT EXISTS `output_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time_of_output` bigint(64) DEFAULT NULL,
  `date_and_time` varchar(128) DEFAULT NULL,
  `content` longtext,
  `content_type` enum('OUTPUT','ERROR') DEFAULT 'OUTPUT',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
