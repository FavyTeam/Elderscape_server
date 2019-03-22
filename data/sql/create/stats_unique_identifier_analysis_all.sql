CREATE TABLE IF NOT EXISTS `stats_unique_identifier_analysis_all` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` varchar(45) DEFAULT NULL,
  `username` varchar(12) DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `os` varchar(255) DEFAULT NULL,
  `serial` varchar(255) DEFAULT NULL,
  `windows_uid_basic` varchar(255) DEFAULT NULL,
  `windows_sn_different` varchar(255) DEFAULT NULL,
  `base_board_serial_id` varchar(255) DEFAULT NULL,
  `hard_disk_serial` text,
  `file_store_uuid` text,
  `uuids` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=latin1;