CREATE TABLE IF NOT EXISTS `stats_double_items_npc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `amount_sunk_from_economy` bigint(20) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `amount_sunk_from_economy_string` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=latin1;