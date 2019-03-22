CREATE TABLE IF NOT EXISTS `stats_payment_daily_total` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` varchar(255) NOT NULL,
  `total` int(11) NOT NULL,
  `bmt_total` int(11) NOT NULL,
  `osrs_total` int(11) NOT NULL,
  `custom_total` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;