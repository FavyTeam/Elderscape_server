CREATE TABLE IF NOT EXISTS `stats_double_items_npc_daily` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` varchar(45) DEFAULT NULL,
  `sunk` varchar(45) DEFAULT NULL,
  `sunk_raw` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;