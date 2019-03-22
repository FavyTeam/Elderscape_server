CREATE TABLE IF NOT EXISTS `stats_player_online` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` varchar(255) NOT NULL,
  `online_count` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5801 DEFAULT CHARSET=latin1;