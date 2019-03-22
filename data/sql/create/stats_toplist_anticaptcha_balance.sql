CREATE TABLE IF NOT EXISTS `stats_toplist_anticaptcha_balance` (
  `token` varchar(255) NOT NULL,
  `balance` int(11) NOT NULL,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;