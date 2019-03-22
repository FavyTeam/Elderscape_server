CREATE TABLE IF NOT EXISTS `stats_console_print_crash` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` varchar(255) NOT NULL,
  `print` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;