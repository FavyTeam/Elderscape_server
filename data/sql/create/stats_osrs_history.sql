CREATE TABLE IF NOT EXISTS `stats_osrs_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` varchar(45) DEFAULT NULL,
  `entity` varchar(45) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `total_expected` decimal(6,1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;