CREATE TABLE IF NOT EXISTS`osrs_bot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` varchar(255) NOT NULL,
  `player_name` varchar(255) NOT NULL,
  `player_ip` varchar(255) NOT NULL,
  `player_uid` varchar(255) NOT NULL,
  `rsn` varchar(255) NOT NULL,
  `millions_received` decimal(4,1) NOT NULL DEFAULT '0.0',
  `failed_collection` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
