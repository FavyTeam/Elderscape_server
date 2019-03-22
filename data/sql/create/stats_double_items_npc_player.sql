CREATE TABLE IF NOT EXISTS `stats_double_items_npc_player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `wealth` varchar(255) DEFAULT NULL,
  `wealth_raw` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;