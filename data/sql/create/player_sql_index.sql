CREATE TABLE IF NOT EXISTS `player_sql_index` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `latest_index_taken` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
