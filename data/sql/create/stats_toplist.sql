CREATE TABLE IF NOT EXISTS `stats_toplist` (
  `toplist` varchar(45) NOT NULL,
  `position` int(11) NOT NULL,
  `votes` int(11) NOT NULL,
  `ahead_by_votes` int(11) NOT NULL,
  `behind_by_votes` int(11) NOT NULL,
  `top_server_votes` int(11) NOT NULL,
  `notes` varchar(255) NOT NULL COMMENT 'For example if Dawntained was not found.'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;