CREATE TABLE IF NOT EXISTS `client_errors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `error` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `last_username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
